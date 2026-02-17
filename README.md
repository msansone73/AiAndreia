# AiAndreia 🤖

A **Spring Boot middleware** that acts as a bridge between client applications and the [Ollama](https://ollama.com/) AI engine. It provides a REST API to manage users, send AI-powered chat requests with full conversation context, and summarize conversation history to optimise token usage.

---

## ✨ Features

- **AI Chat with Context** — Sends questions to Ollama including the user's full conversation history (or a compressed summary) so the AI can give contextual answers.
- **Conversation Summarization** — Generates a compact summary of all past conversations to reduce token consumption in future interactions.
- **User Management** — Full CRUD for user accounts.
- **History Reset** — Allows clearing a user's conversation history.
- **PostgreSQL Persistence** — Stores all requests, answers, and user data.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.2 |
| Language | Java 21 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| AI Engine | Ollama (local or remote) |
| Build Tool | Maven |
| Other | Lombok, Jackson, Bean Validation |

---

## 📋 Prerequisites

- **Java 21** (or later)
- **Maven 3.9+** (or use the included Maven Wrapper `mvnw`)
- **PostgreSQL** instance (local or remote)
- **Ollama** running and accessible (default: `http://localhost:11434`)

---

## 🚀 Installation & Setup

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/AiAndreia.git
cd AiAndreia
```

### 2. Configure the application

Edit `src/main/resources/application.properties`:

```properties
# PostgreSQL connection
spring.datasource.url=jdbc:postgresql://localhost:5432/your-database
spring.datasource.username=your-username
spring.datasource.password=your-password

# Hibernate auto-creates/updates tables
spring.jpa.hibernate.ddl-auto=update

# Ollama AI API URL
ollama.api.url=http://localhost:11434
```

### 3. Build & Run

Using the Maven Wrapper (no Maven installation required):

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

The server starts on **port 8080** by default.

---

## 🐳 Docker

The project includes a `Dockerfile` and `docker-compose.yml` to run the **application + PostgreSQL + Ollama** with a single command.

### Quick Start

```bash
# Build and start all services
docker compose up -d

# Check that everything is running
docker compose ps

# View application logs
docker compose logs -f app

# Stop all services
docker compose down
```

The compose file exposes:

| Service | URL |
|---|---|
| AiAndreia API | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` |
| Ollama | `http://localhost:11434` |

> [!TIP]
> Database data and Ollama models are persisted in Docker volumes (`pgdata` and `ollama_data`). To start fresh, run `docker compose down -v`.

---

## 🔌 API Endpoints

Base URL: `http://localhost:8080/aiandreia`

### AI Request

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/aiandreia/request` | Send a question to the AI |

**Request Body:**

```json
{
  "question": "What is Spring Boot?",
  "model": "llama3",
  "userId": 1
}
```

**Response:**

```json
{
  "answer": "Spring Boot is a framework that..."
}
```

> The service automatically includes the user's conversation history (or summary) as context when calling Ollama.

---

### User Management

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/aiandreia/user` | Create a new user |
| `GET` | `/aiandreia/user` | List all users |
| `GET` | `/aiandreia/user/{id}` | Get user by ID |
| `PUT` | `/aiandreia/user/{id}` | Update an existing user |
| `DELETE` | `/aiandreia/user/{id}` | Delete a user (and their history) |

**Create / Update Request Body:**

```json
{
  "email": "user@example.com",
  "password": "securepassword",
  "name": "John Doe"
}
```

**Response:**

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "createdAt": "2026-02-17T10:30:00"
}
```

---

### Conversation History

| Method | Endpoint | Description |
|---|---|---|
| `DELETE` | `/aiandreia/user/{id}/history` | Reset (delete) all conversation history for a user |
| `POST` | `/aiandreia/user/{id}/resumir` | Generate and store a summary of the user's conversation history |

**Summarize Response** — returns the generated summary as plain text.

---

## 🗄️ Database Schema

The application uses **Hibernate auto-DDL** (`spring.jpa.hibernate.ddl-auto=update`) so tables are created/updated automatically. Below is the schema:

### `tb_user`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | PK, Auto-increment | User unique identifier |
| `email` | `VARCHAR(255)` | NOT NULL, UNIQUE | User email address |
| `password` | `VARCHAR(255)` | NOT NULL | User password |
| `name` | `VARCHAR(255)` | NOT NULL | User display name |
| `created_at` | `TIMESTAMP` | NOT NULL | Account creation timestamp |
| `resumo` | `TEXT` | Nullable | Compressed summary of past conversations |

### `ai_requests`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | `BIGINT` | PK, Auto-increment | Request unique identifier |
| `question` | `TEXT` | NOT NULL | The question sent to the AI |
| `model` | `VARCHAR(255)` | NOT NULL | Ollama model used (e.g. `llama3`) |
| `answer` | `TEXT` | Nullable | The AI response |
| `created_at` | `TIMESTAMP` | NOT NULL | Request timestamp |
| `user_id` | `BIGINT` | FK → `tb_user.id` | The user who made the request |

### Entity Relationship

```
tb_user (1) ──────< (N) ai_requests
```

A user can have many AI requests. Each AI request belongs to one user.

---

## 📁 Project Structure

```
src/main/java/br/com/msansone/aiandreia/
├── AiandreiaApplication.java          # Spring Boot entry point
├── config/
│   └── WebConfig.java                 # CORS and web configuration
├── controller/
│   ├── AiRequestController.java       # AI request endpoint
│   └── UserController.java            # User CRUD + history endpoints
├── dto/
│   ├── AiRequestDTO.java              # AI request input
│   ├── AiResponseDTO.java             # AI response output
│   ├── UserDTO.java                   # User input
│   └── UserResponseDTO.java           # User response output
├── entity/
│   ├── AiRequest.java                 # JPA entity for ai_requests table
│   └── User.java                      # JPA entity for tb_user table
├── integration/
│   ├── OllamaClient.java             # HTTP client for Ollama API
│   └── dto/                           # Ollama API request/response DTOs
├── repository/
│   ├── AiRequestRepository.java       # JPA repository for AI requests
│   └── UserRepository.java            # JPA repository for users
└── service/
    ├── AiRequestService.java          # AI request business logic
    └── UserService.java               # User management business logic
```

---

## 🔄 How Context Works

1. When a user sends a question via `POST /aiandreia/request`, the service checks if the user has a stored **summary** (`resumo`).
2. **If a summary exists** → it is sent as a `system` message to Ollama, providing context without sending the entire history.
3. **If no summary exists** → the full conversation history (all past questions and answers) is sent as context.
4. The new question and AI answer are always persisted in the `ai_requests` table.
5. Users can call `POST /aiandreia/user/{id}/resumir` at any time to generate/refresh the summary and reduce future token usage.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
