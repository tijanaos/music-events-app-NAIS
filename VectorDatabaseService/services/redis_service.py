import json
import logging
import hashlib
from typing import Optional

import redis

from config import (
    REDIS_DB,
    REDIS_HOST,
    REDIS_PORT,
    REDIS_RESPONSE_KEY_PREFIX,
    REDIS_RESPONSE_TTL_SECONDS,
)

logger = logging.getLogger(__name__)


class RedisService:
    """
    Centralizovan Redis pristup za response cache.

    Struktura je namerno izdvojena u poseban servis, slično primeru iz zip-a,
    kako bi konekcija i rad sa kešom ostali odvojeni od business logike.
    """

    def __init__(self):
        self._client: Optional[redis.Redis] = None

    def _get_client(self) -> Optional[redis.Redis]:
        if self._client is None:
            try:
                self._client = redis.Redis(
                    host=REDIS_HOST,
                    port=REDIS_PORT,
                    db=REDIS_DB,
                    decode_responses=True,
                    socket_connect_timeout=2,
                    socket_timeout=2,
                )
                self._client.ping()
                logger.info("Connected to Redis at %s:%s", REDIS_HOST, REDIS_PORT)
            except redis.RedisError as exc:
                logger.warning("Redis is unavailable, response cache disabled: %s", exc)
                self._client = None

        return self._client

    def make_hash_key(self, value: object) -> str:
        normalized = json.dumps(value, sort_keys=True, ensure_ascii=False)
        return hashlib.sha256(normalized.encode("utf-8")).hexdigest()

    def _make_key(self, namespace: str, key: str) -> str:
        return f"{REDIS_RESPONSE_KEY_PREFIX}:{namespace}:{key}"

    def get_json(self, namespace: str, key: str) -> Optional[object]:
        client = self._get_client()
        if client is None:
            return None

        try:
            cached = client.get(self._make_key(namespace, key))
            return json.loads(cached) if cached else None
        except (redis.RedisError, json.JSONDecodeError) as exc:
            logger.warning("Failed to read cached response from Redis: %s", exc)
            return None

    def set_json(self, namespace: str, key: str, value: object) -> None:
        client = self._get_client()
        if client is None:
            return

        try:
            client.setex(
                self._make_key(namespace, key),
                REDIS_RESPONSE_TTL_SECONDS,
                json.dumps(value),
            )
        except redis.RedisError as exc:
            logger.warning("Failed to write cached response to Redis: %s", exc)


redis_service = RedisService()
