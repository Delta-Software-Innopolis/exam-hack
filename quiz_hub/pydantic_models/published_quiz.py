from pydantic import BaseModel, Field, computed_field
from typing import Annotated
from pydantic.types import PositiveFloat, PositiveInt
from pydantic import ConfigDict
class Author(BaseModel):
    id: PositiveInt
    name: str
class Fork(BaseModel):
    id: PositiveInt
    rating: PositiveFloat|None
    name: str

class PublishedQuiz(BaseModel):
    id: PositiveInt
    author: Author
    rating: PositiveFloat|None
    name: str
    subject: str
    university: str
    professor: str
    course_book: str
    forks: list[Fork]
    model_config = ConfigDict(from_attributes=True)

class PublishedQuizesResponse(BaseModel):
    packs: list[PublishedQuiz]

class PublishedPackNew(BaseModel):
    pack_id: PositiveInt
    subject: str
    university: str
    professor: str
    course_book: str

