from fastapi import APIRouter, Depends, Query, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import joinedload, load_only, selectinload, DeclarativeBase
from database import get_async_db
from models.published_pack import Published_pack as PublishedPackModel
from models.user import User as UserModel
from models.pack import Pack as PackModel
from sqlalchemy import select, text, func, or_, desc
from pydantic_models.published_quiz import PublishedQuizesResponse, PublishedPackNew
from typing import Any
from dependencies import validate_token
router = APIRouter(
    prefix="/posts",
    tags=["posts"]
)

class FTSMember:
    def __init__(
            self, 
            model_arg_tsv: str,
            search_value: str,
            sqlModel: type[DeclarativeBase],
            model_arg: str
        ):
        self.model_arg_tsv = model_arg_tsv
        self.search_value = search_value
        self.sqlModel = sqlModel
        self.ts_query = func.websearch_to_tsquery('english', " OR ".join(self.search_value.split()))
        self.model_arg = model_arg
    def add_filter(self):
        return [or_(
                getattr(self.sqlModel, self.model_arg_tsv).op('@@')(self.ts_query),
                getattr(self.sqlModel, self.model_arg).ilike(f"%{self.search_value}%"))]
    def add_runk(self):
        return func.ts_rank(getattr(self.sqlModel, self.model_arg_tsv), self.ts_query)

class FilterParams:
    def __call__(
            self,
            subject: str|None = Query(None),
            professor: str|None = Query(None),
            course_book: str|None = Query(None),
            university: str|None = Query(None),
            search_main: str|None = Query(None)
    ):
        fts: dict[str, FTSMember]= {}
        rank_columns = []
        filters = []

        if search_main is not None:
            self._filter_main(search_main, filters, rank_columns)

        if course_book is not None:
            fts["course_book"] = FTSMember(
                                    model_arg_tsv="tsv_course_book", 
                                    search_value=course_book,
                                    sqlModel=PublishedPackModel, 
                                    model_arg="course_book")

        for fts_filter in fts.values():
            filters.extend(fts_filter.add_filter())
            rank_columns.append(fts_filter.add_runk())
        
        filters.extend(
            self._add_ilike_filter(
                subject = subject,
                professor = professor,
                university = university
            )
        )
        return {"filters": filters, "ranks": rank_columns}

    def _add_ilike_filter(self, **kw):
        return [getattr(PublishedPackModel, key).ilike(f"%{value}%") for key, value in kw.items() if hasattr(PublishedPackModel, key) and value is not None]
    
    def _filter_main(self, search: str, filters: list, rank_columns: list):
        if search:
            search_value = search.strip()
            if search_value:
                ts_query = func.websearch_to_tsquery('english', " OR ".join(search_value.split()))
                filters.append(or_(
                    PublishedPackModel.tsv_desc.op('@@')(ts_query),
                    PackModel.tsv_name.op('@@')(ts_query),
                    PublishedPackModel.description.ilike(f"%{search_value}%"),
                    PackModel.name.ilike(f"%{search_value}%"),
                ))
                rank_columns.append(
                    (func.ts_rank(PublishedPackModel.tsv_desc, ts_query) + func.ts_rank(PackModel.tsv_name, ts_query)).label("rank")
                )

@router.get("/", response_model=PublishedQuizesResponse)
async def get_packs(
    offset: int = Query(1, ge=1), 
    limit: int = Query(16, ge=16),
    params: dict[str, Any] = Depends(FilterParams()),
    session: AsyncSession = Depends(get_async_db)
    )->dict:
    filters = params["filters"]
    rank_col = params["ranks"]
    
    if rank_col:
        stmt = select(
            PublishedPackModel, 
            *map(lambda x: x.label(f"rank{x}"), rank_col)
        ).order_by(*[desc(column) for column in rank_col], desc(PublishedPackModel.rating).nulls_last())
    else:
        stmt = select(PublishedPackModel).order_by(desc(PublishedPackModel.rating).nulls_last())
    print("FILTERS" + ("-")* 20, filters)       
    stmt = (
        stmt.join(PublishedPackModel.source)
        .where(*filters)
        .offset((offset - 1) * limit)
        .limit(limit)
        .options(
            load_only(
                PublishedPackModel.id,
                PublishedPackModel.rating,
                PublishedPackModel.subject,
                PublishedPackModel.university,
                PublishedPackModel.professor,
                PublishedPackModel.course_book,
                PublishedPackModel.description
            ),
            joinedload(PublishedPackModel.author)
            .options(load_only(UserModel.id, UserModel.name)),
            selectinload(PublishedPackModel.forks)
            .options(
                joinedload(PublishedPackModel.source),
            ),
            joinedload(PublishedPackModel.source)
            .options(load_only(PackModel.name))
        )
    )
        

    result = (await session.execute(stmt)).all()
    packs = [row[0] for row in result]
    if not result:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND)
    return {"packs": packs}
    

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
