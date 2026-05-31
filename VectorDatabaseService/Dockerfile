FROM python:3.10-slim-bookworm

WORKDIR /app

ENV PIP_DEFAULT_TIMEOUT=300 \
    PIP_DISABLE_PIP_VERSION_CHECK=1

# kopiraj requirements
COPY requirements.txt .

# noviji pip bolje hendluje velike wheel-ove i indeks rezoluciju
RUN pip install --no-cache-dir --upgrade pip setuptools wheel

# Pinovana CPU verzija sa PyPI obicno prolazi stabilnije na Apple Silicon
# nego download.pytorch.org indeks koji cesto uspori ili prekine download.
RUN pip install --no-cache-dir torch==2.2.2

# ostale python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# kopiraj ceo projekat
COPY . .

# port
EXPOSE 8000

# entrypoint
CMD ["bash", "entrypoint.sh"]
