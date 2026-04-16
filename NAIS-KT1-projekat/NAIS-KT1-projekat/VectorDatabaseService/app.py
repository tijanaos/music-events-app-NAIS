"""
VectorDatabaseService - FastAPI application.

Multimodal fashion product search backed by Milvus.
Dataset: ashraq/fashion-product-images-small
Embedding: CLIP ViT-B/32 (512-dim, text and images share the same embedding space)

Run:
    uvicorn app:app --host 0.0.0.0 --port 8000

Swagger UI: http://localhost:8000/docs
ReDoc:      http://localhost:8000/redoc

"""

import logging
import os
import socket

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from config import APP_HOST, APP_PORT, APP_NAME, EUREKA_SERVER
from controller.fashion_controller import router as fashion_router
from controller.health_controller import router as health_router

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(name)s  %(message)s",
)
logger = logging.getLogger(__name__)

# Initialize FastAPI with metadata for the automated Swagger/OpenAPI documentation
app = FastAPI(
    title="Vector Database Service - Multimodal Fashion Product Search",
    description=(
        "Milvus-backed **multimodal** vector search API.\n\n"
        "Dataset: **ashraq/fashion-product-images-small** (~44k fashion products).\n\n"
        "Embedding model: **CLIP ViT-B/32** - text and images share the same 512-dim space.\n\n"
        "Search modes:\n"
        "- **Stage 1**: CRUD operations\n"
        "- **Stage 2**: Text semantic search\n"
        "- **Stage 3**: Image visual similarity search\n"
        "- **Stage 4**: Cross-modal (text-to-image, image-to-text)\n"
        "- **Stage 5**: Advanced (two-stage reranking, multimodal fusion, batch)\n"
    ),
    version="3.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
)

# Enable CORS (Cross-Origin Resource Sharing) to allow frontend apps to call this API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register the routers (grouped endpoints) to the main application
app.include_router(health_router)
app.include_router(fashion_router)


def _get_ip() -> str:
    """Helper: Discovers the local IP address for Eureka registration."""
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        # Doesn't actually send data, just opens a socket to determine the local interface
        s.connect(("10.254.254.254", 1))
        return s.getsockname()[0]
    except Exception:
        return "127.0.0.1" # Fallback to localhost if network discovery fails
    finally:
        s.close()


def _register_eureka() -> None:
    """Registers this microservice with the Eureka Service Discovery server."""
    try:
        from py_eureka_client import eureka_client
        instance_host = os.getenv("INSTANCE_HOST", _get_ip())
        eureka_client.init(
            eureka_server=EUREKA_SERVER,
            app_name=APP_NAME,
            instance_host=instance_host,
            instance_port=APP_PORT,
            health_check_url=f"http://{instance_host}:{APP_PORT}/health",
        )
        logger.info("Registered with Eureka at %s", EUREKA_SERVER)
    except Exception as exc:
        logger.warning("Eureka registration failed (non-fatal): %s", exc)


@app.on_event("startup")
async def startup() -> None:
    """FastAPI Hook: Runs logic when the web server starts up."""
    _register_eureka()
    logger.info(
        "VectorDatabaseService started.  Swagger UI → http://%s:%s/docs",
        APP_HOST, APP_PORT,
    )


if __name__ == "__main__":
    # Entry point for running the script directly via 'python app.py'
    uvicorn.run("app:app", host=APP_HOST, port=APP_PORT, reload=False)
