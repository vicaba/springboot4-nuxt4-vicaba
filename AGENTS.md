# AGENTS.md

## Project Overview
Spring Boot 4 (Kotlin, Servlet/Tomcat, Java 25) backend serving a Nuxt 4 SSG frontend, managed by a Gradle unified build pipeline with a single-source-of-truth OpenAPI 3 specification (`openapi/api.yaml`).

## Commands

### Backend
- `./gradlew openApiGenerate`: Generate Kotlin models (`com.example.demo.model.*`) from `openapi/api.yaml`.
- `./gradlew test`: Run backend unit tests (Kotest).
- `./gradlew integrationTest`: Run Spring Boot integration tests (Kotest + MockMvc).
- `./gradlew check`: Run full check pipeline (unit + integration tests).
- `./gradlew spotlessApply`: Auto-format Kotlin (`ktlint`) and config/doc files (`prettier`).
- `./gradlew bootRun --args='--spring.profiles.active=dev'`: Run backend locally on port `18080`.
- `./gradlew assemble`: Build full app JAR (generates OpenAPI, builds frontend, packages into static resources).

### Frontend
- `cd frontend && npm ci`: Install frontend dependencies.
- `cd frontend && npm run generate:types`: Generate TypeScript types (`app/types/api.d.ts`) from `openapi/api.yaml`.
- `cd frontend && npm run dev`: Run Nuxt dev server on port `3000` (proxies `/api` to `http://localhost:18080`).
- `cd frontend && npm run generate`: Generate static site output to `frontend/.output/public`.
- `cd frontend && npm run test:e2e`: Run E2E Playwright tests using headless Chromium or system Google Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome`).

### Docker
- `docker compose up --build`: Build and run full stack on port `18080`.

## Coding Standards
- **OpenAPI Contract-First**: All API endpoints and data models MUST be defined in `openapi/api.yaml`. Generate DTOs (`./gradlew openApiGenerate` and `npm run generate:types`) before implementing features.
- **Backend Architecture**: All API `@RestController` classes must live in `com.example.demo.handler` and use `@RequestMapping("${application.api-base-path}")`.
- **Domain Code**: Multi-currency and investment domain abstractions use `com.vicaba.demobroker` package namespace (`com.vicaba.demobroker.tracker.currency.domain`, `com.vicaba.demobroker.tracker.transaction.domain`).
- **Frontend Code**: Nuxt 4 source code must live exclusively in `frontend/app/` (pages, components, layouts, types). Use generated OpenAPI types from `~/types/api`.
- **Testing**: Use Kotest (not JUnit assertions) for Kotlin unit/integration tests. For frontend E2E browser tests, launch headless Chromium or system Google Chrome directly (do NOT attach to existing CDP sessions).
- **Logging**: Use project extension `com.example.demo.logger.logger` instead of direct SLF4J logger imports.
- **Properties**: Bind properties using `@ConfigurationProperties` data classes with `application` prefix.

## Gotchas / Pitfalls
- **Build Artifact Exclusions**: NEVER manually commit or edit `frontend/.output/` or `src/main/resources/static/`. They are generated build artifacts.
- **Gradle Task Chain**: NEVER bypass `copyFrontend`. `frontendInstall -> frontendGenerate -> copyFrontend -> processResources` must remain intact for the JAR build.
- **API Route Base Path**: All API endpoints MUST start with `/api` (configured via `application.api-base-path`). Non-API routes are intercepted by `SpaFallbackConfig` and served as SPA/static HTML.
- **Mac Gatekeeper & Playwright**: When running Playwright E2E browser tests, use system Google Chrome (`/Applications/Google Chrome.app/Contents/MacOS/Google Chrome`) or Playwright bundled Chromium rather than unsigned Homebrew binaries that fail macOS Gatekeeper.
