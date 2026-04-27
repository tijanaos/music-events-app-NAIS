#!/bin/bash

echo "Starting Oglas Vector Service..."

# čekaj Milvus (da ne pukne odmah)
sleep 5

uvicorn app:app --host 0.0.0.0 --port "${APP_PORT:-8000}"
