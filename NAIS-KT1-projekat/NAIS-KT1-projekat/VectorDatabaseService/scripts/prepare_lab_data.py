"""
One-time script for laboratory dataset preparation.

Downloads the first `--limit` rows (default 1000) from the HuggingFace dataset
ashraq/fashion-product-images-small and saves them as a local Parquet file.

Images are resized to 224×224 px (CLIP's native resolution) before serialization,
keeping the file compact (~10-15 MB for 1000 rows).
"""

import argparse
import io
import logging
import os
import sys

logging.basicConfig(level=logging.INFO, format="%(asctime)s  %(levelname)-8s %(message)s")
logger = logging.getLogger(__name__)

_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DEFAULT_OUTPUT = os.path.join(_ROOT, "data", "fashion_lab.parquet")
DEFAULT_LIMIT  = 1000
# CLIP internally uses 224×224 — no point in storing larger images
IMAGE_SIZE = 224


def _to_jpeg_bytes(pil_img, size: int = IMAGE_SIZE, quality: int = 85):
    """Resizes a PIL image to `size`×`size` and returns JPEG bytes (or None)."""
    if pil_img is None:
        return None
    try:
        from PIL import Image as PILImage
        img = pil_img.convert("RGB").resize((size, size), PILImage.LANCZOS)
        buf = io.BytesIO()
        img.save(buf, format="JPEG", quality=quality, optimize=True)
        return buf.getvalue()
    except Exception as exc:
        logger.debug("Image conversion failed: %s", exc)
        return None


def prepare(limit: int, output: str) -> None:
    try:
        import pyarrow as pa
        import pyarrow.parquet as pq
        from datasets import load_dataset
    except ImportError as exc:
        logger.error("Missing dependency: %s. Run: pip install datasets Pillow pyarrow", exc)
        sys.exit(1)

    os.makedirs(os.path.dirname(output), exist_ok=True)

    logger.info("Streaming %d rows from 'ashraq/fashion-product-images-small' …", limit)
    ds = load_dataset("ashraq/fashion-product-images-small", split="train", streaming=True)

    rows = []
    for i, row in enumerate(ds):
        if i >= limit:
            break

        rows.append({
            "product_id":      int(row.get("id") or 0),
            "product_name":    str(row.get("productDisplayName") or "").strip()[:511],
            "gender":          str(row.get("gender")         or "")[:31],
            "master_category": str(row.get("masterCategory") or "")[:63],
            "sub_category":    str(row.get("subCategory")    or "")[:63],
            "article_type":    str(row.get("articleType")    or "")[:63],
            "base_colour":     str(row.get("baseColour")     or "")[:63],
            "season":          str(row.get("season")         or "")[:31],
            "year":            int(row.get("year") or 0),
            "usage":           str(row.get("usage")          or "")[:63],
            "image_bytes":     _to_jpeg_bytes(row.get("image")),
        })

        if (i + 1) % 100 == 0:
            images_ok = sum(1 for r in rows if r["image_bytes"] is not None)
            logger.info("  %d rows collected, %d with images …", i + 1, images_ok)

    table = pa.Table.from_pylist(rows)
    pq.write_table(table, output, compression="snappy")

    size_mb = os.path.getsize(output) / 1e6
    images_ok = sum(1 for r in rows if r["image_bytes"] is not None)
    logger.info(
        "Saved %d rows → %s  (%.1f MB, %d/%d images)",
        len(rows), output, size_mb, images_ok, len(rows),
    )


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Prepare a local lab dataset (Parquet) from a HuggingFace dataset."
    )
    parser.add_argument(
        "--limit", type=int, default=DEFAULT_LIMIT,
        help=f"Number of rows to download (default: {DEFAULT_LIMIT})",
    )
    parser.add_argument(
        "--output", default=DEFAULT_OUTPUT,
        help=f"Output path for the Parquet file (default: {DEFAULT_OUTPUT})",
    )
    args = parser.parse_args()
    prepare(args.limit, args.output)