<script setup lang="ts">
const message = ref<string | null>(null)
const pending = ref(true)
const error = ref<string | null>(null)

const fetchMessage = async () => {
  pending.value = true
  error.value = null
  try {
    message.value = await $fetch('/api/hello', {
      responseType: 'text',
      timeout: 3000, // 3-second timeout
    })
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
  <div style="font-family: sans-serif; padding: 2rem; text-align: center;">
    <h1>Nuxt 4 + Spring Boot 4</h1>
    <div v-if="pending">Loading message from backend...</div>
    <div v-else-if="error" style="color: #d32f2f; margin-top: 1rem;">
      <p>Error fetching message: {{ error }}</p>
      <button @click="fetchMessage" style="padding: 0.5rem 1rem; cursor: pointer;">Retry</button>
    </div>
    <div v-else style="font-size: 1.5rem; color: #2e7d32; font-weight: bold; margin-top: 1rem;">
      {{ message }}
    </div>
  </div>
</template>
