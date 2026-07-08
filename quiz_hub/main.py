from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
from routers.pack import router as pack_router
from routers.suggestion import router as suggestion_router
app = FastAPI()

origins = [
    "http://localhost:5173",
    "http://examhacker.ru"
]

app.include_router(pack_router)
app.include_router(suggestion_router)

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

@app.get("/ping")
def main():
    return "pong"

if __name__== "__main__":
    uvicorn.run(app, port=8067, host="0.0.0.0")
