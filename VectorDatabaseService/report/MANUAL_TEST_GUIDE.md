# VectorDatabaseService Manual Test Guide

Ovaj vodic koristi test podatke iz:

- `/Users/jovanavlaskalic/Desktop/NAIS/music-events-app-NAIS/VectorDatabaseService/data/test/kampanje.json`
- `/Users/jovanavlaskalic/Desktop/NAIS/music-events-app-NAIS/VectorDatabaseService/data/test/oglasi.json`
- `/Users/jovanavlaskalic/Desktop/NAIS/music-events-app-NAIS/VectorDatabaseService/data/test/blue_shoe.ppm`
- `/Users/jovanavlaskalic/Desktop/NAIS/music-events-app-NAIS/VectorDatabaseService/data/test/visual_ad_query_base64.txt`

## 1. Pokretanje servisa

Minimalni stack:

```bash
docker compose up --build etcd minio milvus vector-database-service
```

Ako hoces i Eureka + gateway:

```bash
docker compose up --build eureka-server gateway-api etcd minio milvus vector-database-service
```

Direktan pristup servisu ide na `http://localhost:8000`.
Preko gateway-ja ide na `http://localhost:9003/vector-database-service`.

## 2. Brza provera da je servis ziv

```bash
curl http://localhost:8000/health
curl http://localhost:8000/
```

## 3. Inicijalizacija kolekcija

```bash
curl -X POST http://localhost:8000/api/v1/kampanje/collection/init
curl -X POST http://localhost:8000/api/v1/oglasi/collection/init
```

Ako hoces da preskocis rucni unos test podataka, mozes da pokrenes seed skript:

```bash
bash VectorDatabaseService/ingest/seed_test_data.sh
```

Za gateway URL:

```bash
bash VectorDatabaseService/ingest/seed_test_data.sh http://localhost:9003/vector-database-service
```

Ako hoces da pre seed-a obrises postojece podatke:

```bash
RESET_FIRST=true bash VectorDatabaseService/ingest/seed_test_data.sh
```

## 4. Unos test kampanja

Fajl `kampanje.json` je referentni dataset. Ovaj endpoint prima jednu kampanju po pozivu, zato ih unesi jednu po jednu:

```bash
curl -X POST http://localhost:8000/api/v1/kampanje \
  -H "Content-Type: application/json" \
  -d '{
    "kampanja_id": 1,
    "naziv_kampanje": "Letnja moda 2026",
    "opis_kampanje": "Digitalna kampanja za promociju patika, majica i letnjih outfita.",
    "ciljna_grupa": "mladi kupci zainteresovani za modu i lifestyle",
    "kanal": "instagram",
    "budzet": 2500,
    "status_kampanje": "aktivna",
    "datum_pocetka": "2026-05-01",
    "datum_zavrsetka": "2026-06-15"
  }'

curl -X POST http://localhost:8000/api/v1/kampanje \
  -H "Content-Type: application/json" \
  -d '{
    "kampanja_id": 2,
    "naziv_kampanje": "Tech launch prolece",
    "opis_kampanje": "Digitalna kampanja za promociju novih tehnoloskih uredjaja i gadzeta.",
    "ciljna_grupa": "studenti i mladi profesionalci zainteresovani za tehnologiju",
    "kanal": "google_ads",
    "budzet": 4800,
    "status_kampanje": "aktivna",
    "datum_pocetka": "2026-04-20",
    "datum_zavrsetka": "2026-06-30"
  }'

curl -X POST http://localhost:8000/api/v1/kampanje \
  -H "Content-Type: application/json" \
  -d '{
    "kampanja_id": 3,
    "naziv_kampanje": "Stanovi Novi Sad",
    "opis_kampanje": "Kampanja za oglase nekretnina i izdavanje stanova u Novom Sadu.",
    "ciljna_grupa": "porodice i mladi parovi koji traze nekretnine",
    "kanal": "facebook",
    "budzet": 3200,
    "status_kampanje": "draft",
    "datum_pocetka": "2026-06-01",
    "datum_zavrsetka": "2026-08-01"
  }'
```

## 5. Unos test oglasa

