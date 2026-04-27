import os

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
