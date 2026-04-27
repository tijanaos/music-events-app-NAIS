from pymilvus import DataType, MilvusClient
from config import EMBEDDING_DIM, HNSW_M, HNSW_EF_CONSTRUCTION

KAMPANJA_SCALAR_OUTPUT_FIELDS = [
    "kampanja_id",
    "naziv_kampanje",
    "opis_kampanje",
    "ciljna_grupa",
    "kanal",
    "budzet",
    "status_kampanje",
    "datum_pocetka",
    "datum_zavrsetka",
]

def kampanja_schema(client: MilvusClient):
    schema = client.create_schema(
        auto_id=False,
        enable_dynamic_fields=False,
        description=(
            "Kolekcija kampanja. "
            "campaign_embedding = embedding(naziv_kampanje + opis_kampanje + ciljna_grupa)"
        ),
    )

    # Primary key
    schema.add_field(
        "kampanja_id",
        DataType.INT64,
        is_primary=True,
        description="Primarni ključ kampanje"
    )

    # Scalar fields
    schema.add_field("naziv_kampanje", DataType.VARCHAR, max_length=512)
    schema.add_field("opis_kampanje", DataType.VARCHAR, max_length=4000)
    schema.add_field("ciljna_grupa", DataType.VARCHAR, max_length=256)
    schema.add_field("kanal", DataType.VARCHAR, max_length=128)
    schema.add_field("budzet", DataType.DOUBLE)
    schema.add_field("status_kampanje", DataType.VARCHAR, max_length=64)
    schema.add_field("datum_pocetka", DataType.VARCHAR, max_length=64)
    schema.add_field("datum_zavrsetka", DataType.VARCHAR, max_length=64)

    # Vector field
    schema.add_field(
        "campaign_embedding",
        DataType.FLOAT_VECTOR,
        dim=EMBEDDING_DIM,
        description="Embedding formiran iz naziva kampanje, opisa i ciljne grupe"
    )

    return schema


def kampanja_index_params(client: MilvusClient):
    index_params = client.prepare_index_params()

    # Scalar indexes
    index_params.add_index("status_kampanje", index_type="INVERTED")
    index_params.add_index("kanal", index_type="INVERTED")
    index_params.add_index("ciljna_grupa", index_type="INVERTED")

    # Vector index
    index_params.add_index(
        field_name="campaign_embedding",
        index_name="campaign_embedding_hnsw",
        index_type="HNSW",
        metric_type="COSINE",
        params={
            "M": HNSW_M,
            "efConstruction": HNSW_EF_CONSTRUCTION,
        }
    )

    return index_params