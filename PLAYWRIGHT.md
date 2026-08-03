# Playwright End-to-End Testing & Verification

This document provides instructions for running end-to-end (E2E) browser automation tests against the integrated Spring Boot backend and Nuxt 4 SSG frontend stack.

---

## 1. System Prerequisites (Pre-Install to Avoid Re-Downloads)

To run Playwright tests instantly without downloading browser binaries every time, you can use either **npx pre-download** or your **system/Homebrew Chromium/Chrome**.

### Option A: Use System Chromium / Chrome (Homebrew or Installed Chrome)

If you install Chromium via Homebrew:

```bash
brew install --cask chromium
```

You can instruct Playwright to skip browser downloads and use your installed binary by setting environment variables in your shell (`~/.zshrc`):

```bash
export PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1
export PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH="/Applications/Chromium.app/Contents/MacOS/Chromium"
# Or for system Google Chrome:
# export PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
```

Alternatively, specify `executablePath` or `channel: 'chrome'` directly in your script:

```javascript
const browser = await chromium.launch({
  channel: "chrome", // Uses system installed Google Chrome
  headless: true,
});
```

### Option B: Use Playwright Bundled Chromium

Download and cache the headless Chromium browser binary once globally:

```bash
cd frontend
npm install
npx playwright install chromium
```

---

## 2. Running Full-Stack E2E Verification

### Step 1: Start Backend Service

In a terminal window, start the Spring Boot application on port `18080`:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Step 2: Start Frontend Service

In a separate terminal window, start the Nuxt dev server on port `3000`:

```bash
cd frontend
npm run dev
```

### Step 3: Execute Playwright Verification Script

Run the automated Playwright script using Node.js:

```bash
node -e "
import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({
    headless: true,
    // Optional: executablePath: '/Applications/Chromium.app/Contents/MacOS/Chromium'
  });
  const page = await browser.newPage();

  // Test Home Page
  await page.goto('http://localhost:3000/', { waitUntil: 'networkidle' });
  console.log('Home Page Title:', await page.textContent('h1'));
  await page.screenshot({ path: 'home.png', fullPage: true });

  // Test Transactions Page
  await page.goto('http://localhost:3000/transactions', { waitUntil: 'networkidle' });
  await page.waitForSelector('.data-table');
  console.log('Rendered Page Content:\n', await page.innerText('body'));
  await page.screenshot({ path: 'transactions.png', fullPage: true });

  await browser.close();
})();
"
```

---

## 3. General E2E Testing Guidelines

- **Headless Mode**: Always run Chromium with `{ headless: true }`.
- **Network Idle**: Use `{ waitUntil: 'networkidle' }` to ensure backend API fetch requests settle before evaluating DOM content.
- **Selector Waiting**: Use `await page.waitForSelector('<element-selector>')` to wait for dynamic Vue components to render data fetched from backend REST endpoints.
