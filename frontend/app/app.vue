<script setup lang="ts">
// Main layout container with top navigation
</script>

<template>
  <div class="app-layout">
    <header class="navbar">
      <div class="nav-container">
        <div class="brand">
          <NuxtLink to="/" class="brand-link">
            <span class="brand-icon">📈</span>
            <span class="brand-text">Demo Broker</span>
          </NuxtLink>
        </div>
        <nav class="nav-links">
          <NuxtLink to="/" class="nav-item" exact-active-class="active">
            Home
          </NuxtLink>
          <NuxtLink to="/transactions" class="nav-item" exact-active-class="active">
            Transactions
          </NuxtLink>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <NuxtPage />
    </main>

    <footer class="footer">
      <div class="footer-container">
        <p>© 2026 Demo Broker • Spring Boot 4 & Nuxt 4 SSG Stack</p>
      </div>
    </footer>
  </div>
</template>

<style>
/* Global resets */
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background-color: #f8fafc;
  color: #0f172a;
}

.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: white;
  border-bottom: 1px solid #e2e8f0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.85rem 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  font-weight: 800;
  font-size: 1.25rem;
  color: #0f172a;
}

.brand-icon {
  font-size: 1.4rem;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
}

.nav-item {
  text-decoration: none;
  color: #475569;
  font-weight: 600;
  font-size: 0.95rem;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: #4f46e5;
  background: #f1f5f9;
}

.nav-item.active {
  color: #4f46e5;
  background: #eef2ff;
}

.main-content {
  flex: 1;
}

.footer {
  background: white;
  border-top: 1px solid #e2e8f0;
  padding: 1.5rem 1rem;
  text-align: center;
  color: #94a3b8;
  font-size: 0.85rem;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
