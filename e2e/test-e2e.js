import { chromium } from "playwright";
import fs from "fs";

// Determine browser executable path:
// 1. Explicit environment variable if set
// 2. Google Chrome (installed on macOS and passes Gatekeeper)
// 3. Fallback to default Playwright Chromium
function getExecutablePath() {
  if (process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH) {
    return process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH;
  }
  if (
    fs.existsSync(
      "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
    )
  ) {
    return "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
  }
  if (fs.existsSync("/Applications/Chromium.app/Contents/MacOS/Chromium")) {
    return "/Applications/Chromium.app/Contents/MacOS/Chromium";
  }
  return undefined;
}

async function run() {
  const executablePath = getExecutablePath();
  console.log(
    "Launching browser with path:",
    executablePath || "Playwright default",
  );
  const browser = await chromium.launch({
    executablePath,
    headless: true,
  });

  const page = await browser.newPage();

  console.log("Navigating to http://localhost:3000/ ...");
  await page.goto("http://localhost:3000/", { waitUntil: "networkidle" });
  const homeTitle = await page.textContent("h1");
  console.log("Home Title:", homeTitle);

  console.log("Navigating to http://localhost:3000/transactions ...");
  await page.goto("http://localhost:3000/transactions", {
    waitUntil: "networkidle",
  });
  await page.waitForSelector(".data-table", { timeout: 10000 });
  console.log("--- Transactions Page Content ---");
  console.log(await page.innerText("body"));

  await browser.close();
  console.log("E2E Playwright test finished successfully!");
}

run().catch((err) => {
  console.error("E2E Playwright test error:", err);
  process.exit(1);
});
