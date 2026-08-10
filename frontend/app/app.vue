<script setup lang="ts">
// Main layout container with top navigation
</script>

<template>
  <div class="app-layout">
    <header class="header-bar">
      <div class="header-container">
        <div class="brand">
          <NuxtLink to="/" class="brand-link">
            <span>📈</span>
            <span>Demo Broker</span>
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

    <footer class="footer-bar">
      <div class="container" style="padding-top: 0; padding-bottom: 0;">
        <p>© 2026 Demo Broker • Spring Boot 4 & Nuxt 4 SSG Stack</p>
      </div>
    </footer>
  </div>
</template>

