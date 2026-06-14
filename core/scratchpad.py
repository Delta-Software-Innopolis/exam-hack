from openai import OpenAI
import os
from dotenv import load_dotenv



load_dotenv()
client = OpenAI(
  base_url="https://openrouter.ai/api/v1",
  api_key=os.getenv("OPENROUTER_API_KEY"),
)

completion = client.chat.completions.create(
  model="openrouter/free",
  messages=[
    {
      "role": "user",
      "content": ''
    }
  ],
  response_format={
    "type": "json_schema",
    "json_schema": {
        id: int
        question: string
        options: string[]
        correct: int
        hint: string
        explanation: string
    }
)

print(f"Model: {completion.model}")
print(completion.choices[0].message.content)
