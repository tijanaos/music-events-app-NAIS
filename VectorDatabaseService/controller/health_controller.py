import logging

from fastapi import APIRouter

from model.common import HealthResponse
from services.milvus_service import milvus_service

logger = logging.getLogger(__name__)
router = APIRouter(tags=["Health"])


@router.get(
    "/health",
    response_model=HealthResponse,
    summary="Provera dostupnosti API servisa",
    description="Jednostavan health endpoint za proveru da li je FastAPI servis podignut i dostupan.",
)
def health():
    return {"status": "ok"}


@router.get(
    "/health/milvus",
    summary="Provera konekcije sa Milvus bazom",
    description="Vraća status i listu kolekcija ako je konekcija sa Milvus bazom uspešna.",
)
def milvus_health():
    try:
        collections = milvus_service.list_collections()
        return {"status": "ok", "collections": collections}
    except Exception as exc:
        logger.exception("Milvus health check failed")
        return {"status": "error", "detail": str(exc)}


@router.get(
    "/collections/{collection_name}/stats",
    summary="Statistika kolekcije",
    description="Vraća statistiku za proizvoljnu Milvus kolekciju, uključujući broj redova i druge metapodatke.",
)
def collection_stats(collection_name: str):
    try:
        return milvus_service.collection_stats(collection_name)
    except Exception as exc:
        return {"error": str(exc)}
