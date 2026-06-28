from fastapi import APIRouter, Depends, Query, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi.encoders import jsonable_encoder
from sqlalchemy.orm import joinedload, load_only, selectinload
from database import get_async_db
from models.published_pack import Published_pack as PublishedPackModel
from models.user import User as UserModel
from models.pack import Pack as PackModel
from sqlalchemy import select, text
from pydantic_models.published_quiz import PublishedQuizesResponse, PublishedPackNew
from typing import cast, Any
from dependencies import validate_token
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
    

@router.post("/")
async def publish_pack(new_pack_data: PublishedPackNew, session: AsyncSession = Depends(get_async_db), user_data = Depends(validate_token)):
    id = new_pack_data.pack_id
    user_id = user_data["user_id"]
    stmt = select(PackModel).where(PackModel.id == id)
    local_quiz = (await session.scalars(stmt)).first()
    if not local_quiz:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="There is no quiz with such id")
    new_pack = (PublishedPackModel(
        id=id,
        user_id=user_id, 
        subject = new_pack_data.subject,
        university = new_pack_data.university,
        professor = new_pack_data.professor,
        course_book = new_pack_data.course_book)
        )

    session.add(new_pack)
    await session.flush()
    if new_pack.original:
        new_pack.original.forks.append(new_pack)
    await session.commit()
    return {"added": "complite"}

@router.post("/{pack_id}")
async def add_pack(
    pack_id: int,
    user_data: dict[str, Any] = Depends(validate_token),
    session: AsyncSession = Depends(get_async_db)
    )-> dict[str, str]:
    stmt = select(PackModel).where(PackModel.id == pack_id)
    result = (await session.scalars(stmt)).first()
    if not result:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="There is no pack with such id")
    query = text("""
        INSERT INTO pack_permissions (user_id, pack_id, permission) VALUES
                 (:user_id, :pack_id, 2);
                 """)
    await session.execute(query,
        {
            "user_id": user_data["user_id"],
            "pack_id": pack_id
        })
    await session.commit()
    return {"adding": "success"}
