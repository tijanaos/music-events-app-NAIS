"""
One-time script to download and prepare the allenai/sciq dataset as a local Parquet file.

Downloads the first `--limit` rows (default 500) from the train split and saves them
to data/sciq_data.parquet.  Committing this file means docker compose up -d never
needs a network connection to HuggingFace at container startup.

Preprocessing applied here (mirrors ingest/sciq_ingest.py):
  - Skip rows where 'support' is empty or shorter than 20 chars.
  - Truncate fields to VARCHAR limits used in the Milvus schema.
  - Store support_length (char count) for range-filter demos.

"""

import argparse
import logging
import os
import sys

logging.basicConfig(level=logging.INFO, format="%(asctime)s  %(levelname)-8s %(message)s")
logger = logging.getLogger(__name__)

_ROOT          = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DEFAULT_OUTPUT = os.path.join(_ROOT, "data", "sciq_data.parquet")
DEFAULT_LIMIT  = 500
MIN_SUPPORT    = 20


def prepare(limit: int, output: str) -> None:
    try:
        import pyarrow as pa
        import pyarrow.parquet as pq
        from datasets import load_dataset
    except ImportError as exc:
        logger.error("Missing dependency: %s.  Run: pip install datasets pyarrow", exc)
        sys.exit(1)

    os.makedirs(os.path.dirname(output), exist_ok=True)

    logger.info("Streaming %d rows from 'allenai/sciq' (train split) ...", limit)
    ds = load_dataset("allenai/sciq", split="train", streaming=True)

    rows = []
    skipped = 0
    for i, row in enumerate(ds):
        if len(rows) >= limit:
            break

        support = (row.get("support") or "").strip()
        if len(support) < MIN_SUPPORT:
            skipped += 1
            continue

        rows.append({
            "doc_id":         i,
            "support_text":   support[:1999],
            "question":       (row.get("question") or "").strip()[:999],
            "correct_answer": (row.get("correct_answer") or "").strip()[:499],
            "support_length": len(support),
        })

        if len(rows) % 100 == 0:
            logger.info("  %d rows collected (skipped %d without support) ...", len(rows), skipped)

    table = pa.Table.from_pylist(rows)
    pq.write_table(table, output, compression="snappy")

    size_mb = os.path.getsize(output) / 1e6
    lengths = [r["support_length"] for r in rows]
    avg_len = sum(lengths) / len(lengths) if lengths else 0
    logger.info(
        "Saved %d rows → %s  (%.2f MB, avg support length %.0f chars, skipped %d)",
        len(rows), output, size_mb, avg_len, skipped,
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Prepare allenai/sciq as a local Parquet file.")
    parser.add_argument(
        "--limit", type=int, default=DEFAULT_LIMIT,
        help=f"Number of rows to download (default: {DEFAULT_LIMIT})",
    )
    parser.add_argument(
        "--output", default=DEFAULT_OUTPUT,
        help=f"Output Parquet path (default: {DEFAULT_OUTPUT})",
    )
    args = parser.parse_args()
    prepare(args.limit, args.output)
