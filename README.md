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
- Git
- Docker
- Docker Compose
- GNU Make

#### Clone the repository
```sh
git clone https://github.com/Delta-Software-Innopolis/exam-hack && cd exam-hack
```

#### Create secrets for JWT Auth
```sh
make generate-secrets 
```

#### Configure environment variables
```sh
cp .env.dev.example .env.dev
```

#### Run the project with Docker Compose
```sh
make dev
```
# API Documentation for quiz hub

Base URL:

```
/hub
```

Authentication:

Some endpoints require a **Bearer Token**.

```
Authorization: Bearer <your_token>
```

---

# Endpoints

## Health Check

### GET `/ping`

Returns a simple response indicating that the API is running.

#### Response

**200 OK**

```json
{}
```

---

## Packs

### GET `/packs/`

Returns a paginated list of published packs.

### Query Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| offset | integer | No | 1 | Pagination offset |
| limit | integer | No | 16 | Maximum number of packs returned |
| subject | string | No | - | Filter by subject |
| professor | string | No | - | Filter by professor |
| course_book | string | No | - | Filter by course book |
| university | string | No | - | Filter by university |
| search_main | string | No | - | General search |

### Response

**200 OK**

```json
{
  "packs": [
    {
      "id": 1,
      "author": {
        "id": 1,
        "name": "John Doe"
      },
      "rating": 4.7,
      "name": "Biology Midterm",
      "subject": "Biology",
      "university": "Harvard",
      "professor": "Dr. Smith",
      "course_book": "Campbell Biology",
      "forks": [
        {
          "id": 2,
          "rating": 4.5,
          "name": "Updated Version"
        }
      ]
    }
  ]
}
```

---

### POST `/packs/`

Publishes a new pack.

> **Authentication required**

### Request Body

```json
{
  "pack_id": 123,
  "subject": "Biology",
  "university": "Harvard",
  "professor": "Dr. Smith",
  "course_book": "Campbell Biology"
}
```

### Response

**200 OK**

```json
{
  "adding": "success"
}
```

---

### POST `/packs/{pack_id}`

Adds an existing published pack to the current user.

> **Authentication required**

### Path Parameters

| Parameter | Type | Required |
|-----------|------|----------|
| pack_id | integer | Yes |

### Response

**200 OK**

```json
{
  "message": "Pack added successfully"
}
```

---

## Suggestions

### GET `/suggestions/`

Returns autocomplete suggestions.

### Query Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| sug_type | string | Yes | Type of suggestion |
| q | string | Yes | Search query |

### Example

```
GET /suggestions/?sug_type=subject&q=math
```

### Response

**200 OK**

```json
{
  ["suggestion1". "suggestion2"]
}
```

---

# Data Models

## PublishedPackNew

Used when publishing a pack.

| Field | Type |
|--------|------|
| pack_id | integer |
| subject | string |
| university | string |
| professor | string |
| course_book | string |

---

## PublishedQuiz

| Field | Type |
|--------|------|
| id | integer |
| author | Author |
| rating | number \| null |
| name | string |
| subject | string |
| university | string |
| professor | string |
| course_book | string |
| forks | Fork[] |

---

## Author

| Field | Type |
|--------|------|
| id | integer |
| name | string |

---

## Fork

| Field | Type |
|--------|------|
| id | integer |
| rating | number \| null |
| name | string |

---

## PublishedQuizesResponse

```json
{
  "packs": [
    {
      "...": "PublishedQuiz"
    }
  ]
}
```

---

# Error Responses

Validation errors return **422 Unprocessable Entity**.

Example:

```json
{
  "detail": [
    {
      "loc": ["body", "field"],
      "msg": "Field required",
      "type": "missing"
    }
  ]
}
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
