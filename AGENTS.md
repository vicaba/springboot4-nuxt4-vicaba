# AGENTS.md

## 1. Project Context & Tech Stack
- **Core Purpose**: Demo broker tracking system featuring a Spring Boot 4 backend serving a Nuxt 4 SSG frontend, managed via a Gradle unified build pipeline.
- **Tech Stack**:
  - **Backend**: Kotlin, Java 25, Spring Boot 4 (Servlet/Tomcat), JUnit 5.
  - **Frontend**: Nuxt 4 (Vue 3, TypeScript), Playwright (E2E testing).
  - **Specification**: OpenAPI 3 (`openapi/api.yaml`) as the single source of truth.
- **Directory Structure & Core Modules**:
  - `openapi/api.yaml`: OpenAPI contract.
  - `src/main/kotlin/com/vicaba/demobroker/tracker/`: Kotlin backend vertical slices (`application/`, `transaction/`, `currency/`).
  - `frontend/app/`: Nuxt 4 frontend source (`pages/`, `components/`, `layouts/`, `types/`, `utils/`).
  - `frontend/.output/` & `src/main/resources/static/`: Generated build output directories.

## 2. Guardrails & Constraints
- **Protected Files**: NEVER edit or commit generated artifacts in `frontend/.output/` or `src/main/resources/static/`.
- **Forbidden Actions & Anti-Patterns**:
  - NEVER bypass the Gradle frontend task chain (`frontendInstall -> frontendGenerate -> copyFrontend -> processResources`).
  - NEVER import SLF4J loggers directly.
  - NEVER compute list sizes or state in frontend when backend can provide computed properties.
  - NEVER attach Playwright E2E tests to existing CDP sessions or use unsigned Homebrew binaries (causes macOS Gatekeeper failures).
- **Human Input Scenarios**: Stop and request human confirmation before executing breaking OpenAPI changes, modifying task chains, or running destructive repository commands.

## 3. Build, Test, and Lint Commands
- **Environment & Configuration**:
  - Active profile: `SPRING_PROFILES_ACTIVE=dev` (default).
  - API base path: `application.api-base-path=/api` (default).
  - Chrome binary: `/Applications/Google Chrome.app/Contents/MacOS/Google Chrome`.
- **Backend Commands**:
  - Model Generation: `./gradlew openApiGenerate` (generates `com.vicaba.demobroker.tracker.contract.model.*`).
  - Unit Tests: `./gradlew test` (JUnit 5).
  - Integration Tests: `./gradlew integrationTest` (JUnit 5 + MockMvc).
  - Check Pipeline: `./gradlew check` (runs unit and integration tests).
  - Code Formatting: `./gradlew spotlessApply` (ktlint and prettier).
  - Local Server: `./gradlew bootRun --args='--spring.profiles.active=dev'` (runs on port `18080`).
  - Full App Build: `./gradlew assemble` (generates OpenAPI, builds frontend, packages runnable JAR).
- **Frontend Commands**:
  - Dependency Install: `cd frontend && npm ci`.
  - Type Generation: `cd frontend && npm run generate:types` (generates `app/types/api.d.ts`).
  - Dev Server: `cd frontend && npm run dev` (runs on port `3000`, proxies `/api` to `http://localhost:18080`).
  - Static Generation: `cd frontend && npm run generate` (outputs to `frontend/.output/public`).
  - E2E Tests: `cd frontend && npm run test:e2e` (Playwright via Chromium or system Google Chrome).
- **Docker Command**:
  - Full Stack Container: `docker compose up --build` (runs on port `18080`).

## 4. Coding Standards & Patterns
- **OpenAPI Contract-First**:
  - Define all endpoints and models in `openapi/api.yaml` before code changes.
  - Generate models/types (`./gradlew openApiGenerate` and `npm run generate:types`) prior to implementation.
  - Prefer backend-driven computed properties over frontend calculations.
- **Backend Architecture**:
  - `@RestController` classes must implement their generated OpenAPI interface (`com.vicaba.demobroker.tracker.contract.api.*Api`), e.g., `TransactionController.kt`.
  - Use project logger extension `com.vicaba.demobroker.tracker.application.infra.logger.logger`.
  - Bind configuration properties with `@ConfigurationProperties` using `application` prefix (e.g., `ApplicationConfig.kt`).
- **Frontend Standards**:
  - Code must reside exclusively inside `frontend/app/`.
  - Import OpenAPI types (`paths`, `operations`, `components`) from `~/types/api` and use typed `fetchApi` utility.
- **Architecture & Modularity**:
  - Structure code using vertical slicing (e.g., `transaction`, `currency`).
  - Decouple slices, minimize exposed surfaces, and encapsulate internal components.
- **Routing & Base Path**:
  - All API routes MUST start with `/api`. Non-API routes are handled via `SpaFallbackConfig` to serve static/SPA HTML.
- **Testing Standards**:
  - Backend: Use JUnit 5 (JUnit Jupiter) for unit and integration testing.
  - Frontend: Use Playwright launching headless Chromium or system Google Chrome directly. Do not create e2e tests umless stated explicitly.
