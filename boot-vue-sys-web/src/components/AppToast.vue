<template>
  <div class="toastWrap" aria-live="polite" role="status">
    <div v-for="t in ui.toasts" :key="t.id" class="toast">
      <div class="toast__icon"></div>
      <div class="toast__content">
        <div class="toast__title">{{ t.title }}</div>
        <div class="toast__msg">{{ t.message }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUiStore } from '../stores/ui'

const ui = useUiStore()
</script>

<style scoped>
.toastWrap {
  position: fixed;
  left: 50%;
  bottom: 24px;
  transform: translateX(-50%);
  z-index: 50;
  display: grid;
  gap: 10px;
  width: min(520px, calc(100vw - 24px));
}

.toast {
  display: flex;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(18px);
  color: #e5e7eb;
  box-shadow: 0 12px 50px rgba(15, 23, 42, 0.18);
}

.toast__icon {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: rgba(52, 211, 153, 0.14);
  border: 1px solid rgba(52, 211, 153, 0.22);
}

.toast__content {
  min-width: 0;
}

.toast__title {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.toast__msg {
  margin-top: 2px;
  font-size: 12px;
  color: #d1d5db;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

