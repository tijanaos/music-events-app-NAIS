import logging
import time

from pymilvus import MilvusClient
from config import MILVUS_CONNECT_DELAY, MILVUS_CONNECT_RETRIES, MILVUS_URI

logger = logging.getLogger(__name__)


def _connect_with_retry(uri: str, retries: int, delay: float) -> MilvusClient:
    for attempt in range(1, retries + 1):
        try:
            client = MilvusClient(uri=uri)
            client.list_collections()
            logger.info("Connected to Milvus at %s", uri)
            return client
        except Exception as exc:
            logger.warning("Milvus not ready (attempt %d/%d): %s", attempt, retries, exc)
            if attempt == retries:
                raise
            time.sleep(delay)


class MilvusService:
    def __init__(self):
        logger.info("Connecting to Milvus at %s", MILVUS_URI)
        self.client = _connect_with_retry(
            uri=MILVUS_URI,
            retries=MILVUS_CONNECT_RETRIES,
            delay=MILVUS_CONNECT_DELAY,
        )

    # ─────────────────────────────────────────────────────────────
    # COLLECTION MANAGEMENT
    # ─────────────────────────────────────────────────────────────

    def ensure_collection(self, name, schema, index_params):
        if self.client.has_collection(name):
            logger.info("Collection '%s' already exists.", name)
            return

        logger.info("Creating collection '%s'...", name)

        self.client.create_collection(
            collection_name=name,
            schema=schema,
            index_params=index_params,
        )

        logger.info("Collection '%s' created.", name)

    def drop_and_recreate(self, name, schema, index_params):
        if self.client.has_collection(name):
            logger.info("Dropping collection '%s'...", name)
            self.client.drop_collection(name)

        logger.info("Recreating collection '%s'...", name)

        self.client.create_collection(
            collection_name=name,
            schema=schema,
            index_params=index_params,
        )

    def list_collections(self):
        return self.client.list_collections()

    def collection_stats(self, name):
        return self.client.get_collection_stats(collection_name=name)


# Singleton
milvus_service = MilvusService()
