# Real Data Format

Ovaj folder služi za čuvanje stvarnih podataka koje želiš da ubaciš u Milvus i da kasnije ponovo koristiš.

## Fajlovi

- `kampanje.jsonl`
- `oglasi.jsonl`
- `images/` — lokalne slike koje oglasi koriste preko `content_url`
- `queries/` — opciono, slike koje želiš da koristiš za ručno testiranje pretrage

## JSONL format

Svaki red u `.jsonl` fajlu je jedan JSON objekat.

### `kampanje.jsonl`

```json
{"kampanja_id": 1, "naziv_kampanje": "Digitalna tech kampanja", "opis_kampanje": "Promocija novih uredjaja", "ciljna_grupa": "studenti i mladi profesionalci", "kanal": "google_ads", "budzet": 5000, "status_kampanje": "aktivna", "datum_pocetka": "2026-05-01", "datum_zavrsetka": "2026-07-01"}
```

### `oglasi.jsonl`

Za tekstualni oglas:

```json
{"oglas_id": 101, "naziv": "Popust na laptopove", "opis": "Veliki izbor laptopova za posao i studije.", "tip_oglasa": "tekstualni", "content_url": "", "status": "aktivan", "kategorija": "tehnika", "datum_kreiranja": "2026-05-01", "datum_poslednje_izmene": "2026-05-01", "kampanja_id": 1}
```

Za vizuelni oglas sa pravom slikom:

```json
{"oglas_id": 102, "naziv": "Plave patike", "opis": "Vizuelna kampanja za novu kolekciju patika.", "tip_oglasa": "vizuelni", "content_url": "images/plave_patike.jpg", "status": "aktivan", "kategorija": "moda", "datum_kreiranja": "2026-05-01", "datum_poslednje_izmene": "2026-05-01", "kampanja_id": 1}
```

Relativni `content_url` se tokom ingest-a automatski prevodi na apsolutnu putanju unutar foldera `data/real`.

## Ingest

```bash
python3 VectorDatabaseService/ingest/local_real_data_ingest.py --reset
```

## Minimum

Za zahtev projekta ciljaj najmanje:

- 200 kampanja
- 200 oglasa

Poželjno je da deo oglasa bude `vizuelni` i da stvarno ima slike u `images/`.
