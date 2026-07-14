from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession
from config import DATABASE_URL, DEBUG
from sqlalchemy.orm import DeclarativeBase
from collections.abc import AsyncGenerator

async_engine = create_async_engine(DATABASE_URL, echo=DEBUG) 

async_session_maker = async_sessionmaker(async_engine, expire_on_commit=False, class_=AsyncSession)

class Base(DeclarativeBase):
    pass


async def get_async_db() -> AsyncGenerator[AsyncSession, None]:
    """
    Get access to the async sqlalchemy session
    """

    async with async_session_maker() as session:

        yield session