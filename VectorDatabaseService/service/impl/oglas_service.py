from datetime import datetime
from typing import Optional

from model.oglas import (
    OglasCreate,
    OglasUpdate,
    OglasSemanticFilterRequest,
    OglasHybridSearchRequest,
)
from repositroy.oglas_repository import oglas_repository
from services.embedding_service import embedding_service


class OglasService:
    """
    Service sloj za oglase.

    Ovde se nalazi business logika:
    - CRUD
    - formiranje text_embedding
    - formiranje media_embedding
    - scalar filteri
    - vector search
    - hybrid search
    """

    # ─────────────────────────────────────────────────────────────────────────
    # HELPERS
    # ─────────────────────────────────────────────────────────────────────────

    def _build_text_for_embedding(self, naziv: str, opis: str) -> str:
        return f"{naziv} {opis}".strip()

    def _build_filter(
        self,
        tip_oglasa: Optional[str] = None,
        status: Optional[str] = None,
        kategorija: Optional[str] = None,
        kampanja_id: Optional[int] = None,
    ) -> str:
        filters = []

        if tip_oglasa:
            filters.append(f'tip_oglasa == "{tip_oglasa}"')

        if status:
            filters.append(f'status == "{status}"')

        if kategorija:
            filters.append(f'kategorija == "{kategorija}"')

        if kampanja_id is not None:
            filters.append(f"kampanja_id == {kampanja_id}")

        return " && ".join(filters)

    def _create_embeddings(self, naziv: str, opis: str, tip_oglasa: str, content_url: Optional[str]):
        text_for_embedding = self._build_text_for_embedding(naziv, opis)

        text_embedding = embedding_service.encode_text_one(text_for_embedding)

        if tip_oglasa == "vizuelni" and content_url:
            media_embedding = embedding_service.encode_image_from_path_or_url(content_url)
        else:
            media_embedding = embedding_service.encode_text_one(text_for_embedding)

        return text_embedding, media_embedding

    def _to_record(self, payload: OglasCreate) -> dict:
        text_embedding, media_embedding = self._create_embeddings(
            naziv=payload.naziv,
            opis=payload.opis,
            tip_oglasa=payload.tip_oglasa,
            content_url=payload.content_url,
        )

        return {
            "oglas_id": payload.oglas_id,
            "naziv": payload.naziv,
            "opis": payload.opis,
            "tip_oglasa": payload.tip_oglasa,
            "content_url": payload.content_url or "",
            "status": payload.status,
            "kategorija": payload.kategorija,
            "datum_kreiranja": payload.datum_kreiranja,
            "datum_poslednje_izmene": payload.datum_poslednje_izmene,
            "kampanja_id": payload.kampanja_id,
            "text_embedding": text_embedding,
            "media_embedding": media_embedding,
        }

    # ─────────────────────────────────────────────────────────────────────────
    # CRUD
    # ─────────────────────────────────────────────────────────────────────────

    def create_oglas(self, payload: OglasCreate) -> dict:
        existing = oglas_repository.find_by_id(payload.oglas_id)

        if existing:
            raise ValueError(f"Oglas sa id={payload.oglas_id} već postoji.")

        record = self._to_record(payload)
        result = oglas_repository.insert(record)

        return {
            "message": "Oglas uspešno kreiran.",
            "result": result,
            "data": payload.model_dump(),
        }

    def get_oglas(self, oglas_id: int) -> dict:
        oglas = oglas_repository.find_by_id(oglas_id)

        if not oglas:
            raise KeyError(f"Oglas sa id={oglas_id} nije pronađen.")

        return oglas

    def get_all_oglasi(
        self,
        tip_oglasa: Optional[str] = None,
        status: Optional[str] = None,
        kategorija: Optional[str] = None,
        kampanja_id: Optional[int] = None,
        limit: int = 20,
        offset: int = 0,
    ) -> list[dict]:
        filter_expr = self._build_filter(
            tip_oglasa=tip_oglasa,
            status=status,
            kategorija=kategorija,
            kampanja_id=kampanja_id,
        )

        return oglas_repository.find_all(
            filter_expr=filter_expr,
            limit=limit,
            offset=offset,
        )

    def update_oglas(self, oglas_id: int, payload: OglasUpdate) -> dict:
        existing = oglas_repository.find_by_id(oglas_id, include_vectors=True)

        if not existing:
            raise KeyError(f"Oglas sa id={oglas_id} nije pronađen.")

        update_data = payload.model_dump(exclude_unset=True)

        merged = {
            **existing,
            **update_data,
        }

        merged["oglas_id"] = oglas_id

        if not merged.get("datum_poslednje_izmene"):
            merged["datum_poslednje_izmene"] = datetime.utcnow().isoformat()

        fields_that_affect_embedding = {
            "naziv",
            "opis",
            "tip_oglasa",
            "content_url",
        }

        should_recalculate_embeddings = any(
            field in update_data for field in fields_that_affect_embedding
        )

        if should_recalculate_embeddings:
            text_embedding, media_embedding = self._create_embeddings(
                naziv=merged["naziv"],
                opis=merged["opis"],
                tip_oglasa=merged["tip_oglasa"],
                content_url=merged.get("content_url"),
            )

            merged["text_embedding"] = text_embedding
            merged["media_embedding"] = media_embedding

        result = oglas_repository.upsert(merged)

        return {
            "message": "Oglas uspešno ažuriran.",
            "result": result,
            "data": {
                key: value
                for key, value in merged.items()
                if key not in ["text_embedding", "media_embedding"]
            },
        }

    def delete_oglas(self, oglas_id: int) -> dict:
        existing = oglas_repository.find_by_id(oglas_id)

        if not existing:
            raise KeyError(f"Oglas sa id={oglas_id} nije pronađen.")

        result = oglas_repository.delete_by_id(oglas_id)

        return {
            "message": "Oglas uspešno obrisan.",
            "result": result,
        }

    # ─────────────────────────────────────────────────────────────────────────
    # SIMPLE QUERIES
    # ─────────────────────────────────────────────────────────────────────────

    def count_oglasi(
        self,
        tip_oglasa: Optional[str] = None,
        status: Optional[str] = None,
        kategorija: Optional[str] = None,
        kampanja_id: Optional[int] = None,
    ) -> dict:
        filter_expr = self._build_filter(
            tip_oglasa=tip_oglasa,
            status=status,
            kategorija=kategorija,
            kampanja_id=kampanja_id,
        )

        count = oglas_repository.count(filter_expr)

        return {
            "filter": filter_expr,
            "count": count,
        }

    # ─────────────────────────────────────────────────────────────────────────
    # VECTOR SEARCH + FILTERING
    # ─────────────────────────────────────────────────────────────────────────

    def semantic_search_with_filters(
        self,
        request: OglasSemanticFilterRequest,
    ) -> list[dict]:
        query_vector = embedding_service.encode_text_one(request.query)

        filter_expr = self._build_filter(
            tip_oglasa=request.tip_oglasa,
            status=request.status,
            kategorija=request.kategorija,
        )

        return oglas_repository.search_by_text_embedding(
            text_vector=query_vector,
            top_k=request.top_k,
            filter_expr=filter_expr,
        )

    # ─────────────────────────────────────────────────────────────────────────
    # MULTI-VECTOR HYBRID SEARCH
    # ─────────────────────────────────────────────────────────────────────────

    def hybrid_search(
        self,
        request: OglasHybridSearchRequest,
    ) -> list[dict]:
        text_vector = embedding_service.encode_text_one(request.text_query)

        media_vector = embedding_service.encode_image_base64(request.image_base64)

        filter_expr = self._build_filter(
            tip_oglasa=request.tip_oglasa,
            status=request.status,
            kategorija=request.kategorija,
        )

        return oglas_repository.hybrid_search(
            text_vector=text_vector,
            media_vector=media_vector,
            filter_expr=filter_expr,
            top_k=request.top_k,
            text_weight=0.6,
            media_weight=0.4,
        )

    # ─────────────────────────────────────────────────────────────────────────
    # COLLECTION MANAGEMENT
    # ─────────────────────────────────────────────────────────────────────────

    def ensure_collection(self) -> dict:
        oglas_repository.ensure_collection()
        return {"message": "Kolekcija 'oglasi' je spremna."}

    def reset_collection(self) -> dict:
        oglas_repository.reset_collection()
        return {"message": "Kolekcija 'oglasi' je resetovana."}

    def get_stats(self) -> dict:
        return oglas_repository.get_stats()


oglas_service = OglasService()
