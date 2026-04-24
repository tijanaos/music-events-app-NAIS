from pymilvus import AnnSearchRequest, WeightedRanker

from services.milvus_service import milvus_service
from schema.oglas_schema import (
    OGLAS_SCALAR_OUTPUT_FIELDS,
    oglas_schema,
    oglas_index_params,
)
from config import OGLASI_COLLECTION, HNSW_EF_SEARCH


class OglasRepository:
    """
    Repository sloj za Milvus kolekciju 'oglasi'.

    Ovde se nalaze:
    - CRUD operacije
    - scalar query/filter
    - vector search nad text_embedding i media_embedding
    - hybrid search nad oba vector polja
    """

    def __init__(self):
        self._client = milvus_service.client
        self._collection = OGLASI_COLLECTION
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

    def find_by_id(self, oglas_id: int, include_vectors: bool = False) -> dict | None:
        output_fields = list(OGLAS_SCALAR_OUTPUT_FIELDS)

        if include_vectors:
            output_fields += ["text_embedding", "media_embedding"]

        rows = self._client.get(
            collection_name=self._collection,
            ids=[oglas_id],
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
            output_fields=OGLAS_SCALAR_OUTPUT_FIELDS,
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
    # record, uključujući ponovo izračunate embeddinge ako se menjaju naziv/opis/slika.
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

    def delete_by_id(self, oglas_id: int) -> dict:
        result = self._client.delete(
            collection_name=self._collection,
            ids=[oglas_id],
        )
        return {
            "delete_count": int(result.get("delete_count", 0)),
        }

    # ─────────────────────────────────────────────────────────────────────────
    # VECTOR SEARCH
    # ─────────────────────────────────────────────────────────────────────────

    def search(
        self,
        query_vectors: list[list[float]],
        anns_field: str,
        top_k: int = 10,
        filter_expr: str = "",
        include_vectors: bool = False,
    ) -> list[list[dict]]:
        output_fields = list(OGLAS_SCALAR_OUTPUT_FIELDS)

        if include_vectors:
            output_fields += ["text_embedding", "media_embedding"]

        raw = self._client.search(
            collection_name=self._collection,
            data=query_vectors,
            anns_field=anns_field,
            search_params=self._search_params,
            limit=top_k,
            filter=filter_expr,
            output_fields=output_fields,
            consistency_level="Strong",
        )

        return self._format_search_results(raw)

    def search_by_text_embedding(
        self,
        text_vector: list[float],
        top_k: int = 10,
        filter_expr: str = "",
    ) -> list[dict]:
        return self.search(
            query_vectors=[text_vector],
            anns_field="text_embedding",
            top_k=top_k,
            filter_expr=filter_expr,
        )[0]

    def search_by_media_embedding(
        self,
        media_vector: list[float],
        top_k: int = 10,
        filter_expr: str = "",
    ) -> list[dict]:
        return self.search(
            query_vectors=[media_vector],
            anns_field="media_embedding",
            top_k=top_k,
            filter_expr=filter_expr,
        )[0]

    # ─────────────────────────────────────────────────────────────────────────
    # HYBRID SEARCH
    # text_embedding + media_embedding + WeightedRanker
    # ─────────────────────────────────────────────────────────────────────────

    def hybrid_search(
        self,
        text_vector: list[float],
        media_vector: list[float],
        filter_expr: str = "",
        top_k: int = 10,
        text_weight: float = 0.6,
        media_weight: float = 0.4,
    ) -> list[dict]:
        text_request = AnnSearchRequest(
            data=[text_vector],
            anns_field="text_embedding",
            param=self._search_params,
            limit=top_k,
            expr=filter_expr,
        )

        media_request = AnnSearchRequest(
            data=[media_vector],
            anns_field="media_embedding",
            param=self._search_params,
            limit=top_k,
            expr=filter_expr,
        )

        ranker = WeightedRanker(text_weight, media_weight)

        raw = self._client.hybrid_search(
            collection_name=self._collection,
            reqs=[text_request, media_request],
            ranker=ranker,
            limit=top_k,
            output_fields=OGLAS_SCALAR_OUTPUT_FIELDS,
            consistency_level="Strong",
        )

        return self._format_single_search_result(raw[0] if raw else [])

    # ─────────────────────────────────────────────────────────────────────────
    # COLLECTION MANAGEMENT
    # ─────────────────────────────────────────────────────────────────────────

    def ensure_collection(self) -> None:
        schema = oglas_schema(self._client)
        index_params = oglas_index_params(self._client)

        milvus_service.ensure_collection(
            name=self._collection,
            schema=schema,
            index_params=index_params,
        )

        self._client.load_collection(self._collection)

    def reset_collection(self) -> None:
        schema = oglas_schema(self._client)
        index_params = oglas_index_params(self._client)

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

    def _format_search_results(self, raw) -> list[list[dict]]:
        results = []

        for hits in raw:
            batch = self._format_single_search_result(hits)
            results.append(batch)

        return results

    def _format_single_search_result(self, hits) -> list[dict]:
        batch = []

        for hit in hits:
            entity = self._extract_hit_entity(hit)
            hit_id = self._extract_hit_value(hit, "id")
            if hit_id is None:
                hit_id = entity.get("oglas_id")

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


oglas_repository = OglasRepository()
