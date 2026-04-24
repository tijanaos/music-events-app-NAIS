import base64
import io
import requests
from typing import List
import threading

from PIL import Image
from sentence_transformers import SentenceTransformer
from config import EMBEDDING_MODEL_NAME, IMAGE_DOWNLOAD_TIMEOUT


class EmbeddingService:
    """
    Servis za generisanje embeddinga.

    Podržava:
    - tekst → embedding
    - slika (URL ili lokalno) → embedding
    - base64 slika → embedding

    Koristi CLIP model (ili drugi SentenceTransformer model).
    """

    def __init__(self):
        self.model = None
        self._model_lock = threading.Lock()

    def _get_model(self) -> SentenceTransformer:
        if self.model is None:
            with self._model_lock:
                if self.model is None:
                    print(f"[EmbeddingService] Učitavam model: {EMBEDDING_MODEL_NAME}")
                    self.model = SentenceTransformer(EMBEDDING_MODEL_NAME)

        return self.model

    # ─────────────────────────────────────────────────────────────────────────
    # TEXT EMBEDDING
    # ─────────────────────────────────────────────────────────────────────────

    def encode_text_one(self, text: str) -> List[float]:
        if not text or not text.strip():
            raise ValueError("Tekst za embedding je prazan.")

        embedding = self._get_model().encode(text, normalize_embeddings=True)

        return embedding.tolist()

    def encode_text_batch(self, texts: List[str]) -> List[List[float]]:
        embeddings = self._get_model().encode(texts, normalize_embeddings=True)
        return [e.tolist() for e in embeddings]

    # ─────────────────────────────────────────────────────────────────────────
    # IMAGE EMBEDDING
    # ─────────────────────────────────────────────────────────────────────────

    def encode_image(self, image: Image.Image) -> List[float]:
        embedding = self._get_model().encode(image, normalize_embeddings=True)
        return embedding.tolist()

    def encode_image_from_path_or_url(self, path_or_url: str) -> List[float]:
        image = self._load_image(path_or_url)
        return self.encode_image(image)

    def encode_image_base64(self, image_base64: str) -> List[float]:
        try:
            image_bytes = base64.b64decode(image_base64)
            image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        except Exception as e:
            raise ValueError(f"Neispravan base64 image: {e}")

        return self.encode_image(image)

    # ─────────────────────────────────────────────────────────────────────────
    # IMAGE LOADING
    # ─────────────────────────────────────────────────────────────────────────

    def _load_image(self, path_or_url: str) -> Image.Image:
        """
        Učitava sliku:
        - ako je URL → skida je
        - ako je lokalni path → učitava sa diska
        """

        if path_or_url.startswith("http://") or path_or_url.startswith("https://"):
            return self._load_image_from_url(path_or_url)
        else:
            return self._load_image_from_file(path_or_url)

    def _load_image_from_url(self, url: str) -> Image.Image:
        try:
            response = requests.get(url, timeout=IMAGE_DOWNLOAD_TIMEOUT)
            response.raise_for_status()

            image = Image.open(io.BytesIO(response.content)).convert("RGB")
            return image

        except Exception as e:
            raise ValueError(f"Greška pri učitavanju slike sa URL-a: {e}")

    def _load_image_from_file(self, path: str) -> Image.Image:
        try:
            image = Image.open(path).convert("RGB")
            return image

        except Exception as e:
            raise ValueError(f"Greška pri učitavanju lokalne slike: {e}")


# Singleton instance
embedding_service = EmbeddingService()
