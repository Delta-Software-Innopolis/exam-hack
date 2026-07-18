from typing import TYPE_CHECKING

from sqlalchemy import String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from .rating import rating as rating_table

if TYPE_CHECKING:
    from .comment import Comment
    from .published_pack import Published_pack


class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True, index=True)
    name: Mapped[str] = mapped_column(String(30), nullable=False, index=True)
    pass_hash: Mapped[str] = mapped_column(Text, nullable=False, index=True)
    
    packs: Mapped[list["Published_pack"]] = relationship(
        "Published_pack",
        back_populates="author",
    )
    rated_packs: Mapped[list["Published_pack"]] = relationship(
        "Published_pack",
        secondary=rating_table,
        back_populates="valuers",
        lazy="noload"
    )
    comments: Mapped[list["Comment"]] = relationship("Comment", back_populates="author", lazy="noload")