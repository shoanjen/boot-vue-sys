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
 * @description 待审核社团列表（平台管理员）
 * @return 社团列表
 */
export async function apiPlatformPendingClubs(): Promise<Club[]> {
  const list = await get<ClubDTO[]>('/api/v1/platform/club/pendingList')
  return list.map(mapClub)
}

/**
 * @param clubId 社团ID
 * @param pass 是否通过
 * @param rejectReason 驳回原因
 * @description 审核社团创建申请
 * @return 无
 */
export async function apiPlatformReviewClub(clubId: string, pass: boolean, rejectReason?: string): Promise<void> {
  await post<void, { clubId: number; pass: boolean; rejectReason?: string }>('/api/v1/platform/club/review', {
    clubId: Number(clubId),
    pass,
    rejectReason,
  })
}

