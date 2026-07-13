from sqlalchemy import String, Text, DateTime, ForeignKey, Float, Table, Integer, Column
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from datetime import datetime, timezone

forks = Table(
    "forks",
    Base.metadata,
    Column("fork_id", Integer, ForeignKey("packs.id", ondelete="CASCADE"), primary_key=True, index=True),
    Column("original_id", Integer, ForeignKey("packs.id"), onupdate="CASCADE", primary_key=True, index=True)
)