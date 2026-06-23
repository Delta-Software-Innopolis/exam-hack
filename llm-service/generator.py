import os
from typing import List, Literal

from openai import AsyncOpenAI
from pydantic import BaseModel, Field


_client: AsyncOpenAI | None = None


def _get_client() -> AsyncOpenAI:
    global _client
    if _client is None:
        api_key = os.getenv("OPENROUTER_API_KEY")
        if not api_key:
            raise CardGenerationError("OPENROUTER_API_KEY is not set")
        _client = AsyncOpenAI(
            base_url="https://openrouter.ai/api/v1",
            api_key=api_key,
        )
    return _client


class MultipleChoiceCard(BaseModel):
    question: str = Field(description="The question text itself, clear and unambiguous.")
    options: List[str] = Field(
        description="Array of exactly 4 distinct answer options. Raw text only, no prefixes like A) or 1)."
    )
    correct_indices: List[int] = Field(
        description="Array of 0-based indices (0 to 3) representing the correct options. Can be one or multiple."
    )
    hint: str = Field(description="A short helpful hint.")


class MultipleChoiceList(BaseModel):
    items: List[MultipleChoiceCard]


class SingleAnswerCard(BaseModel):
    question: str = Field(description="The question text itself, clear and unambiguous.")
    correct_answer: str = Field(description="The exact expected text answer.")
    hint: str = Field(description="A short helpful hint.")


class SingleAnswerList(BaseModel):
    items: List[SingleAnswerCard]


SYSTEM_PROMPT_MULTIPLE_CHOICE = """PRIMARY OBJECTIVE
Generate exactly {count} multiple-choice questions about the provided text.
Some questions MUST have more than one correct answer.

OUTPUT CONTRACT
Return a JSON object with an "items" array.
Each item must have:
- "question": string
- "options": exactly 4 strings, no prefixes like A), B), 1), etc.
- "correct_indices": 0-based indices of correct options, e.g. [0, 2]
- "hint": short hint
"""

SYSTEM_PROMPT_SINGLE_ANSWER = """PRIMARY OBJECTIVE
Generate exactly {count} questions with a single correct text answer based on the provided text.

OUTPUT CONTRACT
Return a JSON object with an "items" array.
Each item must have:
- "question": string
- "correct_answer": exact expected text answer
- "hint": short hint
"""


class CardGenerationError(Exception):
    """Raised when the LLM fails to produce valid cards."""
    pass


async def generate_cards(
    text: str,
    card_type: Literal["multiple_choice", "single_answer"],
    count: int,
) -> List[dict]:
    """Generate study cards from raw text using the configured LLM."""
    if card_type == "multiple_choice":
        response_format = MultipleChoiceList
        system_prompt = SYSTEM_PROMPT_MULTIPLE_CHOICE.format(count=count)
    else:
        response_format = SingleAnswerList
        system_prompt = SYSTEM_PROMPT_SINGLE_ANSWER.format(count=count)

    try:
        response = await _get_client().chat.completions.parse(
            model=os.getenv("LLM_MODEL", "openai/gpt-oss-20b:free"),
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": f"Context: {text}"},
            ],
            response_format=response_format,
            temperature=0.1,
        )
        parsed = response.choices[0].message.parsed
    except Exception as exc:
        raise CardGenerationError(f"LLM request failed: {exc}") from exc

    if parsed is None or not parsed.items:
        raise CardGenerationError("LLM returned no cards")

    cards: List[dict] = []
    for item in parsed.items:
        if card_type == "multiple_choice":
            cards.append(
                {
                    "type": "multiple_choice",
                    "question": item.question,
                    "options": item.options,
                    "correct_indices": item.correct_indices,
                    "correct_answer": None,
                    "hint": item.hint,
                }
            )
        else:
            cards.append(
                {
                    "type": "single_answer",
                    "question": item.question,
                    "options": None,
                    "correct_indices": None,
                    "correct_answer": item.correct_answer,
                    "hint": item.hint,
                }
            )

    return cards
