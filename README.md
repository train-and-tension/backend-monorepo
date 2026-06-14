# Train & Tension Backend

Train & Tension Backend is a runnable Spring-based backend monorepo for the platform's gateway, identity, core domain API, and shared backend library. The repository brings multiple backend services together under a Maven reactor and provides local infrastructure with Docker Compose.

The project is designed as a small microservice backend: external clients enter through the gateway, authentication and user management live in the identity service, workout/profile domain operations live in the core service, and shared concerns are extracted into a local common module.

## Key Engineering Highlights

- Consolidated gateway, identity, core, and shared common modules into a Maven multi-module monorepo.
- Built a runnable local backend environment with Docker Compose, PostgreSQL, MongoDB, Redis, and service healthchecks.
- Implemented gateway-based routing for identity and core APIs through Spring Cloud Gateway.
- Separated authentication/user management from workout/profile domain logic with service-to-service communication.
- Used PostgreSQL, Flyway, and jOOQ for relational domain data; MongoDB for identity data; Redis for token and cache state.
- Added GitHub Actions CI to validate Maven builds and Docker Compose configuration.

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

## Core Domain Model

The core service domain is derived from the Flyway migrations under `services/core/src/main/resources/db/migration`. It models user fitness profiles, body measurements, exercise catalogs, workout plans, scheduled/completed sessions, completed set results, saved exercises, and client synchronization state.

```mermaid
erDiagram
    USER_PROFILE {
        UUID id PK
        VARCHAR username UK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR profile_pic_url
        VARCHAR timezone
        BIGINT version
        TIMESTAMPTZ created_at
    }

    BODY_INFORMATION {
        UUID id PK
        UUID user_profile_id FK
        training_goal training_goal
        weight_goal weight_goal
        DECIMAL weight_kg
        DECIMAL height_cm
        DATE birth_date
        gender gender
        activity_level activity_level
        unit_system unit
        BIGINT version
        TIMESTAMPTZ created_at
    }

    MEASUREMENT_HISTORY {
        UUID id PK
        UUID user_profile_id FK
        DECIMAL weight_kg
        DECIMAL height_cm
        unit_system unit
        BIGINT version
        TIMESTAMPTZ created_at
    }

    EQUIPMENT {
        UUID id PK
        UUID user_profile_id FK
        VARCHAR name
        VARCHAR description
        VARCHAR media_url
        BIGINT version
        TIMESTAMPTZ created_at
    }

    EXERCISE {
        UUID id PK
        UUID user_profile_id FK
        UUID equipment_id FK
        VARCHAR name
        VARCHAR description
        VARCHAR media_url
        BIGINT version
        TIMESTAMPTZ created_at
    }

    MUSCLE {
        UUID id PK
        VARCHAR name UK
        VARCHAR description
        VARCHAR media_url
        BIGINT version
        TIMESTAMPTZ created_at
    }

    EXERCISE_MUSCLE {
        UUID id PK
        UUID user_profile_id FK
        UUID exercise_id FK
        UUID muscle_id FK
        activation_level activation_level
        BIGINT version
    }

    SAVED_EXERCISE {
        UUID id PK
        UUID user_profile_id FK
        UUID exercise_id FK
        BIGINT version
    }

    WORKOUT_PROGRAM {
        UUID id PK
        UUID user_profile_id FK
        BOOLEAN is_edited
        BOOLEAN is_active
        VARCHAR name
        VARCHAR description
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    WORKOUT_DAY {
        UUID id PK
        UUID user_profile_id FK
        UUID workout_program_id FK
        BOOLEAN is_off
        INTEGER order_number
        VARCHAR name
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    TARGET_SET {
        UUID id PK
        UUID user_profile_id FK
        UUID exercise_id FK
        UUID workout_day_id FK
        DECIMAL weight_kg
        INTEGER duration
        INTEGER rest_duration
        INTEGER rep_count
        INTEGER order_number
        unit_system unit
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    WORKOUT_PERIOD {
        UUID id PK
        UUID user_profile_id FK
        UUID workout_program_id FK
        BOOLEAN is_active
        TIMESTAMPTZ start_date
        TIMESTAMPTZ end_date
        VARCHAR workout_program_name_snapshot
        BIGINT version
        TIMESTAMPTZ created_at
    }

    WORKOUT_SESSION {
        UUID id PK
        UUID user_profile_id FK
        UUID workout_period_id FK
        UUID workout_day_id FK
        DATE start_date
        TIMESTAMPTZ start_time
        TIMESTAMPTZ end_time
        workout_status status
        session_type type
        VARCHAR workout_day_name_snapshot
        INTEGER exercise_count_snapshot
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    SET_RESULT {
        UUID id PK
        UUID user_profile_id FK
        UUID workout_session_id FK
        UUID exercise_id FK
        INTEGER order_number
        INTEGER duration
        INTEGER rest_duration
        INTEGER rep_count
        DECIMAL weight_kg
        unit_system unit
        VARCHAR exercise_name_snapshot
        INTEGER targeted_rep_count
        DECIMAL targeted_weight
        INTEGER targeted_duration
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    USER_PROFILE ||--o{ BODY_INFORMATION : has
    USER_PROFILE ||--o{ MEASUREMENT_HISTORY : tracks
    USER_PROFILE ||--o{ EQUIPMENT : owns
    USER_PROFILE ||--o{ EXERCISE : owns
    USER_PROFILE ||--o{ EXERCISE_MUSCLE : scopes
    USER_PROFILE ||--o{ SAVED_EXERCISE : saves
    USER_PROFILE ||--o{ WORKOUT_PROGRAM : owns
    USER_PROFILE ||--o{ WORKOUT_DAY : owns
    USER_PROFILE ||--o{ TARGET_SET : owns
    USER_PROFILE ||--o{ WORKOUT_PERIOD : follows
    USER_PROFILE ||--o{ WORKOUT_SESSION : performs
    USER_PROFILE ||--o{ SET_RESULT : records

    EQUIPMENT ||--o{ EXERCISE : supports
    EXERCISE ||--o{ EXERCISE_MUSCLE : targets
    MUSCLE ||--o{ EXERCISE_MUSCLE : activated_by
    EXERCISE ||--o{ SAVED_EXERCISE : saved_as
    WORKOUT_PROGRAM ||--o{ WORKOUT_DAY : contains
    WORKOUT_DAY ||--o{ TARGET_SET : contains
    EXERCISE ||--o{ TARGET_SET : prescribed_as
    WORKOUT_PROGRAM ||--o{ WORKOUT_PERIOD : becomes
    WORKOUT_PERIOD ||--o{ WORKOUT_SESSION : schedules
    WORKOUT_DAY ||--o{ WORKOUT_SESSION : planned_from
    WORKOUT_SESSION ||--o{ SET_RESULT : produces
    EXERCISE ||--o{ SET_RESULT : performed_as
```

