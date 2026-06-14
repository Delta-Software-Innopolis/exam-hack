from openai import OpenAI
import os
from dotenv import load_dotenv



load_dotenv()
client = OpenAI(
  base_url="https://openrouter.ai/api/v1",
  api_key=os.getenv("OPENROUTER_API_KEY"),
)

schema = {
    "type": "json_schema",
    "json_schema": {
        "name": "quiz_item", 
        "strict": True,      
        "schema": {
            "type": "object",
            "properties": {
                "id": {"type": "integer"},
                "question": {"type": "string"},
                "options": {
                    "type": "array",
                    "items": {"type": "string"}
                },
                "correct": {"type": "integer"},
                "hint": {"type": "string"},
                "explanation": {"type": "string"}
            },
            "required": ["id", "question", "options", "correct", "hint", "explanation"],
            "additionalProperties": False
        }
    }
}

completion = client.chat.completions.create(
  model="openrouter/free",
  messages=[
    {
      "role": "user",
      "content": "Сгенерируй один сложный вопрос по Python для викторины."
    }
  ],
  response_format=schema
)

print(f"Model: {completion.model}")
print(completion.choices[0].message.content)
