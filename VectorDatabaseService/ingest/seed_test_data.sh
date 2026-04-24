#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${1:-http://localhost:8000}"
RESET_FIRST="${RESET_FIRST:-false}"

post_json() {
  local path="$1"
  local payload="$2"

  curl --silent --show-error --fail \
    -X POST "${BASE_URL}${path}" \
    -H "Content-Type: application/json" \
    -d "${payload}"

  printf '\n'
}

delete_call() {
  local path="$1"

  curl --silent --show-error --fail \
    -X DELETE "${BASE_URL}${path}"

  printf '\n'
}

get_call() {
  local path="$1"

  curl --silent --show-error --fail "${BASE_URL}${path}"
  printf '\n'
}

echo "Checking service health at ${BASE_URL}..."
get_call "/health"

if [[ "${RESET_FIRST}" == "true" ]]; then
  echo "Resetting collections..."
  delete_call "/api/v1/oglasi/collection/reset"
  delete_call "/api/v1/kampanje/collection/reset"
fi

echo "Initializing collections..."
post_json "/api/v1/kampanje/collection/init" '{}'
post_json "/api/v1/oglasi/collection/init" '{}'

echo "Seeding kampanje..."
post_json "/api/v1/kampanje" '{
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

post_json "/api/v1/kampanje" '{
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

post_json "/api/v1/kampanje" '{
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

echo "Seeding oglasi..."
post_json "/api/v1/oglasi" '{
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

post_json "/api/v1/oglasi" '{
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

post_json "/api/v1/oglasi" '{
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

post_json "/api/v1/oglasi" '{
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

post_json "/api/v1/oglasi" '{
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

echo "Seed completed."
echo "Examples:"
echo "  curl ${BASE_URL}/api/v1/oglasi/101"
echo "  curl \"${BASE_URL}/api/v1/oglasi/count?tip_oglasa=tekstualni&status=aktivan&kategorija=moda\""
echo "  curl \"${BASE_URL}/api/v1/kampanje/search/semantic?query=digitalna%20kampanja%20za%20tehnologiju&kanal=google_ads&min_budzet=3000&max_budzet=6000&top_k=5\""
