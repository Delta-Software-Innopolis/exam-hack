from typing import TYPE_CHECKING

from sqlalchemy import String, Text, DateTime, ForeignKey, Computed, Index
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import TSVECTOR
from database import Base
from datetime import datetime, timezone
from .forks import forks

if TYPE_CHECKING:
    from .card import Card
    from .comment import Comment
    from .published_pack import Published_pack

class Pack(Base):
    __tablename__ = "packs"

    id: Mapped[int] = mapped_column(primary_key=True, index=True, autoincrement=True)
    name: Mapped[str] = mapped_column(String(50), nullable=False, index=True)
    creation_date: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updating_date: Mapped[datetime|None] = mapped_column(DateTime(timezone=True), default=None)
    author_id: Mapped[int] = mapped_column(ForeignKey("users.id"), nullable=False)

    tsv_name: Mapped[TSVECTOR] = mapped_column(
        TSVECTOR,
        Computed(
            """
            setweight(to_tsvector('english', coalesce(name, '')), 'A')
            """,
            persisted=True
        ),
        nullable=False
    )
    published: Mapped["Published_pack | None"] = relationship("Published_pack", back_populates="source", uselist=False, lazy="noload")
    cards: Mapped[list["Card"]] = relationship("Card", back_populates="pack")
    comments: Mapped[list["Comment"]] = relationship("Comment", back_populates="pack")

    __table_args__ = (
        Index("ix_name_tsv_gin", "tsv_name", postgresql_using="gin"),
    )