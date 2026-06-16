import asyncio
from openai import AsyncOpenAI
from pydantic import BaseModel, Field
from typing import List
import time
import os

client = AsyncOpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY"),
)

# Обновленная схема: теперь correct - это список индексов
class QuizItem(BaseModel):
    id: int
    question: str
    options: List[str] = Field(..., min_length=2, max_length=6)
    correct: List[int] = Field(..., description="List of 1-based indices of correct options")
    hint: str
    explanation: str

class QuizList(BaseModel):
    items: List[QuizItem]

SYSTEM_PROMPT = """PRIMARY OBJECTIVE
Generate exactly {count} multiple-choice questions about the given text.
Some questions MUST have multiple correct answers.

OUTPUT CONTRACT
Return a JSON object with "items" array.
"correct" MUST be an array of integers (1-based index). Example: [1, 3] if options A and C are correct.
"""

async def generate_batch(text_chunk: str, count: int = 3) -> QuizList:
    """Асинхронная функция генерации одного батча вопросов"""
    prompt = SYSTEM_PROMPT.replace("{count}", str(count))
    try:
        response = await client.beta.chat.completions.parse(
            model="openai/gpt-oss-20b:free", # Для прода лучше взять gpt-4o-mini или claude-3-haiku
            messages=[
                {"role": "system", "content": prompt},
                {"role": "user", "content": f"Context: {text_chunk}"}
            ],
            response_format=QuizList,
        )
        return response.choices[0].message.parsed
    except Exception as e:
        print(f"[ERROR] Batch failed: {e}")
        return QuizList(items=[])

async def main():
    # Допустим, мы разбили документ на 3 логических куска
    chunks = [
        "Integration by parts formula is integral of u dv = uv - integral of v du.",
        "Substitution rule is the reverse chain rule.",
        "Partial fractions are used for rational functions."
    ]
    
    start_time = time.perf_counter()
    
    # Запускаем генерацию ПАРАЛЛЕЛЬНО
    # Лайфхак: asyncio.gather отправляет все реквесты разом
    tasks = [generate_batch(chunk, count=2) for chunk in chunks]
    results = await asyncio.gather(*tasks)
    
    all_questions = []
    for res in results:
        all_questions.extend(res.items)
        
    print(f"Generated {len(all_questions)} questions in {time.perf_counter() - start_time:.2f}s")
    # Тут дальше сохраняешь в БД

if __name__ == "__main__":
    asyncio.run(main())