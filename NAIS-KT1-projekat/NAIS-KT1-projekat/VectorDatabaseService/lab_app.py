import logging
import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from controller.fashion_lab_controller import router as lab_router
from controller.sciq_controller import router as sciq_router
from controller.chat_controller import router as chat_router

logging.basicConfig(level=logging.INFO, format="%(asctime)s  %(levelname)-8s  %(name)s  %(message)s")

app = FastAPI(
    title="Vector Database Service — Lab",
    description="Multimodal fashion search API. Dataset: ashraq/fashion-product-images-small (1000 rows). Embedding: CLIP ViT-B/32 (512-dim).",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
)

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])
app.include_router(lab_router)
app.include_router(sciq_router)
app.include_router(chat_router)


@app.get("/health", tags=["Health"])
def health():
    return {"status": "ok", "collection": "fashion_lab"}


if __name__ == "__main__":
    uvicorn.run("lab_app:app", host="0.0.0.0", port=8000, reload=False)
