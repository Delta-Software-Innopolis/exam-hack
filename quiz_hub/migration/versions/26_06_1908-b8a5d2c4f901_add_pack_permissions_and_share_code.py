"""add pack permissions and share code

Revision ID: b8a5d2c4f901
Revises: fe2ebf3f76c2
Create Date: 2026-06-26 19:08:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.engine.reflection import Inspector

# revision identifiers, used by Alembic.
revision: str = 'b8a5d2c4f901'
down_revision: Union[str, Sequence[str], None] = 'fe2ebf3f76c2'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    bind = op.get_bind()
    inspector = Inspector.from_engine(bind)

    op.alter_column('packs', 'share_code', nullable=True)
    op.drop_index("ix_packs_share_code", 'packs')
    op.create_index('ix_packs_share_code', 'packs', ['share_code'], unique=False)
    op.execute("UPDATE packs SET share_code = 'mock_code_lol'|| id")
    op.drop_index("ix_packs_share_code", 'packs')
    op.create_index('ix_packs_share_code', 'packs', ['share_code'], unique=True)
    op.alter_column('packs', 'share_code', nullable=False)

    if not inspector.has_table("pack_permissions"):
        op.create_table(
            'pack_permissions',
            sa.Column('user_id', sa.Integer(), nullable=False),
            sa.Column('pack_id', sa.Integer(), nullable=False),
            sa.Column('permission', sa.Integer(), nullable=False),
            sa.ForeignKeyConstraint(['pack_id'], ['packs.id']),
            sa.ForeignKeyConstraint(['user_id'], ['users.id']),
            sa.PrimaryKeyConstraint('user_id', 'pack_id'),
)
    index_names = ["ix_pack_permissions_pack_id", "ix_pack_permissions_user_id", "ix_pack_permissions_permission"]
    for index_name in index_names:
        indexes = {ix["name"] for ix in inspector.get_indexes("pack_permissions")}
        if index_name not in indexes:
            op.create_index(op.f('ix_pack_permissions_user_id'), 'pack_permissions', ['user_id'], unique=False)
            op.create_index(op.f('ix_pack_permissions_pack_id'), 'pack_permissions', ['pack_id'], unique=False)
            op.create_index(op.f('ix_pack_permissions_permission'), 'pack_permissions', ['permission'], unique=False)


def downgrade() -> None:
    """Downgrade schema."""
    op.drop_index(op.f('ix_pack_permissions_permission'), table_name='pack_permissions')
    op.drop_index(op.f('ix_pack_permissions_pack_id'), table_name='pack_permissions')
    op.drop_index(op.f('ix_pack_permissions_user_id'), table_name='pack_permissions')
    op.drop_table('pack_permissions')

    op.drop_constraint('fk_packs_author_id_users', 'packs', type_='foreignkey')
    op.drop_index('ix_packs_share_code', table_name='packs')
    op.drop_index(op.f('ix_packs_author_id'), table_name='packs')
    op.drop_column('packs', 'share_code')
    op.drop_column('packs', 'author_id')
