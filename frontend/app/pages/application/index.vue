<script setup lang="ts">
import type { paths, operations } from '~/types/api'
import { fetchApi } from '~/utils/api'

definePageMeta({
  path: '/',
  alias: '/application',
})

type HealthPath = keyof Pick<paths, '/api/status/health'>
type HealthResponse = operations['getHealth']['responses'][200]['content']['application/json']

const healthPath: HealthPath = '/api/status/health'

const status = ref<string | null>(null)
const pending = ref(true)
const error = ref<string | null>(null)

const fetchHealth = async () => {
  pending.value = true
  error.value = null
  try {
    const data = await fetchApi(healthPath, {
      timeout: 3000,
    })
    status.value = data.status
  } catch (err: any) {
    error.value = err?.message || 'Failed to fetch status from backend'
  } finally {
    pending.value = false
  }
}

onMounted(() => {
  fetchHealth()
})
</script>

<template>
  <div class="home-container">
    <div class="hero-card">
      <div class="badge badge-indigo" style="margin-bottom: 1rem;">Spring Boot 4 + Nuxt 4</div>
      <h1 class="hero-title">Demo Broker Portfolio Tracker</h1>
      <p class="hero-subtitle">
        Manage and analyze multi-currency stock transactions with contract-first OpenAPI integration.
      </p>

      <div class="api-status">
        <span class="status-label">Backend API Status:</span>
        <span v-if="pending" class="status-value pending">Checking system connection...</span>
        <span v-else-if="error" class="status-value error">Offline ({{ error }})</span>
        <span v-else class="status-value success">Online ({{ status }})</span>
      </div>

      <div class="hero-actions">
        <NuxtLink to="/transactions" class="btn btn-primary">
          View Transactions List →
        </NuxtLink>
        <button @click="fetchHealth" class="btn btn-secondary">
          Refresh API Status
        </button>
      </div>
    </div>
  </div>
</template>

