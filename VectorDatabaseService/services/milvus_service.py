from pymilvus import MilvusClient
from config import MILVUS_URI


class MilvusService:
    def __init__(self):
        print(f"[MilvusService] Connecting to: {MILVUS_URI}")
        self.client = MilvusClient(uri=MILVUS_URI)

    # ─────────────────────────────────────────────────────────────
    # COLLECTION MANAGEMENT
    # ─────────────────────────────────────────────────────────────

    def ensure_collection(self, name, schema, index_params):
        if self.client.has_collection(name):
            print(f"[MilvusService] Collection '{name}' already exists.")
            return

        print(f"[MilvusService] Creating collection '{name}'...")

        self.client.create_collection(
            collection_name=name,
            schema=schema,
            index_params=index_params,
        )

        print(f"[MilvusService] Collection '{name}' created.")

    def drop_and_recreate(self, name, schema, index_params):
        if self.client.has_collection(name):
            print(f"[MilvusService] Dropping collection '{name}'...")
            self.client.drop_collection(name)

        print(f"[MilvusService] Recreating collection '{name}'...")

        self.client.create_collection(
            collection_name=name,
            schema=schema,
            index_params=index_params,
        )

    def list_collections(self):
        return self.client.list_collections()


# Singleton
milvus_service = MilvusService()