from typing import Optional
from pydantic import BaseModel, Field


class OglasBase(BaseModel):
    naziv: str = Field(..., max_length=512, description="Naziv oglasa")
    opis: str = Field(..., max_length=4000, description="Opis oglasa")
    tip_oglasa: str = Field(..., max_length=64, description="tekstualni | vizuelni")
    content_url: Optional[str] = Field(None, description="Putanja do slike ili tekstualnog fajla")
    status: str = Field(..., max_length=64, description="aktivan | istekao | draft")
    kategorija: str = Field(..., max_length=128, description="moda | tehnika | nekretnine | ...")
    datum_kreiranja: str = Field(..., description="ISO datum kreiranja")
    datum_poslednje_izmene: str = Field(..., description="ISO datum poslednje izmene")
    kampanja_id: int = Field(..., description="Veza ka kampanji")


class OglasCreate(OglasBase):
    oglas_id: int = Field(..., description="Primarni ključ oglasa")


class OglasUpdate(BaseModel):
    naziv: Optional[str] = Field(None, max_length=512)
    opis: Optional[str] = Field(None, max_length=4000)
    tip_oglasa: Optional[str] = Field(None, max_length=64)
    content_url: Optional[str] = None
    status: Optional[str] = Field(None, max_length=64)
    kategorija: Optional[str] = Field(None, max_length=128)
    datum_kreiranja: Optional[str] = None
    datum_poslednje_izmene: Optional[str] = None
    kampanja_id: Optional[int] = None


class Oglas(OglasBase):
    oglas_id: int = Field(..., description="Primarni ključ oglasa")

    class Config:
        from_attributes = True


class OglasSearchResult(BaseModel):
    id: int | None = None
    oglas_id: int
    score: float = Field(..., description="Score iz vector search-a")
    naziv: str = ""
    opis: str = ""
    tip_oglasa: str = ""
    content_url: Optional[str] = None
    status: str = ""
    kategorija: str = ""
    datum_kreiranja: str = ""
    datum_poslednje_izmene: str = ""
    kampanja_id: int = 0
    fused_score: Optional[float] = Field(None, description="Score nakon fuzije / rerankinga")


class OglasHybridSearchRequest(BaseModel):
    text_query: str = Field(..., description="Tekstualni upit")
    image_base64: str = Field(..., description="Base64 enkodovana slika")
    tip_oglasa: Optional[str] = None
    status: Optional[str] = None
    kategorija: Optional[str] = None
    top_k: int = Field(10, ge=1, le=100)

    model_config = {
        "json_schema_extra": {
            "example": {
                "text_query": "letnja promocija patika",
                "image_base64": "<base64-encoded-image>",
                "tip_oglasa": "vizuelni",
                "status": "aktivan",
                "kategorija": "moda",
                "top_k": 5,
            }
        }
    }


class OglasSemanticFilterRequest(BaseModel):
    query: str = Field(..., description="Semantički tekstualni upit")
    tip_oglasa: str = Field(..., description="tip oglasa")
    status: str = Field(..., description="status oglasa")
    kategorija: str = Field(..., description="kategorija oglasa")
    top_k: int = Field(10, ge=1, le=100)

    model_config = {
        "json_schema_extra": {
            "example": {
                "query": "letnja promocija patika",
                "tip_oglasa": "tekstualni",
                "status": "aktivan",
                "kategorija": "moda",
                "top_k": 5,
            }
        }
    }
