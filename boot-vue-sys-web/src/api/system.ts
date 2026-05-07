import { get } from './http'

/**
 * @description 调用后端健康检查
 * @return 健康状态
 */
export async function ping(): Promise<string> {
  return await get<string>('/api/v1/system/ping')
}
