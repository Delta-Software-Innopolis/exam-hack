import os
from dotenv import load_dotenv

load_dotenv()

HOST = os.getenv("POSTGRES_HOST")
USER = os.getenv("POSTGRES_USER")
DB = os.getenv("POSTGRES_DB")
PASSWORD = os.getenv("POSTGRES_PASSWORD")
PORT = os.getenv("POSTGRES_PORT")

DATABASE_URL = f"postgresql+asyncpg://{USER}:{PASSWORD}@{HOST}:{PORT}/{DB}"

DEBUG = os.getenv("DEBUG", "0") == "1"
