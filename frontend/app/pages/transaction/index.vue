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
  <div class="page-container">
    <div class="header-section">
      <div>
        <h1 class="page-title">Demo Broker Transactions</h1>
        <p class="page-description">
          Overview of stock transaction history mapped from OpenAPI contracts.
        </p>
      </div>
      <button @click="fetchTransactions" class="refresh-btn">
        <span class="icon">↻</span> Refresh Data
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
    <div v-else-if="error" class="state-container error-state">
      <p class="error-msg">Error loading transactions: {{ error }}</p>
      <button @click="fetchTransactions" class="btn-retry">Retry</button>
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

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-title {
  font-size: 1.85rem;
  font-weight: 800;
  color: #0f172a;
  margin: 0 0 0.4rem 0;
}

.page-description {
  color: #64748b;
  margin: 0;
}

.refresh-btn {
  background: white;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  padding: 0.6rem 1.2rem;
  font-weight: 600;
  color: #334155;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: #f8fafc;
  border-color: #94a3b8;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.25rem;
  margin-bottom: 1.75rem;
}

.stat-card {
  background: white;
  padding: 1.25rem 1.5rem;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
}

.stat-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 800;
  color: #0f172a;
  margin-top: 0.25rem;
}

.buy-color {
  color: #16a34a;
}

.sell-color {
  color: #dc2626;
}

.controls-card {
  background: white;
  border-radius: 12px;
  padding: 1rem 1.25rem;
  border: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
  max-width: 100%;
  padding: 0.6rem 1rem;
  border-radius: 8px;
  border: 1px solid #cbd5e1;
  outline: none;
  font-size: 0.95rem;
}

.search-input:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.1);
}

.filter-group {
  display: flex;
  gap: 0.5rem;
}

.filter-btn {
  background: #f1f5f9;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-btn.active {
  background: #4f46e5;
  color: white;
}

.table-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow-x: auto;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.data-table th {
  background: #f8fafc;
  padding: 0.85rem 1rem;
  font-size: 0.8rem;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e2e8f0;
}

.data-table td {
  padding: 1rem;
  border-bottom: 1px solid #f1f5f9;
  font-size: 0.95rem;
  vertical-align: middle;
}

.data-table tr:last-child td {
  border-bottom: none;
}

.data-table tr:hover {
  background: #fafafa;
}

.id-cell {
  font-family: monospace;
  font-size: 0.85rem;
  color: #64748b;
}

.account-cell {
  font-weight: 500;
  color: #334155;
}

.stock-badge {
  background: #f1f5f9;
  padding: 0.3rem 0.6rem;
  border-radius: 6px;
  font-weight: 700;
  font-size: 0.9rem;
  color: #1e293b;
}

.op-badge {
  display: inline-block;
  padding: 0.25rem 0.65rem;
  border-radius: 50px;
  font-size: 0.8rem;
  font-weight: 700;
  text-align: center;
}

.op-buy {
  background: #dcfce7;
  color: #15803d;
}

.op-sell {
  background: #fee2e2;
  color: #b91c1c;
}

.number-cell {
  font-weight: 600;
}

.price-primary {
  font-weight: 700;
  color: #0f172a;
}

.price-secondary {
  font-size: 0.8rem;
  color: #94a3b8;
}

.date-cell {
  font-size: 0.88rem;
  color: #64748b;
}

.code-badge {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
}

.code-badge.open {
  background: #e0f2fe;
  color: #0369a1;
}

.code-badge.close {
  background: #fef3c7;
  color: #b45309;
}

.code-badge.unknown {
  background: #f1f5f9;
  color: #64748b;
}

.state-container {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  text-align: center;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.error-state {
  border-color: #fecaca;
  background: #fef2f2;
}

.error-msg {
  color: #dc2626;
  font-weight: 600;
  margin-bottom: 1rem;
}

.btn-retry {
  background: #dc2626;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e2e8f0;
  border-top-color: #4f46e5;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 1rem auto;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: #94a3b8;
}
</style>
