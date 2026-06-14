import os
from dotenv import load_dotenv

load_dotenv()
DATABASE_URL=os.getenv("DATABASE_URL")
DATABASE_URL_ALEMBIC=os.getenv("DATABASE_URL_ALEMBIC")
DEBUG=bool(os.getenv("DEBUG", ""))
