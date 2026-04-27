from fastapi import APIRouter, File, Form, HTTPException, Query, UploadFile

from model.common import CollectionActionResponse, CountResponse
from model.oglas import (
    OglasCreate,
    OglasSearchResult,
    OglasUpdate,
    OglasSemanticFilterRequest,
    OglasHybridSearchRequest,
    Oglas,
)
from service.impl.oglas_service import oglas_service

router = APIRouter(prefix="/api/v1/oglasi", tags=["Oglasi"])


@router.post(
    "",
    summary="Kreiranje oglasa",
    description="Kreira novi oglas sa scalar poljima i automatski generisanim embedding vektorima.",
)
def create_oglas(payload: OglasCreate):
    try:
        return oglas_service.create_oglas(payload)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get(
    "",
    response_model=list[Oglas],
    summary="Listanje oglasa",
    description="Vraća oglase uz opcione filtere po tipu, statusu, kategoriji i kampanji.",
)
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


@router.get(
    "/count",
    response_model=CountResponse,
    summary="Brojanje oglasa",
    description="Prebrojava oglase koji zadovoljavaju zadate scalar filtere.",
)
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


@router.get(
    "/{oglas_id}",
    response_model=Oglas,
    summary="Dobavljanje oglasa po ID-u",
    description="Vraća jedan oglas na osnovu njegovog primarnog ključa.",
)
def get_oglas(oglas_id: int):
    try:
        return oglas_service.get_oglas(oglas_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.put(
    "/{oglas_id}",
    summary="Ažuriranje oglasa",
    description="Ažurira scalar polja oglasa i po potrebi ponovo računa embedding vektore.",
)
def update_oglas(oglas_id: int, payload: OglasUpdate):
    try:
        return oglas_service.update_oglas(oglas_id, payload)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.delete(
    "/{oglas_id}",
    summary="Brisanje oglasa",
    description="Briše oglas na osnovu ID-a.",
)
def delete_oglas(oglas_id: int):
    try:
        return oglas_service.delete_oglas(oglas_id)
    except KeyError as e:
        raise HTTPException(status_code=404, detail=str(e))


@router.post(
    "/search/semantic-filtered",
    response_model=list[OglasSearchResult],
    summary="Semantička pretraga oglasa sa filterima",
    description="Pronalazi oglase semantički slične tekstualnom upitu i dodatno ih filtrira po tipu, statusu i kategoriji.",
)
def semantic_search_with_filters(payload: OglasSemanticFilterRequest):
    return oglas_service.semantic_search_with_filters(payload)


@router.post(
    "/search/hybrid",
    response_model=list[OglasSearchResult],
    summary="Hybrid pretraga oglasa",
    description="Vrši multi-vector pretragu nad text_embedding i media_embedding poljima i spaja rezultate WeightedRanker logikom.",
)
def hybrid_search(payload: OglasHybridSearchRequest):
    return oglas_service.hybrid_search(payload)


@router.post(
    "/search/hybrid-upload",
    response_model=list[OglasSearchResult],
    summary="Hybrid pretraga oglasa sa uploadovanom slikom",
    description="Koristi stvarni upload slike iz Swagger UI-ja zajedno sa tekstualnim upitom i opcionim filterima.",
)
async def hybrid_search_upload(
    text_query: str = Form(...),
    image_file: UploadFile = File(...),
    tip_oglasa: str | None = Form(None),
    status: str | None = Form(None),
    kategorija: str | None = Form(None),
    top_k: int = Form(10),
):
    image_bytes = await image_file.read()
    return oglas_service.hybrid_search_from_image_bytes(
        text_query=text_query,
        image_bytes=image_bytes,
        tip_oglasa=tip_oglasa,
        status=status,
        kategorija=kategorija,
        top_k=top_k,
    )


@router.post(
    "/collection/init",
    response_model=CollectionActionResponse,
    summary="Inicijalizacija kolekcije oglasa",
    description="Kreira kolekciju oglasa i indekse ako već ne postoje.",
)
def init_collection():
    return oglas_service.ensure_collection()


@router.delete(
    "/collection/reset",
    response_model=CollectionActionResponse,
    summary="Reset kolekcije oglasa",
    description="Briše i ponovo kreira kolekciju oglasa zajedno sa indeksima.",
)
def reset_collection():
    return oglas_service.reset_collection()


@router.get(
    "/collection/stats",
    summary="Statistika kolekcije oglasa",
    description="Vraća statistiku Milvus kolekcije za oglase.",
)
def collection_stats():
    return oglas_service.get_stats()
