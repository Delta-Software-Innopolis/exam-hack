from typing import TYPE_CHECKING

from sqlalchemy import String, Text, DateTime, ForeignKey, Float, Computed, Index
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import TSVECTOR
from database import Base
from datetime import datetime, timezone
from .forks import forks
from .rating import rating as rating_table

if TYPE_CHECKING:
    from .pack import Pack
    from .user import User

class Published_pack(Base):
    __tablename__ = "published_pack"

    id: Mapped[int] = mapped_column(ForeignKey("packs.id"), primary_key=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), nullable=False)
    rating: Mapped[float|None] = mapped_column(Float, nullable=True)
    subject: Mapped[str] = mapped_column(String(30), index=True, nullable=False)
    university: Mapped[str] = mapped_column(String(100), index=True, nullable=False)
    professor: Mapped[str] = mapped_column(String(60), index=True, nullable=False)
    course_book: Mapped[str] = mapped_column(String(120), index=True)
    description: Mapped[str|None] = mapped_column(Text, default=None, nullable=True)
    @property
    def name(self):
        return self.source.name

    @property
    def cards(self):
        return self.source.cards 
    tsv_desc: Mapped[TSVECTOR] = mapped_column(
        TSVECTOR,
        Computed(
            """
            setweight(to_tsvector('english', coalesce(description, '')), 'D')
            """,
            persisted=True
        ),
        nullable=False
    )
    tsv_course_book: Mapped[TSVECTOR] = mapped_column(
        TSVECTOR,
        Computed(
            """
            setweight(to_tsvector('english', coalesce(course_book, '')), 'B')
            """,
            persisted=True
        ),
        nullable=False
    )

    author: Mapped["User"] = relationship("User", back_populates="packs", uselist=False)
    source: Mapped["Pack"] = relationship("Pack", back_populates="published", uselist=False, lazy="noload")
    original: Mapped["Published_pack"] = relationship(
        "Published_pack", 
        uselist=False,
        secondary=forks,
        primaryjoin="Published_pack.id == forks.c.fork_id",
        secondaryjoin="Published_pack.id == forks.c.original_id",
        lazy="noload")
    forks: Mapped[list["Published_pack"]] = relationship(
        "Published_pack",
        secondary=forks, 
        primaryjoin="Published_pack.id == forks.c.original_id",
        secondaryjoin="Published_pack.id == forks.c.fork_id",
        lazy="noload",
        order_by="Published_pack.rating.desc().nulls_last()"
    )
    
    valuers: Mapped[list["User"]] = relationship(
        "User", 
        secondary=rating_table,
        lazy="noload",
        back_populates="rated_packs"
        )
        
    __table_args__ = (
        Index("ix_desc_tsv_gin", "tsv_desc", postgresql_using="gin"),
        Index("ix_course_book_tsv_gin", "tsv_course_book", postgresql_using="gin"),
    )