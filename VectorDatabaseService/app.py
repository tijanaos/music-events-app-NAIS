import logging

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import py_eureka_client.eureka_client as eureka_client

from config import APP_INSTANCE_HOST, APP_NAME, APP_PORT, EUREKA_ENABLED, EUREKA_SERVER
from controller.health_controller import router as health_router
from controller.oglas_controller import router as oglas_router
from controller.kampanja_controller import router as kampanja_router
from model.common import RootResponse
from service.impl.oglas_service import oglas_service
from service.impl.kampanja_service import kampanja_service

logger = logging.getLogger(__name__)

app = FastAPI(
    title=APP_NAME,
    description="Mikroservis za upravljanje oglasima i kampanjama u vektorskoj bazi. Swagger dokumentacija je dostupna na /docs.",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
async def startup():
    oglas_service.ensure_collection()
    kampanja_service.ensure_collection()

    if not EUREKA_ENABLED:
        logger.info("Eureka registration is disabled.")
        return

    eureka_kwargs = {
        "eureka_server": EUREKA_SERVER,
        "app_name": APP_NAME,
        "instance_port": APP_PORT,
    }
    if APP_INSTANCE_HOST:
        eureka_kwargs["instance_host"] = APP_INSTANCE_HOST

    await eureka_client.init_async(**eureka_kwargs)
    logger.info("Registered %s with Eureka at %s", APP_NAME, EUREKA_SERVER)


@app.on_event("shutdown")
async def shutdown():
    if not EUREKA_ENABLED:
        return

    client = eureka_client.get_client()
    if client is None:
        return

    await eureka_client.stop_async()
    logger.info("Unregistered %s from Eureka", APP_NAME)


@app.get(
    "/",
    response_model=RootResponse,
    summary="Osnovne informacije o servisu",
    description="Vraća naziv servisa, status i listu podržanih kolekcija.",
)
def root():
    return {
        "service": APP_NAME,
        "status": "running",
        "collections": ["oglasi", "kampanje"],
    }
app.include_router(health_router)
app.include_router(oglas_router)
app.include_router(kampanja_router)
