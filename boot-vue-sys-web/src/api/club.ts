import type { Club } from '../types/domain'
import { get, post } from './http'
import { mapClub } from './mappers'

type ClubDTO = {
  id: number
  name: string
  type: number
  intro: string
  advisorName?: string | null
  contact: string
  status: number
  ownerUserId: number
  createdAt?: string
}

/**
 * @param keyword 关键词
 * @param type 类型码
 * @description 获取社团列表
 * @return 社团列表
 */
export async function apiClubList(keyword?: string, type?: number): Promise<Club[]> {
  const qs = new URLSearchParams()
  if (keyword) qs.set('keyword', keyword)
  if (typeof type === 'number') qs.set('type', String(type))
  const list = await get<ClubDTO[]>(`/api/v1/club/list?${qs.toString()}`)
  return list.map(mapClub)
}

/**
 * @param clubId 社团ID
 * @description 获取社团详情
 * @return 社团
 */
export async function apiClubDetail(clubId: string): Promise<Club> {
  const dto = await get<ClubDTO>(`/api/v1/club/detail?clubId=${encodeURIComponent(clubId)}`)
  return mapClub(dto)
}

/**
 * @description 获取我创建的社团
 * @return 社团列表
 */
export async function apiMyCreatedClubs(): Promise<Club[]> {
  const list = await get<ClubDTO[]>('/api/v1/club/myCreated')
  return list.map(mapClub)
}

/**
 * @param input 创建社团申请
 * @description 提交创建社团申请
 * @return 社团
 */
export async function apiApplyCreateClub(input: {
  name: string
  type: number
  intro: string
  advisorName?: string
  contact: string
}): Promise<Club> {
  const dto = await post<ClubDTO, typeof input>('/api/v1/club/applyCreate', input)
  return mapClub(dto)
}

