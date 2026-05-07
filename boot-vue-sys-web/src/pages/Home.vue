<template>
  <section class="card">
    <h2 class="card__title">后端联通性</h2>

    <div class="row">
      <button class="btn" :disabled="loading" @click="loadPing">
        {{ loading ? '请求中...' : '调用 /api/v1/system/ping' }}
      </button>
      <div class="status" v-if="apiStatus">
        状态：<span class="status__ok">{{ apiStatus }}</span>
      </div>
    </div>

    <div class="error" v-if="errorMessage">{{ errorMessage }}</div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ping } from '../api/system'
import { useAppStore } from '../stores/app'

const appStore = useAppStore()
const loading = ref(false)
const errorMessage = ref('')

const apiStatus = computed(() => appStore.apiStatus)

/**
 * @description 调用后端健康检查并更新状态
 * @return 无
 */
async function loadPing() {
  try {
    errorMessage.value = ''
    loading.value = true
    const result = await ping()
    appStore.setApiStatus(result)
  } catch (e) {
    errorMessage.value = e instanceof Error ? e.message : '请求失败'
  } finally {
    loading.value = false
  }
}

onMounted(loadPing)
</script>

<style scoped>
.card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 16px;
  max-width: 720px;
}

.card__title {
  margin: 0 0 12px 0;
  font-size: 18px;
}

.row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn {
  appearance: none;
  border: 1px solid #0f172a;
  background: #0f172a;
  color: #ffffff;
  border-radius: 8px;
  padding: 10px 12px;
  cursor: pointer;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.status {
  color: #111827;
}

.status__ok {
  font-weight: 600;
  color: #16a34a;
}

.error {
  margin-top: 12px;
  color: #dc2626;
}
</style>

