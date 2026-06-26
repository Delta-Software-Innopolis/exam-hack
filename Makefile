.PHONY: prod dev test down db-shell generate-secrets logs ps restart help

DC = docker compose
ENV ?= dev

prod:
	$(DC) \
		-p prod \
		--env-file .env.prod \
		up --build -d

dev:
	$(DC) \
		-p dev \
		-f docker-compose.yml \
		-f docker-compose.dev.yml \
		--env-file .env.dev \
		up --build -d

test:
	$(DC) \
		-p test \
		-f docker-compose.yml \
		-f docker-compose.test.yml \
		--env-file .env.test \
		up --build -d

down:
	$(DC) -p $(ENV) down

db-shell:
	. ./.env.$(ENV) && $(DC) -p $(ENV) exec db psql -U $$POSTGRES_USER -d $$POSTGRES_DB

generate-secrets:
	openssl genpkey -algorithm RSA -out jwt_private.pem -pkeyopt rsa_keygen_bits:2048 && openssl rsa -pubout -in jwt_private.pem -out jwt_public.pem && mkdir auth/secrets && mkdir quiz_core/secrets && cp jwt_public.pem jwt_private.pem auth/secrets/ && cp jwt_public.pem quiz_core/secrets/

logs:
	$(DC) -p $(ENV) logs -f

ps:
	$(DC) -p $(ENV) ps

restart:
	$(DC) -p $(ENV) restart

help:
	@echo "make dev              - start app in dev mode (dev docker compose up)"
	@echo "make test             - start app in test mode (test docker compose up)"
	@echo "make prod             - start app in production mode (prod docker compose up"
	@echo "make down ENV=...     - stop app (docker compose down)"
	@echo "make db-shell ENV=... - internactive database shell (docker exec -it psql)"
	@echo "make logs ENV=...     - attach to the logs (docker logs -f)"
	@echo "make ps ENV=...       - show running containers (docker compose ps"
	@echo "make restart ENV=...  - restart app (docker compose restart)"
	@echo "make help             - show this message"
	@echo "NOTE: some commands use ENV=... option. If you dont specify it, it's dev by default, so for example if you do 'make db-shell', it will treat it as 'make db-shell ENV=dev'"
