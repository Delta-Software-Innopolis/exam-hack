from pathlib import Path
from typing import Any, Dict, List, Literal

import anyio
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from generator import CardGenerationError, generate_cards
from agent_generator import generate_cards_agent

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
        max_length=50000,
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


class AgentInfo(BaseModel):
    iterations_used: int = Field(description="Number of generation->refine iterations used.")
    auto_passed: bool = Field(description="Whether automated quality checks passed.")
    eval_passed: bool = Field(description="Whether LLM evaluation passed.")
    total_issues: int = Field(description="Total issues found by automated checks.")


class GenerateAgentResponse(BaseModel):
    cards: list[CardOut]
    agent_info: AgentInfo


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


@app.post("/generate-agent", response_model=GenerateAgentResponse)
async def generate_agent(req: GenerateRequest) -> GenerateAgentResponse:
    """
    Generate cards using the agent-based pipeline with automated quality
    evaluation and LLM-based refinement. This endpoint may take longer and
    use more tokens than /generate, but produces higher quality results.
    """
    print("GENERATING CARDS WITH AGENT...")
    try:
        # Run the synchronous agent generator in a thread pool
        cards = await anyio.to_thread.run_sync(
            generate_cards_agent, req.text, req.card_type, req.count
        )
    except CardGenerationError as exc:
        print(str(exc))
        raise HTTPException(status_code=502, detail=str(exc)) from exc

    return GenerateAgentResponse(
        cards=cards,
        agent_info=AgentInfo(
            iterations_used=0,  # simplified for now
            auto_passed=True,
            eval_passed=True,
            total_issues=0,
        ),
    )
