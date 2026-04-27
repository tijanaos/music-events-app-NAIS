import argparse
import json
import os
import sys
from pathlib import Path

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from model.kampanja import KampanjaCreate
from model.oglas import OglasCreate
from service.impl.kampanja_service import kampanja_service
from service.impl.oglas_service import oglas_service


ROOT = Path(__file__).resolve().parents[1]
REAL_DATA_DIR = ROOT / "data" / "real"
KAMPANJE_PATH = REAL_DATA_DIR / "kampanje.jsonl"
OGLASI_PATH = REAL_DATA_DIR / "oglasi.jsonl"
CONTAINER_REAL_DATA_DIR = Path("/app/data/real")


def _read_jsonl(path: Path) -> list[dict]:
    if not path.exists():
        raise FileNotFoundError(f"Missing file: {path}")

    raw_content = path.read_text(encoding="utf-8").strip()
    if not raw_content:
        return []

    if raw_content.startswith("["):
        data = json.loads(raw_content)
        if not isinstance(data, list):
            raise ValueError(f"Expected JSON array in {path}")
        return data

    rows = []
    with path.open("r", encoding="utf-8") as handle:
        for line_no, raw_line in enumerate(handle, start=1):
            line = raw_line.strip()
            if not line:
                continue
            try:
                rows.append(json.loads(line))
            except json.JSONDecodeError as exc:
                raise ValueError(f"Invalid JSON on line {line_no} in {path}: {exc}") from exc
    return rows


def _resolve_content_url(record: dict) -> dict:
    content_url = (record.get("content_url") or "").strip()
    if not content_url:
        return record

    if content_url.startswith(("http://", "https://", "/app/")):
        return record

    record = dict(record)
    record["content_url"] = str((CONTAINER_REAL_DATA_DIR / content_url).as_posix())
    return record


def ingest(reset: bool = False) -> None:
    if reset:
        kampanja_service.reset_collection()
        oglas_service.reset_collection()
    else:
        kampanja_service.ensure_collection()
        oglas_service.ensure_collection()

    kampanje = _read_jsonl(KAMPANJE_PATH)
    oglasi = [_resolve_content_url(row) for row in _read_jsonl(OGLASI_PATH)]

    print(f"Loaded {len(kampanje)} kampanje and {len(oglasi)} oglasi from local manifests.")
    if len(kampanje) < 200 or len(oglasi) < 200:
        print("Warning: requirement is at least 200 records per collection.")

    inserted_kampanje = 0
    for row in kampanje:
        payload = KampanjaCreate(**row)
        try:
            kampanja_service.create_kampanja(payload)
            inserted_kampanje += 1
        except ValueError:
            pass

    inserted_oglasi = 0
    for row in oglasi:
        payload = OglasCreate(**row)
        try:
            oglas_service.create_oglas(payload)
            inserted_oglasi += 1
        except ValueError:
            pass

    print(f"Inserted kampanje: {inserted_kampanje}")
    print(f"Inserted oglasi: {inserted_oglasi}")
    print(f"Total kampanje in manifest: {len(kampanje)}")
    print(f"Total oglasi in manifest: {len(oglasi)}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Ingest real local data into kampanje and oglasi collections.")
    parser.add_argument("--reset", action="store_true", help="Reset collections before ingest.")
    args = parser.parse_args()
    ingest(reset=args.reset)
