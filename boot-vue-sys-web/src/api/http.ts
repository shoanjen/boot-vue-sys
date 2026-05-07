import axios, { AxiosInstance } from 'axios'

export interface ApiResponse<T> {
  success: boolean
  code: string
  message: string
  data: T
  timestamp: string
}

const AUTH_STORAGE_KEY = 'boot_vue_sys_auth_v1'

/**
 * @param baseURL 请求基础地址
 * @description 创建HTTP客户端（统一处理ApiResponse封装）
 * @return Axios实例
 */
export function createHttpClient(baseURL: string): AxiosInstance {
  const instance = axios.create({
    baseURL,
    timeout: 15000,
    withCredentials: true,
  })

  instance.interceptors.request.use(config => {
    const raw = sessionStorage.getItem(AUTH_STORAGE_KEY) || localStorage.getItem(AUTH_STORAGE_KEY)
    if (raw) {
      try {
        const parsed = JSON.parse(raw) as { token?: string }
        if (parsed.token) {
          config.headers = config.headers || {}
          config.headers.Authorization = `Bearer ${parsed.token}`
        }
      } catch {
      }
    }
    return config
  })

  instance.interceptors.response.use(undefined, (error: unknown) => {
    throw new Error(extractErrorMessage(error))
  })

  return instance
}

/**
 * @param error 任意异常
 * @description 提取可读错误信息
 * @return 错误信息
 */
export function extractErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    return (
      error.response?.data?.message ||
      error.message ||
      '网络异常，请稍后重试'
    )
  }
  if (error instanceof Error) return error.message
  return '未知异常'
}

export const http = createHttpClient(
  (import.meta.env.VITE_API_BASE_URL as string | undefined) || '/'
)

/**
 * @param url 请求路径
 * @description GET请求（自动解包 ApiResponse）
 * @return 业务数据
 */
export async function get<T>(url: string): Promise<T> {
  const resp = await http.get<ApiResponse<T>>(url)
  const body = resp.data
  if (body.success) return body.data
  throw new Error(body.message || '请求失败')
}

/**
 * @param url 请求路径
 * @param data 请求体
 * @description POST请求（自动解包 ApiResponse）
 * @return 业务数据
 */
export async function post<T, B = unknown>(url: string, data: B): Promise<T> {
  const resp = await http.post<ApiResponse<T>>(url, data)
  const body = resp.data
  if (body.success) return body.data
  throw new Error(body.message || '请求失败')
}
