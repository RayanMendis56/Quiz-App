# Quiz App

A full-stack quiz application. Players pick a category and difficulty, answer timed multiple-choice questions, get a score, and see how they rank on a leaderboard.

Built with **React**, **Spring Boot** and **MySQL** as a layered monolith.

<!-- TODO: add screenshots. Save images in docs/screenshots/ and link them like this:
![Welcome Screen](docs/screenshots/welcome.png)
-->

## Features

- Choose from **3 categories** (Java, Python, JavaScript) and **3 difficulty levels** (Easy, Medium, Hard)
- Random question selection for every quiz
- **30-second countdown** per question, with automatic advance when time runs out
- Score screen with percentage and feedback message
- **Leaderboard** with top 10 scores overall and per category
- Full CRUD API for managing questions

## Tech Stack

| Layer    | Technology                                      |
|----------|-------------------------------------------------|
| Frontend | React (Create React App), plain CSS, Fetch API  |
| Backend  | Java, Spring Boot 3.4, Spring Web, Spring Data JPA, Hibernate, Lombok |
| Database | MySQL 8                                         |
| Build    | Maven (backend), npm (frontend)                 |

## Architecture

The backend follows a **layered architecture**:

```
Controller  →  Service  →  Repository  →  MySQL
(REST API)    (business     (Spring Data
               logic)        JPA)
```

```
Quiz-App/
├── backend/
│   └── src/main/java/com/rayan/
│       ├── controller/   REST endpoints
│       ├── service/      business logic and scoring
│       ├── repository/   Spring Data JPA repositories
│       ├── model/        entities and response wrappers
│       └── config/       CORS configuration
└── frontend/
    └── src/
        ├── pages/        Welcome, Quiz, Score and Leaderboard screens
        ├── api.js        all calls to the backend
        └── App.jsx       screen flow and shared state
```

## Getting Started

### Prerequisites

- Java 17 or newer
- Maven (or use the IntelliJ built-in runner)
- Node.js and npm
- MySQL 8

### 1. Clone the repository

```bash
git clone https://github.com/RayanMendis56/Quiz-App.git
cd Quiz-App
```

### 2. Create the database

```sql
CREATE DATABASE questiondb;
```

Tables are created automatically by Hibernate (`ddl-auto=update`) the first time the backend starts.

### 3. Configure the backend

Database credentials are read from environment variables, so nothing sensitive lives in the code:

| Variable       | Required | Default                                  |
|----------------|----------|------------------------------------------|
| `DB_PASSWORD`  | Yes      | none                                     |
| `DB_USERNAME`  | No       | `root`                                   |
| `DB_URL`       | No       | `jdbc:mysql://localhost:3306/questiondb` |

```bash
export DB_PASSWORD=your_mysql_password
```

In IntelliJ, set it under **Run → Edit Configurations → Environment variables**.

### 4. Start the backend

Run the `Main` class from your IDE, or from the `backend` folder:

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

### 5. Load the sample questions

Run the SQL seed scripts against `questiondb` (MySQL Workbench or the `mysql` CLI). They insert questions for each category and difficulty.

<!-- TODO: add the seed scripts to the repo, for example backend/sql/, and link them here. -->

### 6. Start the frontend

```bash
cd frontend
npm install
npm start
```

The app opens at `http://localhost:3000`.

## API Reference

### Questions

| Method | Endpoint                        | Description                                  |
|--------|---------------------------------|----------------------------------------------|
| GET    | `/question/allQuestions`        | All questions (correct answers are not included) |
| GET    | `/question/category/{category}` | Questions in one category                    |
| POST   | `/question/add`                 | Add a question                               |
| PUT    | `/question/update/{id}`         | Replace a question                           |
| DELETE | `/question/delete/{id}`         | Delete a question                            |

### Quizzes

| Method | Endpoint                                                | Description                                      |
|--------|---------------------------------------------------------|--------------------------------------------------|
| POST   | `/quiz/create?category=&level=&numQ=&title=`            | Creates a quiz from random questions, returns its id |
| GET    | `/quiz/get/{id}`                                        | Questions for a quiz (without correct answers)   |
| POST   | `/quiz/submit/{id}?playerName=`                         | Scores the answers and saves the result to the leaderboard |

Submit body:

```json
[
  { "id": 12, "response": "2" },
  { "id": 31, "response": "4" }
]
```

`id` is the question id and `response` is the chosen option number (1 to 4). The response is the player's score as an integer.

### Leaderboard

| Method | Endpoint                     | Description                  |
|--------|------------------------------|------------------------------|
| GET    | `/leaderboard/top`           | Top 10 scores overall        |
| GET    | `/leaderboard/top/{category}`| Top 10 scores in a category  |

## Design Decisions

- **Correct answers never reach the browser.** Quiz questions are returned through a `QuestionWrapper` that leaves out the answer, so players can't find it in the network tab.
- **Scoring happens on the server.** The frontend sends only the player's choices, and the backend grades them, so scores can't be faked from the client.
- **Answers are matched by question id,** not by position, so the order in which they arrive doesn't affect the score.
- **Leaderboard entries are saved during submission,** in the same request that calculates the score.
- **Credentials come from environment variables,** keeping secrets out of version control.
- **A monolith by choice.** An app this size doesn't need the overhead of microservices.

## Known Limitations

- Random selection uses `ORDER BY RAND()`, which is fine at this scale but would need a different approach for a large question table.
- Submitting the same quiz twice creates two leaderboard entries.
- Leaderboard ranking uses the raw score, which assumes every quiz has the same number of questions.
- There is no user authentication yet, and the question management endpoints are open.

## Roadmap

- [ ] Input validation and a global exception handler
- [ ] Unit and integration tests (JUnit 5, Mockito)
- [ ] User accounts and JWT authentication, with protected admin endpoints
- [ ] More questions per category and level
- [ ] Deployment

## Author

**Rayan Mendis**, [GitHub](https://github.com/RayanMendis56)
