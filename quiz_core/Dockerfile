FROM golang:tip-bookworm

WORKDIR /app

COPY . .
RUN go mod download

RUN go build -o main ./cmd
RUN chmod +x main

COPY secrets /app/secrets

CMD ["./main"]