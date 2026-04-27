from pymilvus import DataType, MilvusClient
from config import EMBEDDING_DIM, HNSW_M, HNSW_EF_CONSTRUCTION

OGLAS_SCALAR_OUTPUT_FIELDS = [
    "oglas_id",
    "naziv",
    "opis",
    "tip_oglasa",
    "content_url",
    "status",
    "kategorija",
    "datum_kreiranja",
    "datum_poslednje_izmene",
    "kampanja_id",
]

def oglas_schema(client: MilvusClient):
    schema = client.create_schema(
        auto_id=False,
        enable_dynamic_fields=False,
        description=(
            "Kolekcija oglasa. "
            "Sadrži dva vektorska polja: text_embedding i media_embedding. "
            "text_embedding = embedding(naziv + opis). "
            "media_embedding = embedding(image) za vizuelne oglase ili embedding(naziv + opis) za tekstualne."
        ),
    )

    # Primary key
    schema.add_field(
        "oglas_id",
        DataType.INT64,
        is_primary=True,
        description="Primarni ključ oglasa"
    )

    # Scalar fields
    schema.add_field("naziv", DataType.VARCHAR, max_length=512)
    schema.add_field("opis", DataType.VARCHAR, max_length=4000)
    schema.add_field("tip_oglasa", DataType.VARCHAR, max_length=64)       # tekstualni | vizuelni
    schema.add_field("content_url", DataType.VARCHAR, max_length=1024)
    schema.add_field("status", DataType.VARCHAR, max_length=64)           # aktivan | istekao | draft
    schema.add_field("kategorija", DataType.VARCHAR, max_length=128)
    schema.add_field("datum_kreiranja", DataType.VARCHAR, max_length=64)
    schema.add_field("datum_poslednje_izmene", DataType.VARCHAR, max_length=64)
    schema.add_field("kampanja_id", DataType.INT64)

    # Vector fields
    schema.add_field(
        "text_embedding",
        DataType.FLOAT_VECTOR,
        dim=EMBEDDING_DIM,
        description="Embedding formiran iz naziva i opisa oglasa"
    )
    schema.add_field(
        "media_embedding",
        DataType.FLOAT_VECTOR,
        dim=EMBEDDING_DIM,
        description="Embedding slike za vizuelne oglase ili teksta za tekstualne"
    )

    return schema


def oglas_index_params(client: MilvusClient):
    index_params = client.prepare_index_params()

    # Scalar indexes
    index_params.add_index("tip_oglasa", index_type="INVERTED")
    index_params.add_index("status", index_type="INVERTED")
    index_params.add_index("kategorija", index_type="INVERTED")
    index_params.add_index("kampanja_id", index_type="INVERTED")

    # Vector indexes
    index_params.add_index(
        field_name="text_embedding",
        index_name="text_embedding_hnsw",
        index_type="HNSW",
        metric_type="COSINE",
        params={
            "M": HNSW_M,
            "efConstruction": HNSW_EF_CONSTRUCTION,
        }
    )

    index_params.add_index(
        field_name="media_embedding",
        index_name="media_embedding_hnsw",
        index_type="HNSW",
        metric_type="COSINE",
        params={
            "M": HNSW_M,
            "efConstruction": HNSW_EF_CONSTRUCTION,
        }
    )

    return index_params