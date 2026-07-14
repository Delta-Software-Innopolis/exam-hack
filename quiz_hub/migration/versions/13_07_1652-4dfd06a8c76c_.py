"""empty message

Revision ID: 4dfd06a8c76c
Revises: 248d2a417dcf
Create Date: 2026-07-13 16:52:53.632307

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.engine.reflection import Inspector

# revision identifiers, used by Alembic.
revision: str = '4dfd06a8c76c'
down_revision: Union[str, Sequence[str], None] = '248d2a417dcf'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None

def upgrade() -> None:
    """Upgrade schema."""
    bind = op.get_bind()
    inspector = Inspector.from_engine(bind)

    if inspector.has_table("cards"):
        op.drop_constraint(
            "cards_pack_id_fkey",
            "cards",
            type_="foreignkey",
        )
        op.create_foreign_key(
            "cards_pack_id_fkey",
            "cards",
            "packs",
            ["pack_id"],
            ["id"],
            ondelete="CASCADE"
        )

    if inspector.has_table("card_options"):
        op.drop_constraint(
            "card_options_card_id_fkey",
            "card_options",
            type_="foreignkey",
        )
        op.create_foreign_key(
            "card_options_card_id_fkey",
            "card_options",
            "cards",
            ["card_id"],
            ["id"],
            ondelete="CASCADE"
        )

def downgrade() -> None:
    """Downgrade schema."""
    bind = op.get_bind()
    inspector = Inspector.from_engine(bind)

    if inspector.has_table("card_options"):
        op.drop_constraint(
            "card_options_card_id_fkey",
            "card_options",
            type_="foreignkey",
        )
        op.create_foreign_key(
            "card_options_card_id_fkey",
            "card_options",
            "cards",
            ["card_id"],
            ["id"],
        )

    if inspector.has_table("cards"):
        op.drop_constraint(
            "cards_pack_id_fkey",
            "cards",
            type_="foreignkey",
        )
        op.create_foreign_key(
            "cards_pack_id_fkey",
            "cards",
            "packs",
            ["pack_id"],
            ["id"],
        )
