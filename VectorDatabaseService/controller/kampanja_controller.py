from fastapi import APIRouter, HTTPException, Query

from model.common import CollectionActionResponse, CountResponse
from model.kampanja import (
    KampanjaCreate,
    KampanjaSearchResult,
    KampanjaUpdate,
    KampanjaIteratorSearchRequest,
    Kampanja,
)
from service.impl.kampanja_service import kampanja_service

router = APIRouter(prefix="/api/v1/kampanje", tags=["Kampanje"])


@router.post(
    "",
    summary="Kreiranje kampanje",
    description="Kreira novu kampanju i generiše campaign_embedding iz naziva, opisa i ciljne grupe.",
)
def create_kampanja(payload: KampanjaCreate):
    try:
        return kampanja_service.create_kampanja(payload)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get(
    "",
    response_model=list[Kampanja],
    summary="Listanje kampanja",
    description="Vraća kampanje uz opcione filtere po statusu, kanalu, ciljnoj grupi i budžetu.",
)
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


@router.get(
    "/count",
    response_model=CountResponse,
    summary="Brojanje kampanja",
    description="Prebrojava kampanje koje zadovoljavaju zadate scalar filtere.",
)
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


@router.get(
    "/{kampanja_id}",
    response_model=Kampanja,
    summary="Dobavljanje kampanje po ID-u",
    description="Vraća jednu kampanju na osnovu njenog primarnog ključa.",
)
def get_kampanja(kampanja_id: int):
    try:
        return kampanja_service.get_kampanja(kampanja_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.put(
    "/{kampanja_id}",
    summary="Ažuriranje kampanje",
    description="Ažurira scalar polja kampanje i po potrebi ponovo računa campaign_embedding.",
)
def update_kampanja(kampanja_id: int, payload: KampanjaUpdate):
    try:
        return kampanja_service.update_kampanja(kampanja_id, payload)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.delete(
    "/{kampanja_id}",
    summary="Brisanje kampanje",
    description="Briše kampanju na osnovu ID-a.",
)
def delete_kampanja(kampanja_id: int):
    try:
        return kampanja_service.delete_kampanja(kampanja_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.get(
    "/search/semantic",
    response_model=list[KampanjaSearchResult],
    summary="Semantička pretraga kampanja",
    description="Pronalazi kampanje semantički slične query tekstu uz opcione filtere po statusu, kanalu, ciljnoj grupi i budžetu.",
)
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


@router.post(
    "/search/iterator",
    response_model=list[KampanjaSearchResult],
    summary="Iterator pretraga kampanja",
    description="Vrši vektorsku pretragu kampanja sa filterima i interno obrađuje rezultate kroz Milvus iterator.",
)
def search_with_iterator(payload: KampanjaIteratorSearchRequest):
    return kampanja_service.search_with_iterator(payload)


@router.post(
    "/collection/init",
    response_model=CollectionActionResponse,
    summary="Inicijalizacija kolekcije kampanja",
    description="Kreira kolekciju kampanja i indekse ako već ne postoje.",
)
def init_collection():
    return kampanja_service.ensure_collection()


@router.delete(
    "/collection/reset",
    response_model=CollectionActionResponse,
    summary="Reset kolekcije kampanja",
    description="Briše i ponovo kreira kolekciju kampanja zajedno sa indeksima.",
)
def reset_collection():
    return kampanja_service.reset_collection()


@router.get(
    "/collection/stats",
    summary="Statistika kolekcije kampanja",
    description="Vraća statistiku Milvus kolekcije za kampanje.",
)
def collection_stats():
    return kampanja_service.get_stats()
