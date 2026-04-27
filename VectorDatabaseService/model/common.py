from pydantic import BaseModel, Field


class RootResponse(BaseModel):
    service: str = Field(..., description="Naziv servisa")
    status: str = Field(..., description="Status servisa")
    collections: list[str] = Field(..., description="Kolekcije dostupne u servisu")


class HealthResponse(BaseModel):
    status: str = Field(..., description="Status health provere")


class CountResponse(BaseModel):
    filter: str = Field(..., description="Primenjeni filter izraz")
    count: int = Field(..., description="Broj pronađenih rezultata")


class CollectionActionResponse(BaseModel):
    message: str = Field(..., description="Rezultat operacije nad kolekcijom")
