# Train & Tension Backend

Train & Tension Backend is a runnable Spring-based backend monorepo for the platform's gateway, identity, core domain API, and shared backend library. The repository brings multiple backend services together under a Maven reactor and provides local infrastructure with Docker Compose.

The project is designed as a small microservice backend: external clients enter through the gateway, authentication and user management live in the identity service, workout/profile domain operations live in the core service, and shared concerns are extracted into a local common module.

## Architecture

```mermaid
%%{init: {"flowchart": {"defaultRenderer": "elk", "curve": "linear", "nodeSpacing": 90, "rankSpacing": 95}} }%%

flowchart TB
    client["Client<br/>Web / Mobile"]

    subgraph repo["Train & Tension Backend Monorepo"]
        direction TB

        gateway["Gateway<br/>Spring Cloud Gateway<br/>:8080"]

        subgraph app["Application Services"]
            direction LR
            identity["Identity Service<br/>Auth / Users<br/>:8081"]
            core["Core Service<br/>Workout / Profile API<br/>:8082"]
        end

        subgraph storage["Storage & Cache"]
            direction LR
            mongo["MongoDB 7<br/>identity database<br/>:27017"]
            redis["Redis 7<br/>shared cache / tokens<br/>:6379"]
            postgres["PostgreSQL 18<br/>core database<br/>:5432"]
        end

        gateway --> identity
        gateway --> core

        identity -. "user profile client" .-> core

        identity --> mongo
        identity --> redis
        core --> redis
        core --> postgres
    end

    client --> gateway

    classDef external fill:#fff7ed,stroke:#9a3412,color:#111;
    classDef gateway fill:#e0f2fe,stroke:#0369a1,color:#111;
    classDef service fill:#eef2ff,stroke:#4338ca,color:#111;
    classDef database fill:#ecfdf5,stroke:#047857,color:#111;
    classDef cache fill:#fef9c3,stroke:#a16207,color:#111;

    class client external;
    class gateway gateway;
    class identity,core service;
    class mongo,postgres database;
    class redis cache;
```

## Services

- `services/gateway`: Reactive Spring Cloud Gateway entrypoint. It routes `/api/identity/**` and `/api/core/**` traffic to the internal services and exposes Swagger routes for local inspection.
- `services/identity`: Authentication and user management service. It stores identity data in MongoDB, uses Redis for refresh token state, signs JWTs, and calls `core` when a user profile must be created.
- `services/core`: Workout, profile, sync, and catalog domain API. It uses PostgreSQL as the source of truth, Flyway for schema migrations, jOOQ for type-safe SQL access, Redis for shared cache state, and Caffeine for local in-memory caching.
- `libs/common`: Shared DTOs, exceptions, user context utilities, role checks, and cross-service support code.

## Technology Stack

- Java 25
- Spring Boot 4
- Spring Cloud Gateway WebFlux
- Spring Web MVC
- Maven multi-module reactor
- PostgreSQL 18
- MongoDB 7
- Redis 7
- Flyway
- jOOQ
- Springdoc OpenAPI / Swagger UI
- Docker and Docker Compose
- GitHub Actions CI

## Repository Layout

```text
.
+-- compose.yaml
+-- infra/
|   +-- docker/
+-- libs/
|   +-- common/
+-- scripts/
|   +-- generate-jwt-env.ps1
|   +-- generate-jwt-env.sh
+-- services/
    +-- core/
    +-- gateway/
    +-- identity/
```

## Design Notes

- The repository uses a monorepo layout so the services and shared library can be built together with one Maven command.
- Each service keeps its own Spring Boot application, Dockerfile, runtime configuration, and port.
- `common` is included as a local Maven module, so local and Docker builds do not require GitHub Packages credentials.
- The gateway is the public backend entrypoint. Service-to-service communication stays inside the Compose network.
- `identity` and `core` both use the same Redis instance, but with separate logical responsibilities.
- `identity` uses MongoDB because user/auth data is document-oriented and evolves independently from the workout domain.
- `core` uses PostgreSQL with Flyway and jOOQ for relational domain data, migrations, and type-safe SQL queries.
- Service startup is guarded by Docker healthchecks so dependent services wait for infrastructure and upstream services to become healthy.

## Prerequisites

- Docker Desktop with Docker Compose.
- OpenSSL for generating local ES256 JWT keys.
- Java 25 only if you want to run Maven locally outside Docker.

## Run Locally

Generate the local `.env` file and JWT key pair:

```powershell
.\scripts\generate-jwt-env.ps1
```

On macOS/Linux:

```sh
./scripts/generate-jwt-env.sh
```

Start the full stack:

```sh
docker compose up --build
```

If you previously started the stack with an older schema or PostgreSQL version, reset local volumes before starting again:

```sh
docker compose down -v
docker compose up --build
```

Default URLs:

- Gateway: `http://localhost:8080`
- Identity through gateway: `http://localhost:8080/api/identity`
- Core through gateway: `http://localhost:8080/api/core`
- Identity Swagger: `http://localhost:8081/swagger/identity/swagger-ui`
- Core Swagger: `http://localhost:8082/swagger/core/swagger-ui`

## Build Locally

Build everything with the Maven reactor:

```sh
./mvnw clean package -DskipTests
```

Build a single service and its dependencies:

```sh
./mvnw -pl services/core -am package -DskipTests
./mvnw -pl services/identity -am package -DskipTests
./mvnw -pl services/gateway -am package -DskipTests
```

## Configuration

Copy `.env.example` or run the key generation script to create `.env`. The generated `.env` is for local development only and is intentionally ignored by git.

Important variables:

- `JWT_PRIVATE_KEY`: PKCS#8 DER base64 private key used by `identity`.
- `JWT_PUBLIC_KEY`: X.509 DER base64 public key used by `gateway`.
- `CORE_DB_*`: PostgreSQL 18 settings for `core`; Flyway migrations use the built-in `uuidv7()` function.
- `ID_MONGO_*`: MongoDB settings for `identity`.
- `REDIS_*`: Shared Redis settings.
- `SWAGGER_ENABLED`: Enables Springdoc routes for local inspection.

## Reliability Checks

- GitHub Actions validates the Maven reactor build on every push and pull request.
- Docker Compose config is validated in CI.
- PostgreSQL, MongoDB, Redis, `core`, `identity`, and `gateway` have local healthchecks.
- Service startup ordering waits for dependencies to become healthy before dependent services start.
- Spring Actuator health endpoints are used by service healthchecks.

## Status

The local stack is runnable with Docker Compose and includes the gateway, identity service, core service, PostgreSQL, MongoDB, and Redis.
