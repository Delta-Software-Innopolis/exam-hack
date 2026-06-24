# ExamHacker
ExamHacker is an educational platform for AI-powered quiz generation, sharing, and passive learning. The platform consists of a web application and an Android application synchronized through a shared backend.

Students can upload lecture materials and automatically generate quizzes and flashcards using AI or create them manually. Quizzes can be shared privately via links or published to a community-driven QuizHub where users can discover, rate, comment on, and download study materials. The Android application can display learning cards during everyday smartphone usage, such as after unlocking the phone or opening selected applications.

### Key Features
- AI-powered quiz and flashcard generation from study materials
- Manual quiz creation and editing
- Public QuizHub with ratings, comments, and tags
- Quiz sharing via links
- Cross-platform synchronization between web and mobile
- Passive learning through contextual mobile flashcards
- Progress tracking and gamification

---

## Tech Stack
- Backend - Golang with Gin, GORM and FastAPI with gRPC, SQLAlchemy
- Databases - PostgreSQL
- Mobile App - Kotlin, Android SDK, Jetpack Compose, Room,
- Decompose, Ktor
- Web Interface - Vue.js with TypeScript
- DevOps - GitHub Actions, Docker
- ML - Python with Pydantic and OpenAI

## Launch/Access Instructions
#### You can access ExamHacker by the link: http://111.88.118.254:9080/

#### Or you can build the app yourself:

#### Prerequisites
- Docker
- Docker compose

#### Clone the repository
```sh
git clone https://github.com/Delta-Software-Innopolis/exam-hack && cd exam-hack
```
#### Create secrets for JWT Auth
```sh
openssl genpkey -algorithm RSA -out jwt_private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in jwt_private.pem -out jwt_public.pem
mkdir auth/secrets
mkdir quiz_core/secrets
cp jwt_public.pem jwt_private.pem auth/secrets/
cp jwt_public.pem quiz_core/secrets
```

#### Configure environment variables
```sh
cp .env.dev.example .env.dev
```

#### Run the project with Docker Compose
```sh
docker compose -p dev -f docker-compose.yml -f docker-compose.dev.yml --env-file .env.dev up --build -d
```


## Team members
- Timur Chumaraev - Frontend
- Alexander Blinov - Backend
- Konstantin Ustiuzhanin - Backend/DevOps
- Pavel Mashchenko - Mobile
- Daria Evdokimova - Mobile
- Viktor Konovalov - Fullstack, Project manager
- Mikhail Novikov - ML/DevOps
- Stepan Tarabin - Design/Fullstack
