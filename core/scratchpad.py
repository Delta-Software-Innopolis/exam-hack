from openai import OpenAI
import os
from dotenv import load_dotenv
from pydantic import BaseModel


load_dotenv()
client = OpenAI(
  base_url="https://openrouter.ai/api/v1",
  api_key=os.getenv("OPENROUTER_API_KEY"),
)

class QuizItem(BaseModel):
    id: int
    question: str
    options: list[str]
    correct: int
    hint: str
    explanation: str

completion = client.chat.completions.parse(
  model="openrouter/free",
  messages=[
    {
      "role": "user",
      "content": "Сгенерируй один сложный вопрос по Python для викторины."
    }
  ],
  response_format=QuizItem,
)

print(f"Model: {completion.model}")
print(completion.choices[0].message.content)
