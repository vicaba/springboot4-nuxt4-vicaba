<script setup lang="ts">
import type { paths, operations } from '~/types/api'
import { fetchApi } from '~/utils/api'

definePageMeta({
  path: '/transactions',
  alias: '/transaction',
})

type TransactionsPath = keyof Pick<paths, '/api/transactions'>
type TransactionResponse =
  operations['getTransactions']['responses'][200]['content']['application/json'][number]

const transactionsPath: TransactionsPath = '/api/transactions'

const transactions = ref<TransactionResponse[]>([])
const pending = ref(true)
const error = ref<string | null>(null)
const searchQuery = ref('')
const selectedOp = ref<'ALL' | 'BUY' | 'SELL'>('ALL')

const fetchTransactions = async () => {
  pending.value = true
  error.value = null
  try {
    const data = await fetchApi(transactionsPath, {
      timeout: 5000,
    })
    transactions.value = data
  } catch (err: any) {
    error.value = err?.message || 'Failed to fetch transactions from server'
  } finally {
    pending.value = false
  }
}

onMounted(() => {
  fetchTransactions()
})

const filteredTransactions = computed(() => {
  return transactions.value.filter((tx) => {
    const matchesOp = selectedOp.value === 'ALL' || tx.op === selectedOp.value
    const matchesSearch =
      tx.symbol.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      tx.account.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      tx.id.toLowerCase().includes(searchQuery.value.toLowerCase())
    return matchesOp && matchesSearch
  })
})

const totalBuys = computed(() =>
  transactions.value.filter((t) => t.op === 'BUY').length
)
const totalSells = computed(() =>
  transactions.value.filter((t) => t.op === 'SELL').length
)

const formatMoney = (val: number, currency: string) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2,
  }).format(val)
}

const formatDate = (dateStr: string) => {
  try {
    const date = new Date(dateStr)
    return new Intl.DateTimeFormat('en-US', {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(date)
  } catch {
    return dateStr
  }
}
</script>

<template>
  <div class="container">
    <div class="header-section">
      <div>
        <h1 class="page-title">Demo Broker Transactions</h1>
        <p class="page-description">
          Overview of stock transaction history mapped from OpenAPI contracts.
        </p>
      </div>
      <button @click="fetchTransactions" class="btn btn-outline btn-sm">
        <span>↻</span> Refresh Data
      </button>
    </div>

    <!-- Summary Stats -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-title">Total Transactions</div>
        <div class="stat-value">{{ transactions.length }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-title">Buy Orders</div>
        <div class="stat-value buy-color">{{ totalBuys }}</div>
      </div>
      <div class="stat-card">
        <div class="stat-title">Sell Orders</div>
        <div class="stat-value sell-color">{{ totalSells }}</div>
      </div>
    </div>

    <!-- Filters & Controls -->
    <div class="controls-card">
      <div class="search-box">
        <input v-model="searchQuery" type="text" placeholder="Search by Symbol, Account, or ID..."
          class="search-input" />
      </div>
      <div class="filter-group">
        <button @click="selectedOp = 'ALL'" :class="['filter-btn', { active: selectedOp === 'ALL' }]">
          All
        </button>
        <button @click="selectedOp = 'BUY'" :class="['filter-btn', { active: selectedOp === 'BUY' }]">
          Buy Only
        </button>
        <button @click="selectedOp = 'SELL'" :class="['filter-btn', { active: selectedOp === 'SELL' }]">
          Sell Only
        </button>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="pending" class="state-container">
      <div class="spinner"></div>
      <p>Loading transactions from Spring Boot backend...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="state-container state-error">
      <p class="error-msg">Error loading transactions: {{ error }}</p>
      <button @click="fetchTransactions" class="btn btn-danger btn-sm">Retry</button>
    </div>

    <!-- Data Table -->
    <div v-else class="table-card">
      <div v-if="filteredTransactions.length === 0" class="empty-state">
        No transactions match your criteria.
      </div>
      <table v-else class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Account</th>
            <th>Symbol</th>
            <th>Operation</th>
            <th>Quantity</th>
            <th>Price (Converted)</th>
            <th>Fee</th>
            <th>Date</th>
            <th>Status Code</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="tx in filteredTransactions" :key="tx.id">
            <td class="id-cell">{{ tx.id }}</td>
            <td class="account-cell">{{ tx.account }}</td>
            <td class="symbol-cell">
              <span class="stock-badge">{{ tx.symbol }}</span>
            </td>
            <td>
              <span :class="[
                'op-badge',
                tx.op === 'BUY' ? 'op-buy' : 'op-sell',
              ]">
                {{ tx.op }}
              </span>
            </td>
            <td class="number-cell">{{ tx.quantity }}</td>
            <td>
              <div class="price-primary">
                {{ formatMoney(tx.price.value.value, tx.price.value.currency) }}
              </div>
              <div class="price-secondary">
                orig: {{ formatMoney(tx.price.original.value, tx.price.original.currency) }}
              </div>
            </td>
            <td>
              <div class="price-primary">
                {{ formatMoney(tx.fee.value.value, tx.fee.value.currency) }}
              </div>
              <div class="price-secondary">
                orig: {{ formatMoney(tx.fee.original.value, tx.fee.original.currency) }}
              </div>
            </td>
            <td class="date-cell">{{ formatDate(tx.date) }}</td>
            <td>
              <span v-for="c in tx.code" :key="c" :class="['code-badge', c.toLowerCase()]">
                {{ c }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

