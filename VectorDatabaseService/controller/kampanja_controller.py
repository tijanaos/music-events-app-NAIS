from fastapi import APIRouter, HTTPException, Query

from model.kampanja import (
    KampanjaCreate,
    KampanjaUpdate,
    KampanjaIteratorSearchRequest,
)
from service.impl.kampanja_service import kampanja_service

router = APIRouter(prefix="/api/v1/kampanje", tags=["Kampanje"])


@router.post("")
def create_kampanja(payload: KampanjaCreate):
    try:
        return kampanja_service.create_kampanja(payload)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get("")
def get_all_kampanje(
    status_kampanje: str | None = Query(None),
    kanal: str | None = Query(None),
    ciljna_grupa: str | None = Query(None),
    min_budzet: float | None = Query(None),
    max_budzet: float | None = Query(None),
    limit: int = Query(20, ge=1, le=100),
    offset: int = Query(0, ge=0),
):
    return kampanja_service.get_all_kampanje(
        status_kampanje=status_kampanje,
        kanal=kanal,
        ciljna_grupa=ciljna_grupa,
        min_budzet=min_budzet,
        max_budzet=max_budzet,
        limit=limit,
        offset=offset,
    )


@router.get("/count")
def count_kampanje(
    status_kampanje: str | None = Query(None),
    kanal: str | None = Query(None),
    ciljna_grupa: str | None = Query(None),
    min_budzet: float | None = Query(None),
    max_budzet: float | None = Query(None),
):
    return kampanja_service.count_kampanje(
        status_kampanje=status_kampanje,
        kanal=kanal,
        ciljna_grupa=ciljna_grupa,
        min_budzet=min_budzet,
        max_budzet=max_budzet,
    )


@router.get("/{kampanja_id}")
def get_kampanja(kampanja_id: int):
    try:
        return kampanja_service.get_kampanja(kampanja_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.put("/{kampanja_id}")
def update_kampanja(kampanja_id: int, payload: KampanjaUpdate):
    try:
        return kampanja_service.update_kampanja(kampanja_id, payload)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.delete("/{kampanja_id}")
def delete_kampanja(kampanja_id: int):
    try:
        return kampanja_service.delete_kampanja(kampanja_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.get("/search/semantic")
def semantic_search(
    query: str,
    status_kampanje: str | None = Query(None),
    kanal: str | None = Query(None),
    ciljna_grupa: str | None = Query(None),
    min_budzet: float | None = Query(None),
    max_budzet: float | None = Query(None),
    top_k: int = Query(10, ge=1, le=100),
):
    return kampanja_service.semantic_search(
        query=query,
        status_kampanje=status_kampanje,
        kanal=kanal,
        ciljna_grupa=ciljna_grupa,
        min_budzet=min_budzet,
        max_budzet=max_budzet,
        top_k=top_k,
    )


@router.post("/search/iterator")
def search_with_iterator(payload: KampanjaIteratorSearchRequest):
    return kampanja_service.search_with_iterator(payload)


@router.post("/collection/init")
def init_collection():
    return kampanja_service.ensure_collection()


@router.delete("/collection/reset")
def reset_collection():
    return kampanja_service.reset_collection()


@router.get("/collection/stats")
def collection_stats():
    return kampanja_service.get_stats()