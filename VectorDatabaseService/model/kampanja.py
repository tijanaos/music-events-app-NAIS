from typing import Optional
from pydantic import BaseModel, Field


class KampanjaBase(BaseModel):
    naziv_kampanje: str = Field(..., max_length=512, description="Naziv kampanje")
    opis_kampanje: str = Field(..., max_length=4000, description="Opis kampanje")
    ciljna_grupa: str = Field(..., max_length=256, description="Ciljna grupa")
    kanal: str = Field(..., max_length=128, description="Kanal kampanje")
    budzet: float = Field(..., description="Budžet kampanje")
    status_kampanje: str = Field(..., max_length=64, description="Status kampanje")
    datum_pocetka: str = Field(..., description="ISO datum početka")
    datum_zavrsetka: str = Field(..., description="ISO datum završetka")


class KampanjaCreate(KampanjaBase):
    kampanja_id: int = Field(..., description="Primarni ključ kampanje")


class KampanjaUpdate(BaseModel):
    naziv_kampanje: Optional[str] = Field(None, max_length=512)
    opis_kampanje: Optional[str] = Field(None, max_length=4000)
    ciljna_grupa: Optional[str] = Field(None, max_length=256)
    kanal: Optional[str] = Field(None, max_length=128)
    budzet: Optional[float] = None
    status_kampanje: Optional[str] = Field(None, max_length=64)
    datum_pocetka: Optional[str] = None
    datum_zavrsetka: Optional[str] = None


class Kampanja(KampanjaBase):
    kampanja_id: int = Field(..., description="Primarni ključ kampanje")

    class Config:
        from_attributes = True


class KampanjaSearchResult(BaseModel):
    id: int | None = None
    kampanja_id: int
    score: float = Field(..., description="Score iz vector search-a")
    naziv_kampanje: str = ""
    opis_kampanje: str = ""
    ciljna_grupa: str = ""
    kanal: str = ""
    budzet: float = 0.0
    status_kampanje: str = ""
    datum_pocetka: str = ""
    datum_zavrsetka: str = ""


class KampanjaIteratorSearchRequest(BaseModel):
    query: str = Field(..., description="Semantički upit za kampanju")
    kanal: Optional[str] = None
    min_budzet: Optional[float] = None
    max_budzet: Optional[float] = None
    batch_size: int = Field(20, ge=1, le=100)
    limit: int = Field(100, ge=1, le=1000)

    model_config = {
        "json_schema_extra": {
            "example": {
                "query": "digitalna kampanja za tehnologiju",
                "kanal": "google_ads",
                "min_budzet": 3000,
                "max_budzet": 6000,
                "batch_size": 2,
                "limit": 10,
            }
        }
    }
