from datetime import datetime
from typing import Optional

from model.kampanja import (
    KampanjaCreate,
    KampanjaUpdate,
    KampanjaIteratorSearchRequest,
)
from repositroy.kampanja_repository import kampanja_repository
from services.embedding_service import embedding_service


class KampanjaService:
    """
    Service sloj za kampanje.

    Ovde se nalazi business logika:
    - CRUD
    - formiranje campaign_embedding
    - scalar filteri
    - vector search
    - iterator search
    """

    # ─────────────────────────────────────────────────────────────────────────
    # HELPERS
    # ─────────────────────────────────────────────────────────────────────────

    def _build_text_for_embedding(
        self,
        naziv_kampanje: str,
        opis_kampanje: str,
        ciljna_grupa: str,
    ) -> str:
        return f"{naziv_kampanje} {opis_kampanje} {ciljna_grupa}".strip()

    def _build_filter(
        self,
        status_kampanje: Optional[str] = None,
        kanal: Optional[str] = None,
        ciljna_grupa: Optional[str] = None,
        min_budzet: Optional[float] = None,
        max_budzet: Optional[float] = None,
    ) -> str:
        filters = []

        if status_kampanje:
            filters.append(f'status_kampanje == "{status_kampanje}"')

        if kanal:
            filters.append(f'kanal == "{kanal}"')

        if ciljna_grupa:
            filters.append(f'ciljna_grupa == "{ciljna_grupa}"')

        if min_budzet is not None:
            filters.append(f"budzet >= {min_budzet}")

        if max_budzet is not None:
            filters.append(f"budzet <= {max_budzet}")

        return " && ".join(filters)

    def _create_campaign_embedding(
        self,
        naziv_kampanje: str,
        opis_kampanje: str,
        ciljna_grupa: str,
    ) -> list[float]:
        text = self._build_text_for_embedding(
            naziv_kampanje=naziv_kampanje,
            opis_kampanje=opis_kampanje,
            ciljna_grupa=ciljna_grupa,
        )

        return embedding_service.encode_text_one(text)

    def _to_record(self, payload: KampanjaCreate) -> dict:
        campaign_embedding = self._create_campaign_embedding(
            naziv_kampanje=payload.naziv_kampanje,
            opis_kampanje=payload.opis_kampanje,
            ciljna_grupa=payload.ciljna_grupa,
        )

        return {
            "kampanja_id": payload.kampanja_id,
            "naziv_kampanje": payload.naziv_kampanje,
            "opis_kampanje": payload.opis_kampanje,
            "ciljna_grupa": payload.ciljna_grupa,
            "kanal": payload.kanal,
            "budzet": payload.budzet,
            "status_kampanje": payload.status_kampanje,
            "datum_pocetka": payload.datum_pocetka,
            "datum_zavrsetka": payload.datum_zavrsetka,
            "campaign_embedding": campaign_embedding,
        }

    # ─────────────────────────────────────────────────────────────────────────
    # CRUD
    # ─────────────────────────────────────────────────────────────────────────

    def create_kampanja(self, payload: KampanjaCreate) -> dict:
        existing = kampanja_repository.find_by_id(payload.kampanja_id)

        if existing:
            raise ValueError(f"Kampanja sa id={payload.kampanja_id} već postoji.")

        record = self._to_record(payload)
        result = kampanja_repository.insert(record)

        return {
            "message": "Kampanja uspešno kreirana.",
            "result": result,
            "data": payload.model_dump(),
        }

    def get_kampanja(self, kampanja_id: int) -> dict:
        kampanja = kampanja_repository.find_by_id(kampanja_id)

        if not kampanja:
            raise KeyError(f"Kampanja sa id={kampanja_id} nije pronađena.")

        return kampanja

    def get_all_kampanje(
        self,
        status_kampanje: Optional[str] = None,
        kanal: Optional[str] = None,
        ciljna_grupa: Optional[str] = None,
        min_budzet: Optional[float] = None,
        max_budzet: Optional[float] = None,
        limit: int = 20,
        offset: int = 0,
    ) -> list[dict]:
        filter_expr = self._build_filter(
            status_kampanje=status_kampanje,
            kanal=kanal,
            ciljna_grupa=ciljna_grupa,
            min_budzet=min_budzet,
            max_budzet=max_budzet,
        )

        return kampanja_repository.find_all(
            filter_expr=filter_expr,
            limit=limit,
            offset=offset,
        )

    def update_kampanja(self, kampanja_id: int, payload: KampanjaUpdate) -> dict:
        existing = kampanja_repository.find_by_id(kampanja_id, include_vectors=True)

        if not existing:
            raise KeyError(f"Kampanja sa id={kampanja_id} nije pronađena.")

        update_data = payload.model_dump(exclude_unset=True)

        merged = {
            **existing,
            **update_data,
        }

        merged["kampanja_id"] = kampanja_id

        fields_that_affect_embedding = {
            "naziv_kampanje",
            "opis_kampanje",
            "ciljna_grupa",
        }

        should_recalculate_embedding = any(
            field in update_data for field in fields_that_affect_embedding
        )

        if should_recalculate_embedding:
            merged["campaign_embedding"] = self._create_campaign_embedding(
                naziv_kampanje=merged["naziv_kampanje"],
                opis_kampanje=merged["opis_kampanje"],
                ciljna_grupa=merged["ciljna_grupa"],
            )

        result = kampanja_repository.upsert(merged)

        return {
            "message": "Kampanja uspešno ažurirana.",
            "result": result,
            "data": {
                key: value
                for key, value in merged.items()
                if key != "campaign_embedding"
            },
        }

    def delete_kampanja(self, kampanja_id: int) -> dict:
        existing = kampanja_repository.find_by_id(kampanja_id)

        if not existing:
            raise KeyError(f"Kampanja sa id={kampanja_id} nije pronađena.")

        result = kampanja_repository.delete_by_id(kampanja_id)

        return {
            "message": "Kampanja uspešno obrisana.",
            "result": result,
        }

    # ─────────────────────────────────────────────────────────────────────────
    # SIMPLE QUERIES
    # ─────────────────────────────────────────────────────────────────────────

    def count_kampanje(
        self,
        status_kampanje: Optional[str] = None,
        kanal: Optional[str] = None,
        ciljna_grupa: Optional[str] = None,
        min_budzet: Optional[float] = None,
        max_budzet: Optional[float] = None,
    ) -> dict:
        filter_expr = self._build_filter(
            status_kampanje=status_kampanje,
            kanal=kanal,
            ciljna_grupa=ciljna_grupa,
            min_budzet=min_budzet,
            max_budzet=max_budzet,
        )

        count = kampanja_repository.count(filter_expr)

        return {
            "filter": filter_expr,
            "count": count,
        }

    # ─────────────────────────────────────────────────────────────────────────
    # VECTOR SEARCH + FILTERING
    # ─────────────────────────────────────────────────────────────────────────

    def semantic_search(
        self,
        query: str,
        status_kampanje: Optional[str] = None,
        kanal: Optional[str] = None,
        ciljna_grupa: Optional[str] = None,
        min_budzet: Optional[float] = None,
        max_budzet: Optional[float] = None,
        top_k: int = 10,
    ) -> list[dict]:
        query_vector = embedding_service.encode_text_one(query)

        filter_expr = self._build_filter(
            status_kampanje=status_kampanje,
            kanal=kanal,
            ciljna_grupa=ciljna_grupa,
            min_budzet=min_budzet,
            max_budzet=max_budzet,
        )

        return kampanja_repository.search(
            query_vector=query_vector,
            top_k=top_k,
            filter_expr=filter_expr,
        )

    # ─────────────────────────────────────────────────────────────────────────
    # VECTOR SEARCH + FILTERING + ITERATOR
    # ─────────────────────────────────────────────────────────────────────────

    def search_with_iterator(
        self,
        request: KampanjaIteratorSearchRequest,
    ) -> list[dict]:
        query_vector = embedding_service.encode_text_one(request.query)

        filter_expr = self._build_filter(
            kanal=request.kanal,
            min_budzet=request.min_budzet,
            max_budzet=request.max_budzet,
        )

        return kampanja_repository.search_iterator(
            query_vector=query_vector,
            filter_expr=filter_expr,
            batch_size=request.batch_size,
            limit=request.limit,
        )

    # ─────────────────────────────────────────────────────────────────────────
    # COLLECTION MANAGEMENT
    # ─────────────────────────────────────────────────────────────────────────

    def ensure_collection(self) -> dict:
        kampanja_repository.ensure_collection()
        return {"message": "Kolekcija 'kampanje' je spremna."}

    def reset_collection(self) -> dict:
        kampanja_repository.reset_collection()
        return {"message": "Kolekcija 'kampanje' je resetovana."}

    def get_stats(self) -> dict:
        return kampanja_repository.get_stats()


kampanja_service = KampanjaService()
