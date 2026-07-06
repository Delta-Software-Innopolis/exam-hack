from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from database import get_async_db
from sqlalchemy import select 
from models.published_pack import Published_pack as PublishedPackModel
from models.pack import Pack as PackModel
router = APIRouter(
    prefix="/suggestions",
    tags=["suggestions"]
)

def check_suggestions(sug_type: str):
    if sug_type not in ["professor", "university", "subject", "course_book", "name"]:
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="wrong query parameter")


@router.get("/", dependencies=[Depends(check_suggestions)])
async def get_suggestions(sug_type: str, q: str, session: AsyncSession = Depends(get_async_db)):
    field = None
    match sug_type:
        case "professor":
            field = PublishedPackModel.professor
        case "university":
            field = PublishedPackModel.university
        case "subject":
            field = PublishedPackModel.subject
        case "course_book":
            field = PublishedPackModel.course_book
        case "name":
            field = PackModel.name
        case _:
            raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="wrong suggestion type")
    stmt = select(field).where(field.ilike(f"%{q}%")).limit(10)
    result = (await session.scalars(stmt)).all()
    return [*result]