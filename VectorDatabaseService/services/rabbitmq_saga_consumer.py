import json
import logging
import threading
import time

import pika

from config import (
    AD_CREATED_KEY,
    AD_CREATION_FAILED_KEY,
    AD_TYPE_READY_KEY,
    AD_TYPE_READY_QUEUE,
    RABBITMQ_EXCHANGE,
    RABBITMQ_HOST,
    RABBITMQ_PASSWORD,
    RABBITMQ_PORT,
    RABBITMQ_USERNAME,
)
from model.oglas import OglasCreate
from service.impl.oglas_service import oglas_service

logger = logging.getLogger(__name__)


class RabbitMqSagaConsumer:
    def __init__(self):
        self._stop_event = threading.Event()
        self._thread = None
        self._connection = None
        self._channel = None

    def start_in_background(self):
        if self._thread and self._thread.is_alive():
            return

        self._stop_event.clear()
        self._thread = threading.Thread(target=self._run, name="rabbitmq-saga-consumer", daemon=True)
        self._thread.start()

    def stop(self):
        self._stop_event.set()
        if self._connection and self._connection.is_open:
            try:
                self._connection.close()
            except Exception:
                logger.exception("Failed to close RabbitMQ connection cleanly.")

    def _run(self):
        while not self._stop_event.is_set():
            try:
                self._consume_forever()
            except Exception:
                logger.exception("RabbitMQ saga consumer crashed. Retrying in 5 seconds.")
                time.sleep(5)

    def _consume_forever(self):
        credentials = pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
        parameters = pika.ConnectionParameters(
            host=RABBITMQ_HOST,
            port=RABBITMQ_PORT,
            credentials=credentials,
            heartbeat=30,
        )

        self._connection = pika.BlockingConnection(parameters)
        self._channel = self._connection.channel()
        self._channel.exchange_declare(exchange=RABBITMQ_EXCHANGE, exchange_type="topic", durable=True)
        self._channel.queue_declare(queue=AD_TYPE_READY_QUEUE, durable=True)
        self._channel.queue_bind(queue=AD_TYPE_READY_QUEUE, exchange=RABBITMQ_EXCHANGE, routing_key=AD_TYPE_READY_KEY)
        self._channel.basic_qos(prefetch_count=1)
        self._channel.basic_consume(queue=AD_TYPE_READY_QUEUE, on_message_callback=self._handle_message)

        logger.info("RabbitMQ saga consumer is listening on queue '%s'.", AD_TYPE_READY_QUEUE)
        while not self._stop_event.is_set():
            self._connection.process_data_events(time_limit=1)

    def _handle_message(self, channel, method, properties, body):
        event = json.loads(body.decode("utf-8"))
        saga_id = event.get("sagaId")
        ad_type_id = event.get("adTypeId")
        ad_type_created = bool(event.get("adTypeCreatedInSaga"))
        ad_payload = event.get("ad") or {}
        oglas_id = ad_payload.get("oglasId")

        try:
            existing = None
            if oglas_id is not None:
                try:
                    existing = oglas_service.get_oglas(oglas_id)
                except KeyError:
                    existing = None

            if existing:
                if int(existing.get("ad_type_id", 0)) != int(ad_type_id or 0):
                    raise ValueError(
                        f"Oglas sa id={oglas_id} već postoji, ali je vezan za drugi ad_type_id."
                    )
                self._publish(
                    AD_CREATED_KEY,
                    {
                        "sagaId": saga_id,
                        "adTypeId": ad_type_id,
                        "oglasId": oglas_id,
                    },
                )
                logger.info(
                    "[CHOREOGRAPHY][VECTOR] sagaId=%s oglasId=%s already exists, treating event as idempotent success",
                    saga_id,
                    oglas_id,
                )
                channel.basic_ack(delivery_tag=method.delivery_tag)
                return

            payload = OglasCreate(
                oglas_id=oglas_id,
                ad_type_id=ad_payload["adTypeId"],
                naziv=ad_payload["naziv"],
                opis=ad_payload["opis"],
                tip_oglasa=ad_payload["tipOglasa"],
                content_url=ad_payload.get("contentUrl"),
                status=ad_payload["status"],
                kategorija=ad_payload["kategorija"],
                datum_kreiranja=ad_payload["datumKreiranja"],
                datum_poslednje_izmene=ad_payload["datumPoslednjeIzmene"],
                kampanja_id=ad_payload["kampanjaId"],
            )
            oglas_service.create_oglas(payload)
            self._publish(
                AD_CREATED_KEY,
                {
                    "sagaId": saga_id,
                    "adTypeId": ad_type_id,
                    "oglasId": payload.oglas_id,
                },
            )
            logger.info(
                "[CHOREOGRAPHY][VECTOR] sagaId=%s adTypeId=%s oglasId=%s successfully stored in Milvus",
                saga_id,
                ad_type_id,
                payload.oglas_id,
            )
        except Exception as exc:
            logger.exception("[CHOREOGRAPHY][VECTOR] sagaId=%s failed to create ad in Milvus", saga_id)
            self._publish(
                AD_CREATION_FAILED_KEY,
                {
                    "sagaId": saga_id,
                    "adTypeId": ad_type_id,
                    "oglasId": ad_payload.get("oglasId"),
                    "adTypeCreatedInSaga": ad_type_created,
                    "reason": str(exc),
                },
            )
        finally:
            channel.basic_ack(delivery_tag=method.delivery_tag)

    def _publish(self, routing_key: str, payload: dict):
        self._channel.basic_publish(
            exchange=RABBITMQ_EXCHANGE,
            routing_key=routing_key,
            body=json.dumps(payload).encode("utf-8"),
            properties=pika.BasicProperties(content_type="application/json", delivery_mode=2),
        )


rabbitmq_saga_consumer = RabbitMqSagaConsumer()
