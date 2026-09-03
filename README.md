# Task Manager Pro

A full-stack todo application: a Spring Boot REST API backend and a SvelteKit
frontend, with session-cookie authentication and role-based (USER/ADMIN)
authorization.

## Domain model

The application is built around one bounded context ("task management") with
two entities:

- **User** - the aggregate root. Owns a collection of Todos. Has a role
  (`USER`, `ADMIN`, or `GUEST`) and an active/inactive `status`. Deleting a
  User cascades and deletes all of that user's Todos - the aggregate's one
  real consistency boundary (`@OneToMany(cascade = ALL, orphanRemoval = true)`
  on `User.todos`).
- **Todo** - owned by exactly one User. Carries a description, a completion
  flag (`status`), a declared/creation timestamp, and a completion timestamp.
  The invariant *"completionDT is set if and only if status is true"* lives
  inside the entity itself, in two methods - `complete()` and `reopen()` -
  rather than being reconstructed ad hoc wherever the status changes.

```
        ┌───────────────────────┐
        │        User            │  aggregate root
        │  id, fullName, email,   │
        │  password, status, type │
        └───────────┬─────────────┘
                     │ 1
                     │ owns
                     │ *
        ┌────────────▼─────────────┐
        │           Todo            │
        │  id, description, status, │
        │  creationDT, completionDT │
        └────────────────────────────┘
```

Ubiquitous language used throughout the code and API: a **User** is anyone
with an account (an `ADMIN` manages other users; a `USER` manages only their
own data); a **Todo** ("task" in a couple of older endpoint names - they mean
the same thing) belongs to exactly one User and is either *open* or
*completed*.

### Backend layering

Package-by-feature, not package-by-layer - each business capability is a
self-contained module instead of being spread across shared top-level
`controllers/`, `services/`, `entities/` folders:

```
com.pireaus.todoWebApp
├── user/                    the User aggregate
│   ├── User.java              entity (aggregate root)
│   ├── UserController.java    thin HTTP adapter
│   ├── UserService.java       business rules + authorization
│   ├── UserRepo.java          Spring Data JPA repository
│   └── dto/                   request/response records - the API's
│                               actual contract, decoupled from the entity
├── todo/                    the Todo entity
│   ├── Todo.java, TodoController.java, TodoService.java, TodoRepo.java
│   └── dto/
├── security/                 Spring Security config + user-details lookup
└── common/
    ├── exception/             domain exceptions + centralized HTTP mapping
    └── config/                OpenAPI/Swagger bean
```

Each request still flows Controller -> Service -> Repository -> Entity, same
as a classic layered architecture - the difference is that a controller,
its service, its repository and its DTOs live in one folder together instead
of four separate ones, so a new feature is a new package instead of four
simultaneous edits. Controllers only translate HTTP <-> DTOs; every
authorization check ("is this the owner, or an admin?") and business rule
lives in the service layer, one place per concern.

## Stack

Backend: Java, Spring Boot (Spring MVC + Spring Data JPA + Spring Security),
MySQL, Flyway migrations, Maven. Frontend: SvelteKit (Svelte 5). See
`STACK.txt` in this folder for exact versions and more detail.

## API documentation

Once the backend is running, interactive Swagger UI is at:

```
http://localhost:8080/swagger-ui/index.html
```

(raw OpenAPI JSON at `/v3/api-docs`). Since the API uses session-cookie auth,
log in once via `POST /login` (`application/x-www-form-urlencoded`:
`username`, `password`) from a client that keeps cookies - e.g. Postman with
its cookie jar enabled - before calling any endpoint other than
`/register`/`/login`.

## Default admin account

A default administrator is seeded by the Flyway migration
`V3__DEFAULT_ADMIN.sql` the first time the backend starts against a fresh
database:

- email: `admin@todoApp.gr`
- password: `P@ssw0rd`

## Running it

### Option A: Docker (recommended - one command, nothing to install)

From this folder:

```
docker compose up --build
```

This builds and starts four containers - `mysql`, `backend`, `frontend`, and
an `nginx` reverse proxy - and seeds the database via Flyway on first boot.
Once it's up, open **http://localhost:8081** (nginx routes API paths to the
backend and everything else to the frontend, so the session cookie behaves
exactly as it does in local dev). `docker compose down` stops everything;
add `-v` to also wipe the database volume.

Environment variables the backend container reads (already set in
`docker-compose.yml`, override there if needed): `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

### Option B: running locally without Docker

Requirements: a JDK matching `backend/pom.xml`'s `<java.version>`, Node.js
22+, and a running MySQL instance.

**Backend**

1. Create the database MySQL is expecting (see `spring.datasource.url` in
   `backend/src/main/resources/application.properties` - by default
   `todo_mysql_tasos`), and make sure the username/password there match your
   local MySQL.
2. From `backend/`:
   ```
   ./mvnw spring-boot:run
   ```
   (Windows: `mvnw.cmd spring-boot:run`.) Flyway runs the migrations
   automatically on startup, including the default-admin seed. The API
   listens on `http://localhost:8080`.

**Frontend**

From `frontend/`:
```
npm install
npm run dev
```
This starts the Vite dev server (default `http://localhost:5173`), which
proxies `/login`, `/logout`, `/register`, `/me`, `/users`, `/clients` to the
backend on `localhost:8080` (see `frontend/vite.config.js`) so the session
cookie stays same-origin. Open the dev server URL in a browser.

### Building the frontend for production (outside Docker)

```
cd frontend
npm run build
node build
```
`npm run build` uses `@sveltejs/adapter-node`, producing a standalone Node
server in `frontend/build` (`node build` runs it, default port 3000). In
this mode there's no dev proxy, so it needs to sit behind something that
routes API paths to the backend the way `nginx/nginx.conf` does for the
Docker setup.

## Project layout

```
AUEB_TODO_WEB_APP/
├── backend/            Spring Boot REST API (see backend/pom.xml, Dockerfile)
├── frontend/            SvelteKit app (see frontend/package.json, Dockerfile)
├── nginx/nginx.conf      reverse proxy config used by docker-compose
├── docker-compose.yml    mysql + backend + frontend + nginx, one command
└── STACK.txt             detailed technology stack breakdown
```
