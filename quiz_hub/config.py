import os
from dotenv import load_dotenv
from cryptography.hazmat.primitives import serialization

load_dotenv()

HOST = os.getenv("POSTGRES_HOST")
USER = os.getenv("POSTGRES_USER")
DB = os.getenv("POSTGRES_DB")
PASSWORD = os.getenv("POSTGRES_PASSWORD")
PORT = os.getenv("POSTGRES_PORT")

DATABASE_URL = f"postgresql+asyncpg://{USER}:{PASSWORD}@{HOST}:{PORT}/{DB}"

DEBUG = os.getenv("DEBUG", "0") == "1"

ALGORITH = os.getenv("ALGORITHM")

with open('secrets/jwt_public.pem', 'rb') as key_file:
    key = serialization.load_pem_public_key(
        key_file.read()
    )

SECRET_KEY_ACCESS = key
