from fastapi import APIRouter, HTTPException, Query

from model.oglas import (
    OglasCreate,
    OglasUpdate,
    OglasSemanticFilterRequest,
    OglasHybridSearchRequest,
)
from service.impl.oglas_service import oglas_service

router = APIRouter(prefix="/api/v1/oglasi", tags=["Oglasi"])


@router.post("")
def create_oglas(payload: OglasCreate):
    try:
        return oglas_service.create_oglas(payload)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get("")
def get_all_oglasi(
    tip_oglasa: str | None = Query(None),
    status: str | None = Query(None),
    kategorija: str | None = Query(None),
    kampanja_id: int | None = Query(None),
    limit: int = Query(20, ge=1, le=100),
    offset: int = Query(0, ge=0),
):
    return oglas_service.get_all_oglasi(
        tip_oglasa=tip_oglasa,
        status=status,
        kategorija=kategorija,
        kampanja_id=kampanja_id,
        limit=limit,
        offset=offset,
    )


@router.get("/count")
def count_oglasi(
    tip_oglasa: str | None = Query(None),
    status: str | None = Query(None),
    kategorija: str | None = Query(None),
    kampanja_id: int | None = Query(None),
):
    return oglas_service.count_oglasi(
        tip_oglasa=tip_oglasa,
        status=status,
        kategorija=kategorija,
        kampanja_id=kampanja_id,
    )


@router.get("/{oglas_id}")
def get_oglas(oglas_id: int):
    try:
        return oglas_service.get_oglas(oglas_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.put("/{oglas_id}")
def update_oglas(oglas_id: int, payload: OglasUpdate):
    try:
        return oglas_service.update_oglas(oglas_id, payload)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.delete("/{oglas_id}")
def delete_oglas(oglas_id: int):
    try:
        return oglas_service.delete_oglas(oglas_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.post("/search/semantic-filtered")
def semantic_search_with_filters(payload: OglasSemanticFilterRequest):
    return oglas_service.semantic_search_with_filters(payload)


@router.post("/search/hybrid")
def hybrid_search(payload: OglasHybridSearchRequest):
    return oglas_service.hybrid_search(payload)


@router.post("/collection/init")
def init_collection():
    return oglas_service.ensure_collection()


@router.delete("/collection/reset")
def reset_collection():
    return oglas_service.reset_collection()


@router.get("/collection/stats")
def collection_stats():
    return oglas_service.get_stats()