### Sync and Delete Tracking

The core schema also includes a version-based synchronization mechanism. Inserts and updates receive a global version number, while deletes are recorded separately in `deleted_history` so clients can reconcile removed records.

```mermaid
erDiagram
    GLOBAL_VERSION_SEQ {
        BIGINT next_value
        INTEGER cache_size
    }

    SYNCABLE_DOMAIN_TABLE {
        UUID id PK
        UUID user_profile_id FK
        BIGINT version
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }

    DELETED_HISTORY {
        BIGINT version PK
        UUID user_profile_id
        UUID record_id
        VARCHAR table_name
        TIMESTAMPTZ created_at
    }

    SYNC_INDEX {
        UUID user_profile_id
        BIGINT version
    }

    CLEANUP_FUNCTION {
        INTERVAL retention_window
        INTEGER deleted_count
    }

    CLIENT_SYNC_CURSOR {
        UUID user_profile_id
        BIGINT last_known_version
    }

    GLOBAL_VERSION_SEQ ||--o{ SYNCABLE_DOMAIN_TABLE : assigns_on_insert_update
    GLOBAL_VERSION_SEQ ||--o{ DELETED_HISTORY : assigns_on_delete
    SYNCABLE_DOMAIN_TABLE ||--o{ SYNC_INDEX : indexed_by
    SYNCABLE_DOMAIN_TABLE ||--o{ DELETED_HISTORY : delete_trigger_logs
    DELETED_HISTORY ||--o{ CLEANUP_FUNCTION : cleaned_by
    CLIENT_SYNC_CURSOR ||--o{ SYNC_INDEX : reads_changed_rows
    CLIENT_SYNC_CURSOR ||--o{ DELETED_HISTORY : reads_deleted_rows
```

Core migration rules worth highlighting:

- System catalog records are represented with `user_profile_id IS NULL`; user-specific records are scoped with `user_profile_id`.
- Every syncable domain table receives a monotonically increasing `version` from `global_version_seq`.
- Delete operations are captured in `deleted_history`, which lets clients sync removals as well as inserts and updates.
- `target_set` and `set_result` enforce exactly one effort type: either `rep_count` or `duration`.
- A user can have only one active workout program, while system workout programs are prevented from being active.
- Equipment uniqueness is split between global system equipment names and per-user equipment names.
- Workout sessions and set results keep snapshot fields so historical workout records remain readable even if catalog data changes later.

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 25 |
| Framework | Spring Boot 4 |
| API Gateway | Spring Cloud Gateway WebFlux |
| Core API | Spring Web MVC |
| Build / Monorepo | Maven multi-module reactor |
| Relational Database | PostgreSQL 18 |
| Document Database | MongoDB 7 |
| Cache / Token State | Redis 7 |
| Database Migrations | Flyway |
| SQL Access | jOOQ |
| API Documentation | Springdoc OpenAPI / Swagger UI |
| Infrastructure | Docker and Docker Compose |
| CI | GitHub Actions |

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

## Ownership and Rights

Copyright (c) 2026 Ahmet Baha Aktürk. All rights reserved.

This repository is published as a portfolio and graduation project showcase. Unless a separate license is provided, the source code, architecture, documentation, and related assets may not be copied, redistributed, or used commercially without permission from Ahmet Baha Aktürk.

## Status

The local stack is runnable with Docker Compose and includes the gateway, identity service, core service, PostgreSQL, MongoDB, and Redis.
