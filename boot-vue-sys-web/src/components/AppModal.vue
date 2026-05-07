<template>
  <div v-if="open" class="mask" @click.self="emit('close')">
    <div class="panel" role="dialog" aria-modal="true">
      <div class="panel__header" v-if="title">
        <div class="panel__title">{{ title }}</div>
        <button class="iconBtn" type="button" @click="emit('close')">×</button>
      </div>
      <div class="panel__body">
        <slot />
      </div>
      <div class="panel__footer" v-if="$slots.footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  open: boolean
  title?: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()
</script>

<style scoped>
.mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 60;
  display: grid;
  place-items: center;
  padding: 18px;
}

.panel {
  width: min(920px, 100%);
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(18px);
  color: #e5e7eb;
  box-shadow: 0 12px 50px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}

.panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.panel__title {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.panel__body {
  padding: 14px;
}

.panel__footer {
  padding: 12px 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.iconBtn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: #e5e7eb;
  border-radius: 12px;
  width: 32px;
  height: 32px;
  cursor: pointer;
}
</style>

