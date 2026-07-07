FROM golang:tip-bookworm

RUN apt-get update && apt-get install -y \
    poppler-utils \
    wv \
    unrtf \
    tidy \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Сначала только файлы зависимостей
COPY go.mod go.sum ./
RUN go mod download

# Потом весь остальной код
COPY . .

RUN go build -o main ./cmd
RUN chmod +x main

CMD ["./main"]
