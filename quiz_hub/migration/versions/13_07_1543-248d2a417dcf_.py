"""empty message

Revision ID: 248d2a417dcf
Revises: cadec56c693e
Create Date: 2026-07-13 15:43:50.331655

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.engine.reflection import Inspector

# revision identifiers, used by Alembic.
revision: str = '248d2a417dcf'
down_revision: Union[str, Sequence[str], None] = 'cadec56c693e'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    bind = op.get_bind()
    inspector = Inspector.from_engine(bind)

    if inspector.has_table("pack_permissions"):
        op.drop_constraint(
            "pack_permissions_user_id_fkey",
            "pack_permissions",
            type_="foreignkey",
        )

        op.drop_constraint(
            "pack_permissions_pack_id_fkey",
            "pack_permissions",
            type_="foreignkey",
        )

        op.create_foreign_key(
            "pack_permissions_user_id_fkey",
            "pack_permissions",
            "users",
            ["user_id"],
            ["id"],
            ondelete="CASCADE",
        )

        op.create_foreign_key(
            "pack_permissions_pack_id_fkey",
            "pack_permissions",
            "packs",
            ["pack_id"],
            ["id"],
            ondelete="CASCADE",
        )


def downgrade() -> None:
    """Downgrade schema."""
    bind = op.get_bind()
    inspector = Inspector.from_engine(bind)

    if inspector.has_table("pack_permissions"):
        op.drop_constraint(
            "pack_permissions_user_id_fkey",
            "pack_permissions",
            type_="foreignkey",
        )

        op.drop_constraint(
            "pack_permissions_pack_id_fkey",
            "pack_permissions",
            type_="foreignkey",
        )

        op.create_foreign_key(
            "pack_permissions_user_id_fkey",
            "pack_permissions",
            "users",
            ["user_id"],
            ["id"],
        )

        op.create_foreign_key(
            "pack_permissions_pack_id_fkey",
            "pack_permissions",
            "packs",
            ["pack_id"],
            ["id"],
        )
