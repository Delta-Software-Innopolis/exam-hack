from fastapi import APIRouter, Depends, Query, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi.encoders import jsonable_encoder
from sqlalchemy.orm import joinedload, load_only, selectinload
from database import get_async_db
from models.published_pack import Published_pack as PublishedPackModel
from models.user import User as UserModel
from models.pack import Pack as PackModel
from sqlalchemy import select
from pydantic_models.published_quiz import PublishedQuizesResponse
from typing import cast

router = APIRouter(
    prefix="/posts",
    tags=["posts"]
)

@router.get("/", response_model=PublishedQuizesResponse)
async def get_packs(
    offset: int = Query(1, ge=1), 
    limit: int = Query(16, ge=16), 
    session: AsyncSession = Depends(get_async_db)
    )->dict:
    stmt = (
        select(PublishedPackModel)  
        .offset((offset - 1) * limit)
        .limit(limit)
        .order_by(PublishedPackModel.rating)
        .options(
            load_only(
                PublishedPackModel.id,
                PublishedPackModel.rating,
                PublishedPackModel.subject,
                PublishedPackModel.university,
                PublishedPackModel.professor,
                PublishedPackModel.course_book,
            ),
            joinedload(PublishedPackModel.author)
            .options(load_only(UserModel.id, UserModel.name)),
            joinedload(PublishedPackModel.source)
            .options(load_only(PackModel.name)),
            selectinload(PublishedPackModel.forks)
            .options(
                joinedload(PublishedPackModel.source),
            )
        )
    )
    result = (await session.scalars(stmt)).unique().all()
    if not result:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND)
    for object in result:
        print(object.id)
    return {"packs": result}
    

