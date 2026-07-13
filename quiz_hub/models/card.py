from typing import TYPE_CHECKING

from sqlalchemy import String, Text, DateTime, ForeignKey, Float
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from datetime import datetime, timezone

if TYPE_CHECKING:
    from .card_option import Card_option
    from .pack import Pack


class Card(Base):
    __tablename__ = "cards"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True, index=True)
    question: Mapped[str] = mapped_column(Text, nullable=False)
    pack_id: Mapped[int] = mapped_column(ForeignKey("packs.id"), index=True)
    hint: Mapped[str] = mapped_column(Text, nullable=False)
    @property
    def correct(self):
        return [option.id for option in self.options_models if option.is_right]
    @property
    def options(self):
        return [option.content for option in self.options_models]
    pack: Mapped["Pack"] = relationship("Pack", uselist=False, back_populates="cards")
    options_models: Mapped[list["Card_option"]] = relationship("Card_option", back_populates="card")