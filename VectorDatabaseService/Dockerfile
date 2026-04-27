FROM python:3.10-slim

WORKDIR /app

# system deps (za Pillow, torch itd.)
RUN apt-get update && apt-get install -y \
    build-essential \
    libglib2.0-0 \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*

# kopiraj requirements
COPY requirements.txt .

# noviji pip bolje hendluje velike wheel-ove i indeks rezoluciju
RUN pip install --no-cache-dir --upgrade pip setuptools wheel

# torch instaliramo sa zvanicnog PyTorch CPU indeksa da izbegnemo
# hash mismatch koji se javlja na linux/arm64 wheel-u sa PyPI mirror-a
RUN pip install --no-cache-dir \
    --index-url https://download.pytorch.org/whl/cpu \
    torch

# ostale python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# kopiraj ceo projekat
COPY . .

# port
EXPOSE 8000

# entrypoint
CMD ["bash", "entrypoint.sh"]
