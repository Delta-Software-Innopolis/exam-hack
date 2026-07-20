from pathlib import Path
from typing import Literal

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from generator import CardGenerationError, generate_cards

load_dotenv(dotenv_path=Path(__file__).resolve().parent.parent / ".env")

app = FastAPI(
    title="ExamHacker LLM Service",
    description="Internal service for generating study cards from raw text.",
    version="0.1.0",
)


@app.get("/health")
async def health():
    return {"status": "ok"}


class GenerateRequest(BaseModel):
    text: str = Field(
        ...,
        min_length=10,
        max_length=200000,
        description="Raw text document to generate cards from.",
    )
    card_type: Literal["multiple_choice", "single_answer"] = Field(
        ...,
        description="Type of cards to generate.",
    )
    count: int = Field(
        ...,
        ge=1,
        le=20,
        description="Number of cards to generate (1-20).",
    )


class CardOut(BaseModel):
    type: Literal["multiple_choice", "single_answer"]
    question: str
    options: list[str] | None
    correct_indices: list[int] | None
    correct_answer: str | None
    hint: str


class GenerateResponse(BaseModel):
    cards: list[CardOut]


@app.post("/generate", response_model=GenerateResponse)
async def generate(req: GenerateRequest) -> GenerateResponse:
    """Generate cards from a raw text document."""
    print("GENERATING CARDS...")
    try:
        cards = await generate_cards(req.text, req.card_type, req.count)
        # print(cards)
    except CardGenerationError as exc:
        print(str(exc))
        raise HTTPException(status_code=502, detail=str(exc)) from exc
    return GenerateResponse(cards=cards)
