# CSS Subsystem Guidelines (AGENTS.md)

## 1. Project Context & Tech Stack

- **Core Purpose**: Modular Vanilla CSS styling architecture for the Nuxt 4 frontend, modeled after Tailwind UI (version 4.*) design tokens and component patterns without external CSS dependencies.
- **Tech Stack**: Vanilla CSS (CSS Variables, Flexbox, Grid), Nuxt 4, Vite, Prettier via Gradle Spotless.
- **Directory Structure & Modules**:
  - [`main.css`](main.css): Root aggregator importing all foundation and component stylesheets.
  - [`tokens.css`](tokens.css): Global `:root` CSS variables (color palette, radii, shadows).
  - [`base.css`](base.css): Universal box-sizing reset and base body typography/background.
  - [`typography.css`](typography.css): Page titles, section headings, and lead descriptions.
  - [`layout.css`](layout.css): App shell, navigation header, main container, and footer bar.
  - [`utilities.css`](utilities.css): Spacing helper utilities (`.mt-*`, `.mb-*`, `.py-*`, `.px-*`) and font modifiers (`.font-mono`).
  - [`components/`](components/): Domain-specific component style sheets:
    - [`components/card.css`](components/card.css): Card containers, headers, titles, hero and control panels.
    - [`components/button.css`](components/button.css): Button variants (`primary`, `secondary`, `outline`, `danger`, `page`, `sm`).
    - [`components/badge.css`](components/badge.css): Semantic status badges and pill tags.
    - [`components/table.css`](components/table.css): Data tables, table cards, header bars, and pagination controls.
    - [`components/form.css`](components/form.css): Search inputs, text fields, and filter button groups.
    - [`components/stat.css`](components/stat.css): Metric grids, stat summary cards, and value coloring.
    - [`components/dropzone.css`](components/dropzone.css): File uploader drag-and-drop zones with hover/active states.
    - [`components/alert.css`](components/alert.css): Success and error banner messages.
    - [`components/feedback.css`](components/feedback.css): Loading spinners, empty states, and system status indicators.

## 2. Guardrails & Constraints

- **Forbidden Actions & Anti-Patterns**:
  - NEVER add Tailwind CSS, PostCSS plugins, or third-party CSS framework dependencies.
  - NEVER add `<style scoped>` or `<style>` blocks in `.vue` files; all styles must reside in this directory.
  - NEVER hardcode hex colors or arbitrary dimensions in component files; reference `--color-*`, `--radius-*`, and `--shadow-*` tokens from [`tokens.css`](tokens.css).
  - NEVER create standalone CSS files without registering them in [`main.css`](main.css).
- **Human Input Scenarios**: Request confirmation before renaming existing global class names or modifying token palette definitions in [`tokens.css`](tokens.css).

## 3. Build, Test, and Lint Commands

- Refer to root AGENTS.md

## 4. Coding Standards & Patterns

- **Naming Conventions**:
  - Use kebab-case for class names (e.g., `.table-header-bar`, `.btn-primary`).
  - Component files must use singular nouns (e.g., `card.css`, `button.css`, `table.css`).
- **Component File Template**:
  - Group component styles into dedicated files under `components/`.
  - Maintain consistent padding, border, and border-radius using standard CSS variables (`var(--radius-xl)`, `var(--color-slate-200)`).
- **Exemplary Implementations**:
  - Table architecture: [`components/table.css`](components/table.css)
  - Card layouts: [`components/card.css`](components/card.css)
  - Button states: [`components/button.css`](components/button.css)
