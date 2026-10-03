# WorkHub

![CI](https://github.com/jgoul3/workhub/actions/workflows/ci.yml/badge.svg)

An enterprise work management tool with built-in budgeting: track projects and tasks, log expenses through an approval workflow, and see how each project is tracking against its budget.

> **Status:** In active development. The Projects API is working; tasks, expenses, budgeting, authentication, and the React frontend are on the roadmap below.

## Tech stack

- **Backend:** Java 25, Spring Boot 4 (Web MVC, Data JPA, Validation)
- **Database:** PostgreSQL 16, with schema migrations managed by Flyway
- **Testing:** JUnit, AssertJ, Mockito, MockMvc
- **CI:** GitHub Actions, running the full test suite against a fresh PostgreSQL instance on every push
- **Planned:** React frontend, Spring Security with JWT, Docker, AWS

## Getting started

### Prerequisites

- JDK 25
- Docker Desktop

### Run locally

1. Start the database from the repository root:

   ```bash
   docker compose up -d
   ```

2. Start the application from the `workhub` folder:

   ```bash
   cd workhub
   ./mvnw spring-boot:run
   ```

   On Windows, use `mvnw.cmd spring-boot:run`.

The API runs at `http://localhost:8080`. Flyway creates the database tables automatically on first startup.

### Run the tests

From the `workhub` folder, with the database running:

```bash
./mvnw test
```

## API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/projects` | Create a project |
| `GET` | `/api/projects` | List all projects |
| `GET` | `/api/projects/{id}` | Get a single project |

Example request:

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Website Redesign",
    "description": "Refresh the company site",
    "budget": 25000.00,
    "plannedStartDate": "2026-10-01",
    "plannedEndDate": "2026-12-15"
  }'
```

Errors are returned in the standard [Problem Details](https://www.rfc-editor.org/rfc/rfc9457) format (RFC 9457). Validation errors include the specific fields that failed:

```json
{
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields are invalid.",
  "errors": {
    "name": "Name cannot be blank."
  }
}
```

## Design decisions

- **Package by feature.** Each feature (such as `project`) keeps its entity, repository, service, controller, and DTOs together, rather than splitting code by layer.
- **DTOs at the API boundary.** Request and response records control exactly what clients can send and see, so internal fields like status and timestamps can't be set from outside.
- **Business rules live in the domain model.** For example, a project's planned end date can never be before its start date. The `Project` entity enforces this itself, so no code path can bypass it.
- **Versioned schema migrations.** The database schema is defined by Flyway migrations, and Hibernate only validates against it (`ddl-auto=validate`).
- **Exact money handling.** Budgets use `BigDecimal` in Java and `NUMERIC` in PostgreSQL, never floating point.
- **Centralized, safe error handling.** One handler produces every error response. Unexpected errors return a generic message to the client and are logged in full on the server.

## Roadmap

- [x] Projects: create and read
- [x] Unit and controller tests
- [x] Centralized error handling
- [x] Continuous integration
- [ ] Projects: update and status transitions
- [ ] Tasks
- [ ] Expenses with an approval workflow
- [ ] Budget summary and overspend warnings
- [ ] Authentication and role-based access
- [ ] React frontend
- [ ] Docker and AWS deployment
- [ ] AI-assisted features
