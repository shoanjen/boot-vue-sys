import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', () => {
  const apiStatus = ref('')

  /**
   * @param status API状态
   * @description 设置后端连通性状态
   * @return 无
   */
  function setApiStatus(status: string) {
    apiStatus.value = status
  }

  return { apiStatus, setApiStatus }
})
