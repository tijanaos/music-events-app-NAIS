import os
from pathlib import Path

# ─────────────────────────────────────────────────────────────────────────────
# Milvus connection
# ─────────────────────────────────────────────────────────────────────────────
MILVUS_HOST = os.getenv("MILVUS_HOST", "standalone")
MILVUS_PORT = int(os.getenv("MILVUS_PORT", "19530"))
MILVUS_URI = f"http://{MILVUS_HOST}:{MILVUS_PORT}"
MILVUS_CONNECT_RETRIES = int(os.getenv("MILVUS_CONNECT_RETRIES", "12"))
MILVUS_CONNECT_DELAY = float(os.getenv("MILVUS_CONNECT_DELAY", "5"))

# ─────────────────────────────────────────────────────────────────────────────
# Embedding model
# Ako koristiš CLIP, i tekst i slike će biti u istoj 512-dimenzionalnoj ravni
# što je zgodno za text/media pretragu i hybrid search.
# ─────────────────────────────────────────────────────────────────────────────
EMBEDDING_MODEL_NAME = "clip-ViT-B-32"
EMBEDDING_DIM = 512


APP_HOST = "0.0.0.0"
APP_PORT = int(os.getenv("APP_PORT", "8000"))
APP_NAME = os.getenv("APP_NAME", "vector-database-service")
APP_INSTANCE_HOST = os.getenv("APP_INSTANCE_HOST", os.getenv("HOSTNAME", ""))
EUREKA_ENABLED = os.getenv("EUREKA_ENABLED", "true").lower() in {
    "1",
    "true",
    "yes",
    "on",
}


EUREKA_SERVER = os.getenv(
    "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
    "http://eureka-server:8761/eureka"
)

# ─────────────────────────────────────────────────────────────────────────────
# Collection names
# ─────────────────────────────────────────────────────────────────────────────
OGLASI_COLLECTION = "oglasi"
KAMPANJE_COLLECTION = "kampanje"

# ─────────────────────────────────────────────────────────────────────────────
# HNSW index params
# ─────────────────────────────────────────────────────────────────────────────
HNSW_M = 16
HNSW_EF_CONSTRUCTION = 200
HNSW_EF_SEARCH = 64

# ─────────────────────────────────────────────────────────────────────────────
# Retrieval settings
# ─────────────────────────────────────────────────────────────────────────────
DEFAULT_TOP_K = 10
MAX_TOP_K = 100

# ─────────────────────────────────────────────────────────────────────────────
# Image download / processing
# ─────────────────────────────────────────────────────────────────────────────
IMAGE_DOWNLOAD_TIMEOUT = 10
MAX_IMAGE_SIZE_PX = 1024
REAL_DATA_ROOT = Path(os.getenv("REAL_DATA_ROOT", "/app/data/real"))

# ─────────────────────────────────────────────────────────────────────────────
# Redis cache
# ─────────────────────────────────────────────────────────────────────────────
REDIS_HOST = os.getenv("REDIS_HOST", "redis")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
REDIS_DB = int(os.getenv("REDIS_DB", "0"))
REDIS_RESPONSE_TTL_SECONDS = int(os.getenv("REDIS_RESPONSE_TTL_SECONDS", "300"))
REDIS_RESPONSE_KEY_PREFIX = os.getenv("REDIS_RESPONSE_KEY_PREFIX", "search-response-cache")

# ─────────────────────────────────────────────────────────────────────────────
# RabbitMQ choreography saga
# ─────────────────────────────────────────────────────────────────────────────
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", os.getenv("SPRING_RABBITMQ_HOST", "localhost"))
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", os.getenv("SPRING_RABBITMQ_PORT", "5672")))
RABBITMQ_USERNAME = os.getenv("RABBITMQ_USERNAME", "guest")
RABBITMQ_PASSWORD = os.getenv("RABBITMQ_PASSWORD", "guest")
RABBITMQ_EXCHANGE = os.getenv("RABBITMQ_EXCHANGE", "ad.saga.choreography.exchange")
AD_TYPE_READY_QUEUE = os.getenv("AD_TYPE_READY_QUEUE", "ad.type.ready.queue")
AD_TYPE_READY_KEY = os.getenv("AD_TYPE_READY_KEY", "ad.type.ready")
AD_CREATED_KEY = os.getenv("AD_CREATED_KEY", "ad.created")
AD_CREATION_FAILED_KEY = os.getenv("AD_CREATION_FAILED_KEY", "ad.creation.failed")
