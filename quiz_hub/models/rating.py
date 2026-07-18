from sqlalchemy import ForeignKey, Table, Integer, Column
from database import Base

rating = Table(
    "rating",
    Base.metadata,
    Column("user_id", Integer, ForeignKey("users.id", ondelete="CASCADE"), primary_key=True, index=True),
    Column("pack_id", Integer, ForeignKey("published_pack.id", ondelete="CASCADE"), primary_key=True, index=True),
    Column("score", Integer, nullable=False)
)