```bash
curl -X POST http://localhost:8000/api/v1/oglasi \
  -H "Content-Type: application/json" \
  -d '{
    "oglas_id": 101,
    "naziv": "Letnja promocija patika",
    "opis": "Veliki popust na novu kolekciju patika za leto i gradske aktivnosti.",
    "tip_oglasa": "tekstualni",
    "content_url": "",
    "status": "aktivan",
    "kategorija": "moda",
    "datum_kreiranja": "2026-04-24",
    "datum_poslednje_izmene": "2026-04-24",
    "kampanja_id": 1
  }'

curl -X POST http://localhost:8000/api/v1/oglasi \
  -H "Content-Type: application/json" \
  -d '{
    "oglas_id": 102,
    "naziv": "Vizuelni oglas za plave patike",
    "opis": "Vizuelna kampanja za novu kolekciju plavih patika.",
    "tip_oglasa": "vizuelni",
    "content_url": "/app/data/test/blue_shoe.ppm",
    "status": "aktivan",
    "kategorija": "moda",
    "datum_kreiranja": "2026-04-24",
    "datum_poslednje_izmene": "2026-04-24",
    "kampanja_id": 1
  }'

curl -X POST http://localhost:8000/api/v1/oglasi \
  -H "Content-Type: application/json" \
  -d '{
    "oglas_id": 103,
    "naziv": "Laptop akcija za studente",
    "opis": "Popust na lagane laptopove i dodatnu opremu za fakultet i posao.",
    "tip_oglasa": "tekstualni",
    "content_url": "",
    "status": "aktivan",
    "kategorija": "tehnika",
    "datum_kreiranja": "2026-04-24",
    "datum_poslednje_izmene": "2026-04-24",
    "kampanja_id": 2
  }'

curl -X POST http://localhost:8000/api/v1/oglasi \
  -H "Content-Type: application/json" \
  -d '{
    "oglas_id": 104,
    "naziv": "Stan na izdavanje Liman",
    "opis": "Dvosoban stan u blizini fakulteta sa terasom i parkingom.",
    "tip_oglasa": "tekstualni",
    "content_url": "",
    "status": "draft",
    "kategorija": "nekretnine",
    "datum_kreiranja": "2026-04-24",
    "datum_poslednje_izmene": "2026-04-24",
    "kampanja_id": 3
  }'

curl -X POST http://localhost:8000/api/v1/oglasi \
  -H "Content-Type: application/json" \
  -d '{
    "oglas_id": 105,
    "naziv": "Zimska rasprodaja jakni",
    "opis": "Jakne i duksevi po snizenim cenama za kraj sezone.",
    "tip_oglasa": "tekstualni",
    "content_url": "",
    "status": "istekao",
    "kategorija": "moda",
    "datum_kreiranja": "2026-01-10",
    "datum_poslednje_izmene": "2026-02-15",
    "kampanja_id": 1
  }'
```

## 6. Prosti upiti nad oglasima

Dobavljanje po ID-u:

```bash
curl http://localhost:8000/api/v1/oglasi/101
```

Prebrojavanje po uslovu:

```bash
curl "http://localhost:8000/api/v1/oglasi/count?tip_oglasa=vizuelni&status=aktivan"
curl "http://localhost:8000/api/v1/oglasi/count?tip_oglasa=tekstualni&status=aktivan&kategorija=moda"
```

Listanje po scalar filterima:

```bash
curl "http://localhost:8000/api/v1/oglasi?status=aktivan&kategorija=moda&limit=10&offset=0"
```

## 7. Slozeni upit: semanticka pretraga + najmanje 2 filtera

```bash
curl -X POST http://localhost:8000/api/v1/oglasi/search/semantic-filtered \
  -H "Content-Type: application/json" \
  -d '{
    "query": "letnja promocija patika",
    "tip_oglasa": "tekstualni",
    "status": "aktivan",
    "kategorija": "moda",
    "top_k": 5
  }'
```

Ocekivanje:

- `oglas_id=101` treba da bude vrlo visoko rangiran
- `oglas_id=105` ne treba da se pojavi jer je `istekao`

## 8. Multi-vector hybrid search nad oglasima

Koristi lokalnu test sliku iz fajla `visual_ad_query_base64.txt`.

```bash
curl -X POST http://localhost:8000/api/v1/oglasi/search/hybrid \
  -H "Content-Type: application/json" \
  -d "{
    \"text_query\": \"letnja promocija patika\",
    \"image_base64\": \"$(tr -d '\n' < VectorDatabaseService/data/test/visual_ad_query_base64.txt)\",
    \"tip_oglasa\": \"vizuelni\",
    \"status\": \"aktivan\",
    \"kategorija\": \"moda\",
    \"top_k\": 5
  }"
```

Ocekivanje:

- `oglas_id=102` treba da se pojavi kao relevantan rezultat
- rezultat treba da bude filtriran samo na `vizuelni`, `aktivan`, `moda`

## 9. Prosti i slozeni upiti nad kampanjama

Dobavljanje kampanje po ID-u:

```bash
curl http://localhost:8000/api/v1/kampanje/2
```

Listanje po scalar uslovima:

```bash
curl "http://localhost:8000/api/v1/kampanje?status_kampanje=aktivna&kanal=instagram"
curl "http://localhost:8000/api/v1/kampanje?kanal=google_ads&min_budzet=3000&max_budzet=6000"
```

Count:

```bash
curl "http://localhost:8000/api/v1/kampanje/count?status_kampanje=aktivna"
```

Semanticka pretraga kampanja:

```bash
curl "http://localhost:8000/api/v1/kampanje/search/semantic?query=digitalna%20kampanja%20za%20tehnologiju&kanal=google_ads&min_budzet=3000&max_budzet=6000&top_k=5"
```

Ocekivanje:

- `kampanja_id=2` treba da bude visoko rangirana

## 10. Iterator pretraga kampanja

```bash
curl -X POST http://localhost:8000/api/v1/kampanje/search/iterator \
  -H "Content-Type: application/json" \
  -d '{
    "query": "digitalna kampanja za tehnologiju",
    "kanal": "google_ads",
    "min_budzet": 3000,
    "max_budzet": 6000,
    "batch_size": 2,
    "limit": 10
  }'
```

Ocekivanje:

- `kampanja_id=2` treba da bude u rezultatima
- servis interno koristi iterator, ali odgovor vraca kao objedinjenu listu

## 11. Provera preko gateway-ja

Ako si podigao i `gateway-api`, iste pozive mozes gadjati ovako:

```bash
curl http://localhost:9003/vector-database-service/health
curl http://localhost:9003/vector-database-service/api/v1/oglasi/101
```

## 12. Reset kolekcija za ponovno testiranje

```bash
curl -X DELETE http://localhost:8000/api/v1/oglasi/collection/reset
curl -X DELETE http://localhost:8000/api/v1/kampanje/collection/reset
```
