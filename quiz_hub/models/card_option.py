from typing import TYPE_CHECKING

from sqlalchemy import String, Text, DateTime, ForeignKey, Float
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from datetime import datetime, timezone

if TYPE_CHECKING:
    from .card import Card





class Card_option(Base):
    __tablename__ = "card_options"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True, index=True)
    content: Mapped[str] = mapped_column(Text, nullable=False)
    is_right: Mapped[bool] = mapped_column(nullable=False)
    card_id: Mapped[int] = mapped_column(ForeignKey("cards.id"), nullable=False)

    card: Mapped["Card"] = relationship("Card", uselist=False, back_populates="options")