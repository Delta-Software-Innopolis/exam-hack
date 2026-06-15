from typing import TYPE_CHECKING

from sqlalchemy import String, Text, DateTime, ForeignKey, Float
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from datetime import datetime, timezone

if TYPE_CHECKING:
    from .pack import Pack
    from .user import User

class Published_pack(Base):
    __tablename__ = "published_pack"

    id: Mapped[int] = mapped_column(ForeignKey("packs.id"), primary_key=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), nullable=False)
    rating: Mapped[float|None] = mapped_column(Float, nullable=True)
    subject: Mapped[str] = mapped_column(String(30), index=True, nullable=False)
    university: Mapped[str] = mapped_column(String(30), index=True, nullable=False)
    professor: Mapped[str] = mapped_column(String(60), index=True, nullable=False)
    course_book: Mapped[str] = mapped_column(String(80), index=True)

    author: Mapped["User"] = relationship("User", back_populates="packs", uselist=False)
    source: Mapped["Pack"] = relationship("Pack", back_populates="published", uselist=False)