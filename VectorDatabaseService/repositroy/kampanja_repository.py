from services.milvus_service import milvus_service
from schema.kampanja_schema import (
    KAMPANJA_SCALAR_OUTPUT_FIELDS,
    kampanja_schema,
    kampanja_index_params,
)
from config import KAMPANJE_COLLECTION, HNSW_EF_SEARCH


class KampanjaRepository:
    """
    Repository sloj za Milvus kolekciju 'kampanje'.

    Ovde se nalaze:
    - CRUD operacije
    - scalar query/filter
    - vector search nad campaign_embedding
    - iterator search za kampanje
    """

    def __init__(self):
        self._client = milvus_service.client
        self._collection = KAMPANJE_COLLECTION
        self._search_params = {
            "metric_type": "COSINE",
            "params": {"ef": HNSW_EF_SEARCH},
        }

    # ─────────────────────────────────────────────────────────────────────────
    # CREATE
    # ─────────────────────────────────────────────────────────────────────────

    def insert(self, record: dict) -> dict:
        result = self._client.insert(
            collection_name=self._collection,
            data=[record],
        )
        return self._format_mutation_result(result, "insert_count")

    def insert_many(self, records: list[dict]) -> dict:
        result = self._client.insert(
            collection_name=self._collection,
            data=records,
        )
        return self._format_mutation_result(result, "insert_count")

    # ─────────────────────────────────────────────────────────────────────────
    # READ
    # ─────────────────────────────────────────────────────────────────────────

    def find_by_id(self, kampanja_id: int, include_vectors: bool = False) -> dict | None:
        output_fields = list(KAMPANJA_SCALAR_OUTPUT_FIELDS)

        if include_vectors:
            output_fields += ["campaign_embedding"]

        rows = self._client.get(
            collection_name=self._collection,
            ids=[kampanja_id],
            output_fields=output_fields,
        )

        return rows[0] if rows else None

    def find_all(
        self,
        filter_expr: str = "",
        limit: int = 20,
        offset: int = 0,
    ) -> list[dict]:
        return self._client.query(
            collection_name=self._collection,
            filter=filter_expr or "",
            output_fields=KAMPANJA_SCALAR_OUTPUT_FIELDS,
            limit=limit,
            offset=offset,
        )

    def count(self, filter_expr: str = "") -> int:
        result = self._client.query(
            collection_name=self._collection,
            filter=filter_expr or "",
            output_fields=["count(*)"],
        )

        return int(result[0]["count(*)"]) if result else 0

    # ─────────────────────────────────────────────────────────────────────────
    # UPDATE
    # Milvus upsert menja ceo zapis, zato service sloj mora da pripremi kompletan
    # record, uključujući ponovo izračunat campaign_embedding ako se menjaju
    # naziv_kampanje/opis_kampanje/ciljna_grupa.
    # ─────────────────────────────────────────────────────────────────────────

    def upsert(self, record: dict) -> dict:
        result = self._client.upsert(
            collection_name=self._collection,
            data=[record],
        )
        return self._format_mutation_result(result, "upsert_count")

    # ─────────────────────────────────────────────────────────────────────────
    # DELETE
    # ─────────────────────────────────────────────────────────────────────────

    def delete_by_id(self, kampanja_id: int) -> dict:
        result = self._client.delete(
            collection_name=self._collection,
            ids=[kampanja_id],
        )
        return {
            "delete_count": int(result.get("delete_count", 0)),
        }

    # ─────────────────────────────────────────────────────────────────────────
    # VECTOR SEARCH
    # ─────────────────────────────────────────────────────────────────────────

    def search(
        self,
        query_vector: list[float],
        top_k: int = 10,
        filter_expr: str = "",
        include_vectors: bool = False,
    ) -> list[dict]:
        output_fields = list(KAMPANJA_SCALAR_OUTPUT_FIELDS)

        if include_vectors:
            output_fields += ["campaign_embedding"]

        raw = self._client.search(
            collection_name=self._collection,
            data=[query_vector],
            anns_field="campaign_embedding",
            search_params=self._search_params,
            limit=top_k,
            filter=filter_expr,
            output_fields=output_fields,
            consistency_level="Strong",
        )

        if not raw:
            return []

        return self._format_single_search_result(raw[0])

    # ─────────────────────────────────────────────────────────────────────────
    # ITERATOR SEARCH
    # Koristi se kada želiš da vektorsku pretragu sa filterom obrađuješ po batch-evima.
    # ─────────────────────────────────────────────────────────────────────────

    def search_iterator(
        self,
        query_vector: list[float],
        filter_expr: str = "",
        batch_size: int = 20,
        limit: int = 100,
    ) -> list[dict]:
        output_fields = list(KAMPANJA_SCALAR_OUTPUT_FIELDS)

        iterator = self._client.search_iterator(
            collection_name=self._collection,
            data=[query_vector],
            anns_field="campaign_embedding",
            batch_size=batch_size,
            limit=limit,
            filter=filter_expr,
            output_fields=output_fields,
            search_params=self._search_params,
            consistency_level="Strong",
        )

        results = []

        try:
            while True:
                batch = iterator.next()

                if not batch:
                    break

                results.extend(self._format_single_search_result(batch))
        finally:
            iterator.close()

        return results

    # ─────────────────────────────────────────────────────────────────────────
    # COLLECTION MANAGEMENT
    # ─────────────────────────────────────────────────────────────────────────

    def ensure_collection(self) -> None:
        schema = kampanja_schema(self._client)
        index_params = kampanja_index_params(self._client)

        milvus_service.ensure_collection(
            name=self._collection,
            schema=schema,
            index_params=index_params,
        )

        self._client.load_collection(self._collection)

    def reset_collection(self) -> None:
        schema = kampanja_schema(self._client)
        index_params = kampanja_index_params(self._client)

        milvus_service.drop_and_recreate(
            name=self._collection,
            schema=schema,
            index_params=index_params,
        )

        self._client.load_collection(self._collection)

    def get_stats(self) -> dict:
        return self._client.get_collection_stats(
            collection_name=self._collection,
        )

    # ─────────────────────────────────────────────────────────────────────────
    # HELPERS
    # ─────────────────────────────────────────────────────────────────────────

    def _format_single_search_result(self, hits) -> list[dict]:
        batch = []

        for hit in hits:
            entity = self._extract_hit_entity(hit)
            hit_id = self._extract_hit_value(hit, "id")
            if hit_id is None:
                hit_id = entity.get("kampanja_id")

            row = {
                "id": hit_id,
                "score": round(float(self._extract_hit_value(hit, "distance", 0.0)), 4),
            }
            row.update(entity)
            batch.append(row)

        return batch

    def _format_mutation_result(self, result: dict, count_key: str) -> dict:
        ids = result.get("ids", [])

        return {
            count_key: int(result.get(count_key, 0)),
            "ids": [int(item) for item in ids],
        }

    def _extract_hit_value(self, hit, key: str, default=None):
        if isinstance(hit, dict):
            return hit.get(key, default)

        return getattr(hit, key, default)

    def _extract_hit_entity(self, hit) -> dict:
        if isinstance(hit, dict):
            entity = hit.get("entity", {})
            return dict(entity) if entity else {}

        entity = getattr(hit, "entity", None)
        if entity is not None:
            if isinstance(entity, dict):
                return dict(entity)
            if hasattr(entity, "items"):
                return dict(entity.items())

        fields = getattr(hit, "fields", None)
        if isinstance(fields, dict):
            return dict(fields)

        return {}


kampanja_repository = KampanjaRepository()
