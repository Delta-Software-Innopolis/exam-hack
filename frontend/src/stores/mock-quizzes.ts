import type { QuizItem } from '@/types'

export const MOCK_PREFIX = 'mock_'

export const MOCK_QUIZZES: QuizItem[] = [
  { id: 1,
    mock: true,
    name: "JavaScript Fundamentals",
    description: "Man, that's just a wonderful quiz to learn the fundamentals of JS. I cannot emphasize more of how I love it, it's just enermously splendid.",
    creation_date: new Date("2025-01-10"),
    updation_date: new Date("2025-05-20"),
    author: { id: 1, name: "You" },
    cards: [
      {
        id: 1,
        question: "Which keyword is used to declare a constant in JavaScript?",
        options: ["var", "let", "const", "static"],
        correct: [2],
        hint: "Introduced in ES6.",
        explanation: "The const keyword creates a block-scoped constant."
      },
      {
        id: 2,
        question: "What is the result of typeof null?",
        options: ["null", "object", "undefined", "boolean"],
        correct: [1],
        hint: "This is a historical JavaScript quirk.",
        explanation: "typeof null returns 'object'."
      },
      {
        id: 3,
        question: "Which method converts a JSON string into an object?",
        options: ["JSON.parse()", "JSON.stringify()", "JSON.object()", "JSON.decode()"],
        correct: [0],
        hint: "Used when receiving API responses.",
        explanation: "JSON.parse() converts JSON text into an object."
      },
      {
        id: 4,
        question: "Which operator checks both value and type equality?",
        options: ["==", "!=", "===", "="],
        correct: [2],
        hint: "Preferred in modern JavaScript.",
        explanation: "=== performs strict comparison."
      }
    ]
  },
  {
    id: 2,
    mock: true,
    name: "Vue 3 Essentials",
    creation_date: new Date("2025-02-14"),
    updation_date: new Date("2025-06-01"),
    author: { id: 1, name: "You" },
    forked_from: 1,
    cards: [
      {
        id: 5,
        question: "Which function creates a reactive reference in Vue 3?",
        options: ["reactive()", "watch()", "ref()", "computed()"],
        correct: [2],
        hint: "Often used for primitive values.",
        explanation: "ref() creates a reactive reference."
      },
      {
        id: 6,
        question: "What directive is used for two-way binding?",
        options: ["v-bind", "v-model", "v-if", "v-show"],
        correct: [1],
        hint: "Commonly used with form inputs.",
        explanation: "v-model provides two-way data binding."
      },
      {
        id: 7,
        question: "Which lifecycle hook runs after the component is mounted?",
        options: ["onBeforeMount", "onCreated", "onMounted", "onInit"],
        correct: [2],
        hint: "Useful for API requests.",
        explanation: "onMounted executes after mounting."
      },
      {
        id: 8,
        question: "Which store library is officially recommended for Vue 3?",
        options: ["Redux", "MobX", "Pinia", "Vuex 3"],
        correct: [2],
        hint: "Successor to Vuex.",
        explanation: "Pinia is the recommended state management solution."
      }
    ]
  },
  {
    id: 3,
    mock: true,
    name: "Docker Basics",
    creation_date: new Date("2025-03-01"),
    updation_date: new Date("2025-06-15"),
    author: { id: 1, name: "You" },
    description: "Core concepts required to work with Docker containers.",
    cards: [
      {
        id: 9,
        question: "Which file is used to define a Docker image build process?",
        options: ["docker-compose.yml", "Dockerfile", ".dockerignore", "container.yml"],
        correct: [1],
        hint: "Usually located in the project root.",
        explanation: "Dockerfile contains image build instructions."
      },
      {
        id: 10,
        question: "Which command lists running containers?",
        options: ["docker images", "docker logs", "docker ps", "docker exec"],
        correct: [2],
        hint: "Think 'process status'.",
        explanation: "docker ps displays active containers."
      },
      {
        id: 11,
        question: "What does docker-compose primarily help with?",
        options: [
          "Building Linux kernels",
          "Managing multiple containers",
          "Creating databases",
          "Writing Dockerfiles"
        ],
        correct: [1],
        hint: "Useful for microservices.",
        explanation: "Docker Compose orchestrates multiple containers."
      },
      {
        id: 12,
        question: "Which flag runs a container in detached mode?",
        options: ["-it", "-a", "-d", "-rm"],
        correct: [2],
        hint: "Container runs in background.",
        explanation: "-d starts the container detached."
      },
      {
        id: 13,
        question: "What is the purpose of .dockerignore?",
        options: [
          "Ignore build context files",
          "Ignore containers",
          "Ignore ports",
          "Ignore images"
        ],
        correct: [0],
        hint: "Similar to .gitignore.",
        explanation: ".dockerignore excludes files from build context."
      }
    ]
  },
  {
    id: 4,
    mock: true,
    name: "REST APIs",
    creation_date: new Date("2025-03-15"),
    updation_date: new Date("2025-06-20"),
    author: { id: 1, name: "You" },
    description: "Understanding common HTTP methods and status codes.",
    cards: [
      {
        id: 14,
        question: "Which HTTP method is typically used to create a resource?",
        options: ["GET", "POST", "DELETE", "HEAD"],
        correct: [1],
        hint: "Often used when submitting forms.",
        explanation: "POST is commonly used to create resources."
      },
      {
        id: 15,
        question: "What status code indicates success?",
        options: ["404", "500", "200", "301"],
        correct: [2],
        hint: "The most common success code.",
        explanation: "200 OK indicates a successful request."
      },
      {
        id: 16,
        question: "Which status code means 'Unauthorized'?",
        options: ["401", "403", "404", "400"],
        correct: [0],
        hint: "Frequently returned for invalid tokens.",
        explanation: "401 indicates authentication is required."
      },
      {
        id: 17,
        question: "Which header is commonly used for JWT authentication?",
        options: ["Content-Type", "Origin", "Authorization", "Cache-Control"],
        correct: [2],
        hint: "Usually starts with Bearer.",
        explanation: "Authorization carries access credentials."
      }
    ]
  },
  {
    id: 5,
    mock: true,
    name: "PostgreSQL Essentials",
    creation_date: new Date("2025-04-01"),
    updation_date: new Date("2025-06-18"),
    author: { id: 1, name: "You" },
    cards: [
      {
        id: 18,
        question: "Which command retrieves data from a table?",
        options: ["INSERT", "SELECT", "UPDATE", "CREATE"],
        correct: [1],
        hint: "Most frequently used query.",
        explanation: "SELECT retrieves rows from a table."
      },
      {
        id: 19,
        question: "Which clause filters query results?",
        options: ["ORDER BY", "GROUP BY", "WHERE", "JOIN"],
        correct: [2],
        hint: "Used before sorting.",
        explanation: "WHERE filters rows."
      },
      {
        id: 20,
        question: "What type is commonly used for auto-increment IDs?",
        options: ["TEXT", "BOOLEAN", "SERIAL", "DATE"],
        correct: [2],
        hint: "PostgreSQL-specific.",
        explanation: "SERIAL creates an auto-incrementing integer."
      },
      {
        id: 21,
        question: "Which keyword sorts rows?",
        options: ["SORT", "ORDER BY", "GROUP", "FILTER"],
        correct: [1],
        hint: "Can be ASC or DESC.",
        explanation: "ORDER BY controls sorting."
      }
    ]
  }
]
