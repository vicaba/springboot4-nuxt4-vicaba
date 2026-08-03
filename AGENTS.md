# AGENTS.md

## Overview

Spring Boot 4 (WebFlux, Kotlin, Java 25) backend serving a Nuxt 4 SSG frontend, packaged together into a single Docker image with Gradle managing the full build pipeline.

---

## Commands

### Backend

```bash
# Run all unit tests
./gradlew test

# Run integration tests (also runs unit tests first)
./gradlew integrationTest

# Run all checks (unit + integration tests)
./gradlew check

# Lint & format (Kotlin via ktlint, JSON/YAML/MD via Prettier)
./gradlew spotlessCheck
./gradlew spotlessApply

# Build the full app (frontend generate → copy → backend jar)
./gradlew assemble

# Run locally (dev profile, frontend must be built or served separately)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Frontend

```bash
cd frontend

# Install dependencies
npm ci

# Dev server (hot reload, port 3000)
npm run dev

# Generate static output to frontend/.output/public
npm run generate

# Check for major dependency updates
npm run deps:check

# Apply major dependency updates
npm run deps:update
```

### Docker

```bash
# Build and run via Docker Compose (full stack, port 18080)
docker compose up --build

# Build image only
docker build -t vicaba-app .
```

---

## Structure

```
.
├── src/
│   ├── main/kotlin/com/example/demo/
│   │   ├── config/          # Spring beans: routing, CORS, SPA fallback, app properties
│   │   ├── handler/         # WebFlux request handlers (one handler per resource)
│   │   ├── logger/          # Logger extension (Kotlin extension fun logger())
│   │   └── DemoApplication.kt
│   ├── main/resources/
│   │   ├── application.properties       # Server port, API base path, CORS, index file
│   │   └── application-dev.properties   # Dev overrides (e.g. CORS allowed origins)
│   ├── test/kotlin/         # Unit tests using Kotest + MockK
│   └── it/kotlin/           # Integration tests using Kotest + Spring test context
├── frontend/
│   ├── app/                 # Nuxt source: pages/, components/, layouts/, app.vue
│   ├── public/              # Static assets served at root
│   ├── nuxt.config.ts       # Nuxt configuration (SSG mode implied by `generate`)
│   └── .output/public/      # Generated static output (git-ignored, copied into jar)
├── build.gradle.kts         # Full build pipeline including frontend tasks
├── compose.yaml             # Docker Compose (single `app` service, port 18080)
└── Dockerfile               # Multi-stage: node frontend → JDK backend → distroless runtime
```

- **New API endpoints**: add a handler in `handler/`, register the route in `config/RoutingConfig.kt`.
- **New config properties**: extend `ApplicationProperties` in `config/ApplicationConfig.kt` and add keys to `application.properties`.
- **Frontend pages/components**: work only inside `frontend/app/`.

---

## Code Style & Rules

- **Kotlin style**: enforced by `ktlint` via `spotlessApply`. Run before committing.
- **Async style**: all WebFlux handler methods must be `suspend` and use coroutine-aware APIs (`coRouter`, `bodyValueAndAwait`, etc.). Do not use blocking calls.
- **Handler pattern**: handlers are `@Component` classes with `suspend fun (ServerRequest): ServerResponse`. No `@RestController` or MVC annotations.
- **Logging**: use the project's `logger` extension (`com.example.demo.logger.logger`) instead of importing a logger directly.
- **Properties binding**: use `@ConfigurationProperties` data classes, not `@Value`. Prefix is `application`.
- **Test framework**: Kotest (not JUnit assertions). Unit tests go in `src/test/kotlin`, integration tests in `src/it/kotlin`.
- **Frontend static output**: Nuxt generates to `frontend/.output/public`. Gradle's `copyFrontend` task copies this into `build/resources/main/static` — do not manually place files there.
- **Prettier** formats `.json`, `.js`, `.md`, `.yml`, `.yaml`, `Dockerfile`, and `.sh` files. Run `./gradlew spotlessApply` to auto-fix.

---

## Guardrails

- **Never add `spring.web.resources.add-mappings=true`**. Static resource mapping is intentionally disabled; the SPA fallback is handled by `SpaFallbackConfig` to avoid conflicts.
- **Never commit `frontend/.output/` or `src/main/resources/static/`**. Both are build artifacts and are git-ignored.
- **Never use blocking I/O** (JDBC, `Thread.sleep`, `RestTemplate`) in any handler or service. This is a WebFlux/Reactor application; blocking calls will starve the event loop.
- **Never bypass `copyFrontend` task**. The Gradle task chain (`frontendInstall → frontendGenerate → copyFrontend → processResources`) must remain intact for the jar to include frontend assets.
- **Never modify `build/` or `.gradle/` directories**. These are Gradle's cache and output directories.
- **Do not add `@Controller` or `@RequestMapping`**. All routing goes through the functional `coRouter` DSL in `config/RoutingConfig.kt`.
- **API routes must be prefixed with `/api`** (configured via `application.api-base-path`). Routes outside this path will be caught by the SPA fallback and served as HTML.
