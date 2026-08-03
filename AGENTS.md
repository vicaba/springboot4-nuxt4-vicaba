# AGENTS.md

## Overview

Spring Boot 4 (Web MVC, Kotlin, Java 25) backend serving a Nuxt 4 SSG frontend, packaged together into a single Docker image with Gradle managing the full build pipeline.

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
│   │   ├── config/          # Spring beans: CORS (WebMvcConfig), SPA fallback controller, app properties
│   │   ├── handler/         # MVC handlers/controllers (HelloController, IndexHandler)
│   │   ├── logger/          # Logger extension (Kotlin extension fun logger())
│   │   └── DemoApplication.kt
│   ├── main/resources/
│   │   ├── application.properties       # Server port, API base path, CORS, index file
│   │   └── application-dev.properties   # Dev overrides (e.g. debug, devtools)
│   ├── test/kotlin/         # Unit tests using Kotest + MockK
│   └── it/kotlin/           # Integration tests using Kotest + Spring test context + MockMvc
├── frontend/
│   ├── app/                 # Nuxt source: pages/, components/, layouts/, app.vue
│   ├── public/              # Static assets served at root
│   ├── nuxt.config.ts       # Nuxt configuration (SSG mode implied by `generate`)
│   └── .output/public/      # Generated static output (git-ignored, copied into jar)
├── build.gradle.kts         # Full build pipeline including frontend tasks
├── compose.yaml             # Docker Compose (single `app` service, port 18080)
└── Dockerfile               # Multi-stage: node frontend → JDK backend → distroless runtime
```

- **New API endpoints**: add a `@RestController` in `handler/`, annotate with `@RequestMapping("${application.api-base-path}")`.
- **New config properties**: extend `ApplicationProperties` in `config/ApplicationConfig.kt` and add keys to `application.properties`.
- **SPA fallback & static files**: handled by `SpaFallbackConfig` (`@Controller @GetMapping("/**")`). It serves index.html for SPA routes and static files from `classpath:/static/` for file-extension paths.
- **Frontend pages/components**: work only inside `frontend/app/`.

---

## Code Style & Rules

- **Kotlin style**: enforced by `ktlint` via `spotlessApply`. Run before committing.
- **Controller pattern**: API controllers are `@RestController` classes in `handler/`. Non-API/SPA fallback lives in `config/SpaFallbackConfig.kt`. No functional router DSL.
- **No blocking restriction**: this is a standard Spring MVC (servlet/Tomcat) app; regular synchronous blocking I/O is acceptable and expected.
- **Logging**: use the project's `logger` extension (`com.example.demo.logger.logger`) instead of importing a logger directly.
- **Properties binding**: use `@ConfigurationProperties` data classes, not `@Value`. Prefix is `application`.
- **API base path**: controllers should use `@RequestMapping("\${application.api-base-path}")` instead of hard-coding `/api`.
- **Test framework**: Kotest (not JUnit assertions). Unit tests in `src/test/kotlin`, integration tests in `src/it/kotlin` (use `MockMvc` via `@AutoConfigureMockMvc`).
- **Frontend static output**: Nuxt generates to `frontend/.output/public`. Gradle's `copyFrontend` task copies this into `build/resources/main/static` — do not manually place files there.
- **Prettier** formats `.json`, `.js`, `.md`, `.yml`, `.yaml`, `Dockerfile`, and `.sh` files. Run `./gradlew spotlessApply` to auto-fix.

---

## Guardrails

- **Never commit `frontend/.output/` or `src/main/resources/static/`**. Both are build artifacts and are git-ignored.
- **Never bypass `copyFrontend` task**. The Gradle task chain (`frontendInstall → frontendGenerate → copyFrontend → processResources`) must remain intact for the jar to include frontend assets.
- **Never modify `build/` or `.gradle/` directories**. These are Gradle's cache and output directories.
- **Do not add `@Controller` or `@RequestMapping`** for new API routes outside `handler/`. All API routing goes through `@RestController` classes with `@RequestMapping("${application.api-base-path}")`.
- **API routes must be prefixed with `/api`** (configured via `application.api-base-path`). Routes outside this prefix are caught by `SpaFallbackConfig` and served as HTML or static files.
- **Do not add WebFlux dependencies**. This project uses Spring Web MVC (Tomcat). Adding `spring-boot-starter-webflux` or reactor/coroutine libs will conflict.
