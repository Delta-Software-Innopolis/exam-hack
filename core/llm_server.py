from openai import OpenAI
import os
import time
from dotenv import load_dotenv
from pydantic import BaseModel, ValidationError
from typing import List

load_dotenv()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY"),
)

class QuizItem(BaseModel):
    id: int
    question: str
    options: List[str]
    correct: int
    hint: str
    explanation: str

class QuizList(BaseModel):
    items: List[QuizItem]

# Исправлен OUTPUT CONTRACT для соответствия Pydantic-схеме
SYSTEM_PROMPT = """PRIMARY OBJECTIVE
Generate exactly 5 multiple-choice questions about the given topic.

OUTPUT CONTRACT
Return a JSON object with a single key "items" that contains an array of exactly 5 objects.
No markdown, no code fences, no prose outside the JSON.

SCHEMA FOR EACH OBJECT IN "items" ARRAY
"id": 1..5
"question": plain English, 12..160 chars, unambiguous
"options": array of exactly 4 distinct strings; each 6..80 chars; each prefixed with A) , B) , C) , D)  OR  1) , 2) , 3) , 4)
"correct": 1|2|3|4 (1-based index into options)
"hint": 6..120 chars
"explanation": 12..200 chars

STYLE
Plain text only. ASCII. No extra keys or nesting.

VALIDATION
Exactly 5 items in the array
Each item has all 6 keys
options length is 4
correct in [1,2,3,4]
All strings trimmed and non-empty
All options should have appropriate content, actual options."""

USER_PROMPT = "Topic: questions about integration rules"

print("Sending request to LLM...")
start_time = time.perf_counter()

try:
    completion = client.beta.chat.completions.parse(
        model="openai/gpt-oss-20b:free", 
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": USER_PROMPT}
        ],
        response_format=QuizList,
    )
    
    end_time = time.perf_counter()
    generation_time = end_time - start_time
    
    usage = completion.usage
    prompt_tokens = usage.prompt_tokens if usage else 0
    completion_tokens = usage.completion_tokens if usage else 0
    total_tokens = usage.total_tokens if usage else 0
    tps = completion_tokens / generation_time if generation_time > 0 else 0

    quiz_data = completion.choices[0].message.parsed

    # Прагматичный вывод
    print("\n--- GENERATION SUCCESS ---")
    print(f"Model:   {completion.model}")
    print(f"Time:    {generation_time:.2f} s")
    print(f"Tokens:  {total_tokens} (Prompt: {prompt_tokens} | Completion: {completion_tokens})")
    print(f"Speed:   {tps:.1f} t/s")
    print("--------------------------\n")

    for item in quiz_data.items:
        print(f"Q{item.id}: {item.question}")
        for idx, opt in enumerate(item.options, 1):
            marker = "[X]" if idx == item.correct else "[ ]"
            print(f"  {marker} {opt}")
        print(f"  Hint: {item.hint}")
        print(f"  Expl: {item.explanation}\n")

except ValidationError as e:
    print("\n[ERROR] Pydantic Validation Failed:")
    print(e)
except Exception as e:
    print(f"\n[ERROR] API Exception: {e}")