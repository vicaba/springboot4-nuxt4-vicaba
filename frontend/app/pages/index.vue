<script setup lang="ts">
import type { paths, operations } from '~/types/api'
import { fetchApi } from '~/utils/api'

type HelloPath = keyof Pick<paths, '/api/hello'>
type HelloResponse = operations['getHello']['responses'][200]['content']['application/json']

const helloPath: HelloPath = '/api/hello'

const message = ref<string | null>(null)
const pending = ref(true)
const error = ref<string | null>(null)

const fetchMessage = async () => {
  pending.value = true
  error.value = null
  try {
    const data = await fetchApi(helloPath, {
      timeout: 3000,
    })
    message.value = data.message
  } catch (err: any) {
    error.value = err?.message || 'Failed to fetch message from backend'
  } finally {
    pending.value = false
  }
}

onMounted(() => {
  fetchMessage()
})
</script>

<template>
  <div class="home-container">
    <div class="hero-card">
      <div class="badge">Spring Boot 4 + Nuxt 4</div>
      <h1 class="hero-title">Demo Broker Portfolio Tracker</h1>
      <p class="hero-subtitle">
        Manage and analyze multi-currency stock transactions with contract-first OpenAPI integration.
      </p>

      <div class="api-status">
        <span class="status-label">Backend API Status:</span>
        <span v-if="pending" class="status-value pending">Checking system connection...</span>
        <span v-else-if="error" class="status-value error">Offline ({{ error }})</span>
        <span v-else class="status-value success">Online ({{ message }})</span>
      </div>

      <div class="hero-actions">
        <NuxtLink to="/transactions" class="btn btn-primary">
          View Transactions List →
        </NuxtLink>
        <button @click="fetchMessage" class="btn btn-secondary">
          Refresh API Status
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 3rem 1rem;
}

.hero-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 3rem 2.5rem;
  max-width: 680px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  text-align: center;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.badge {
  display: inline-block;
  padding: 0.35rem 0.9rem;
  border-radius: 50px;
  background: #eef2ff;
  color: #4f46e5;
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 1rem;
  letter-spacing: 0.5px;
}

.hero-title {
  font-size: 2.25rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 1rem 0;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.1rem;
  color: #475569;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.api-status {
  background: #f8fafc;
  border-radius: 10px;
  padding: 1rem 1.5rem;
  margin-bottom: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-size: 0.95rem;
  border: 1px dashed #cbd5e1;
}

.status-label {
  font-weight: 600;
  color: #334155;
}

.status-value.pending {
  color: #d97706;
}

.status-value.error {
  color: #dc2626;
  font-weight: 600;
}

.status-value.success {
  color: #16a34a;
  font-weight: 700;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background: #4f46e5;
  color: white;
}

.btn-primary:hover {
  background: #4338ca;
  transform: translateY(-1px);
}

.btn-secondary {
  background: #f1f5f9;
  color: #334155;
}

.btn-secondary:hover {
  background: #e2e8f0;
}
</style>
