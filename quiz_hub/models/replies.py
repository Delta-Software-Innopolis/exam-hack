from sqlalchemy import String, Text, DateTime, ForeignKey, Float, Table, Column, Integer
from sqlalchemy.orm import Mapped, mapped_column, relationship
from database import Base
from datetime import datetime, timezone

replies = Table(
    "replies",
    Base.metadata,
    Column("initial_id", Integer, ForeignKey("comments.id"), primary_key=True, index=True),
    Column("reply_id", Integer, ForeignKey("comments.id"), index=True, primary_key=True)

)