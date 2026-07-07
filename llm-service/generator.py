import json
import os
from typing import Dict, List, Literal

from openai import AsyncOpenAI
from pydantic import BaseModel, Field


class _ProviderConfig:
    def __init__(self, base_url: str, api_key_env: str, default_model: str):
        self.base_url = base_url
        self.api_key_env = api_key_env
        self.default_model = default_model


_PROVIDERS: Dict[str, _ProviderConfig] = {
    "openrouter": _ProviderConfig(
        base_url="https://openrouter.ai/api/v1",
        api_key_env="OPENROUTER_API_KEY",
        default_model="openai/gpt-oss-20b:free",
    ),
    "deepseek": _ProviderConfig(
        base_url="https://api.deepseek.com",
        api_key_env="DEEPSEEK_API_KEY",
        default_model="deepseek-v4-pro",
    ),
}

_clients: Dict[str, AsyncOpenAI] = {}


class CardGenerationError(Exception):
    """Raised when the LLM fails to produce valid cards."""

    pass


def _detect_provider() -> str:
    """Pick the LLM provider from env, falling back to whichever key is set."""
    provider = os.getenv("LLM_PROVIDER", "").lower().strip()
    if provider in _PROVIDERS:
        return provider
    if os.getenv("DEEPSEEK_API_KEY"):
        return "deepseek"
    if os.getenv("OPENROUTER_API_KEY"):
        return "openrouter"
    raise CardGenerationError(
        "No LLM provider configured. Set LLM_PROVIDER to 'openrouter' or 'deepseek' "
        "and provide the matching API key."
    )


def get_provider_config() -> dict:
    """Return provider config dict with base_url and api_key for synchronous use."""
    provider = _detect_provider()
    config = _PROVIDERS[provider]
    api_key = os.getenv(config.api_key_env)
    if not api_key:
        raise CardGenerationError(f"{config.api_key_env} is not set")
    return {
        "base_url": config.base_url,
        "api_key": api_key,
    }


def _get_client(provider: str) -> AsyncOpenAI:
    """Return (and cache) an AsyncOpenAI client configured for the provider."""
    if provider not in _clients:
        config = _PROVIDERS[provider]
        api_key = os.getenv(config.api_key_env)
        if not api_key:
            raise CardGenerationError(f"{config.api_key_env} is not set")
        _clients[provider] = AsyncOpenAI(base_url=config.base_url, api_key=api_key)
    return _clients[provider]


def card_type_description(card_type: str) -> str:
    """Get a human-readable description of a card type."""
    descriptions = {
        "multiple_choice": "Multiple choice questions with exactly 4 answer options. "
                           "Each question may have one or multiple correct answers.",
        "single_answer": "Questions with a single correct text answer.",
    }
    return descriptions.get(card_type, card_type)


def card_type_examples(card_type: str) -> str:
    """Get JSON examples for a card type to include in prompts."""
    examples = {
        "multiple_choice": (
            'Example card:\n'
            '{\n'
            '  "question": "What is the capital of France?",\n'
            '  "options": ["London", "Paris", "Berlin", "Madrid"],\n'
            '  "correct_indices": [1],\n'
            '  "hint": "Think of the city of light"\n'
            '}'
        ),
        "single_answer": (
            'Example card:\n'
            '{\n'
            '  "question": "What is the capital of France?",\n'
            '  "correct_answer": "Paris",\n'
            '  "hint": "Think of the city of light"\n'
            '}'
        ),
    }
    return examples.get(card_type, "")


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


async def generate_cards(
    text: str,
    card_type: Literal["multiple_choice", "single_answer"],
    count: int,
) -> List[dict]:
    """Generate study cards from raw text using the configured LLM."""
    provider = _detect_provider()
    client = _get_client(provider)
    config = _PROVIDERS[provider]
    model = os.getenv("LLM_MODEL", config.default_model)

    if card_type == "multiple_choice":
        response_format = MultipleChoiceList
        system_prompt = SYSTEM_PROMPT_MULTIPLE_CHOICE.format(count=count)
    else:
        response_format = SingleAnswerList
        system_prompt = SYSTEM_PROMPT_SINGLE_ANSWER.format(count=count)

    try:
        response = await client.chat.completions.create(
            model=model,
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": f"Context: {text}"},
            ],
            response_format={"type": "json_object"},
            temperature=0.1,
        )
        content = response.choices[0].message.content
        if content is None:
            raise CardGenerationError("LLM returned empty content")
        parsed = response_format.model_validate_json(content)
    except CardGenerationError:
        raise
    except Exception as exc:
        raise CardGenerationError(f"LLM request failed: {exc}") from exc

    if not parsed.items:
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