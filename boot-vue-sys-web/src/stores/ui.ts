import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface ToastItem {
  id: string
  title: string
  message: string
}

export const useUiStore = defineStore('ui', () => {
  const toasts = ref<ToastItem[]>([])

  function toast(title: string, message: string) {
    const id = `${Math.random().toString(16).slice(2)}_${Date.now().toString(16)}`
    toasts.value.push({ id, title, message })
    window.setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id)
    }, 2600)
  }

  return { toasts, toast }
})

