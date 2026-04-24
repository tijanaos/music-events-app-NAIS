#!/usr/bin/env bash

set -uo pipefail

BASE_URL="${1:-http://localhost:8000}"
IMAGE_BASE64_FILE="VectorDatabaseService/data/test/visual_ad_query_base64.txt"

pass_count=0
fail_count=0

print_result() {
  local name="$1"
  local ok="$2"

  if [[ "${ok}" == "0" ]]; then
    echo "[PASS] ${name}"
    pass_count=$((pass_count + 1))
  else
    echo "[FAIL] ${name}"
    fail_count=$((fail_count + 1))
  fi
}

test_get_contains() {
  local name="$1"
  local path="$2"
  local needle="$3"

  echo
  echo "[TEST] ${name}"

  local response
  if ! response="$(curl --silent --show-error --fail "${BASE_URL}${path}")"; then
    print_result "${name}" "1"
    return
  fi

  echo "${response}"

  if [[ "${response}" == *"${needle}"* ]]; then
    print_result "${name}" "0"
  else
    print_result "${name}" "1"
  fi
}

test_post_contains() {
  local name="$1"
  local path="$2"
  local payload="$3"
  local needle="$4"

  echo
  echo "[TEST] ${name}"

  local response
  if ! response="$(curl --silent --show-error --fail \
    -X POST "${BASE_URL}${path}" \
    -H "Content-Type: application/json" \
    -d "${payload}")"; then
    print_result "${name}" "1"
    return
  fi

  echo "${response}"

  if [[ "${response}" == *"${needle}"* ]]; then
    print_result "${name}" "0"
  else
    print_result "${name}" "1"
  fi
}

echo "Running query checks against ${BASE_URL}"

test_get_contains \
  "Health endpoint" \
  "/health" \
  "\"status\":\"ok\""

test_get_contains \
  "Kampanja 1 exists" \
  "/api/v1/kampanje/1" \
  "\"kampanja_id\":1"

test_get_contains \
  "Oglas 101 exists" \
  "/api/v1/oglasi/101" \
  "\"oglas_id\":101"

test_get_contains \
  "Oglasi count for moda/tekstualni/aktivan" \
  "/api/v1/oglasi/count?tip_oglasa=tekstualni&status=aktivan&kategorija=moda" \
  "\"count\":1"

test_get_contains \
  "Active kampanje count" \
  "/api/v1/kampanje/count?status_kampanje=aktivna" \
  "\"count\":2"

test_get_contains \
  "Filter kampanje by kanal and budget" \
  "/api/v1/kampanje?kanal=google_ads&min_budzet=3000&max_budzet=6000" \
  "\"kampanja_id\":2"

test_get_contains \
  "Semantic kampanja search returns kampanja 2" \
  "/api/v1/kampanje/search/semantic?query=digitalna%20kampanja%20za%20tehnologiju&kanal=google_ads&min_budzet=3000&max_budzet=6000&top_k=5" \
  "\"kampanja_id\":2"

test_post_contains \
  "Iterator kampanja search returns kampanja 2" \
  "/api/v1/kampanje/search/iterator" \
  '{
    "query": "digitalna kampanja za tehnologiju",
    "kanal": "google_ads",
    "min_budzet": 3000,
    "max_budzet": 6000,
    "batch_size": 2,
    "limit": 10
  }' \
  "\"kampanja_id\":2"

test_post_contains \
  "Semantic oglas search returns oglas 101" \
  "/api/v1/oglasi/search/semantic-filtered" \
  '{
    "query": "letnja promocija patika",
    "tip_oglasa": "tekstualni",
    "status": "aktivan",
    "kategorija": "moda",
    "top_k": 5
  }' \
  "\"oglas_id\":101"

if [[ ! -f "${IMAGE_BASE64_FILE}" ]]; then
  echo
  echo "[TEST] Hybrid oglas search returns visual oglas 102"
  echo "[FAIL] Hybrid oglas search returns visual oglas 102"
  echo "Missing file: ${IMAGE_BASE64_FILE}"
  fail_count=$((fail_count + 1))
else
  IMAGE_BASE64="$(tr -d '\n' < "${IMAGE_BASE64_FILE}")"
  HYBRID_PAYLOAD="$(cat <<EOF
{
  "text_query": "letnja promocija patika",
  "image_base64": "${IMAGE_BASE64}",
  "tip_oglasa": "vizuelni",
  "status": "aktivan",
  "kategorija": "moda",
  "top_k": 5
}
EOF
)"

  test_post_contains \
    "Hybrid oglas search returns visual oglas 102" \
    "/api/v1/oglasi/search/hybrid" \
    "${HYBRID_PAYLOAD}" \
    "\"oglas_id\":102"
fi

echo
echo "Finished."
echo "Passed: ${pass_count}"
echo "Failed: ${fail_count}"

if [[ "${fail_count}" -gt 0 ]]; then
  exit 1
fi
