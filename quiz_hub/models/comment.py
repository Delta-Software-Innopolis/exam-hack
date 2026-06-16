from typing import TYPE_CHECKING

from sqlalchemy import String, Text, DateTime, ForeignKey, Float
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from datetime import datetime, timezone
from .replies import replies

if TYPE_CHECKING:
    from .pack import Pack
    from .user import User

class Comment(Base):
    __tablename__ = "comments"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True, index=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), nullable=False)
    creation_date: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updating_date: Mapped[datetime|None] = mapped_column(DateTime(timezone=True), default=None, nullable=True)
    pack_id: Mapped[int] = mapped_column(ForeignKey("packs.id"), nullable=False, index=True) 
    content: Mapped[str] = mapped_column(Text, nullable=False)

    author: Mapped["User"] = relationship("User", uselist=False, back_populates="comments")
    pack: Mapped["Pack"] = relationship("Pack", back_populates="comments", uselist=False)
    replies: Mapped[list["Comment"]] = relationship("Comment", secondary=replies)