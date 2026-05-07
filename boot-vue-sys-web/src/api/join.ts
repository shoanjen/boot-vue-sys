import type { JoinRequest, JoinRequestStatus } from '../types/domain'
import { get, post } from './http'

type JoinRequestDTO = {
  id: number
  clubId: number
  userId: number
  reason: string
  status: number
  reviewedBy?: number | null
  reviewedAt?: string | null
  rejectReason?: string | null
  createdAt?: string
}

function mapJoinStatus(code: number): JoinRequestStatus {
  if (code === 1) return 'APPROVED'
  if (code === 2) return 'REJECTED'
  return 'PENDING'
}

function mapJoin(dto: JoinRequestDTO): JoinRequest {
  return {
    id: String(dto.id),
    clubId: String(dto.clubId),
    userId: String(dto.userId),
    reason: dto.reason,
    status: mapJoinStatus(dto.status),
    rejectReason: dto.rejectReason || undefined,
    createdAt: dto.createdAt || new Date().toISOString(),
    reviewedAt: dto.reviewedAt || undefined,
    reviewerUserId: dto.reviewedBy ? String(dto.reviewedBy) : undefined,
  }
}

/**
 * @param clubId 社团ID
 * @param reason 申请理由
 * @description 提交入社申请
 * @return 入社申请
 */
export async function apiJoinApply(clubId: string, reason: string): Promise<JoinRequest> {
  const dto = await post<JoinRequestDTO, { clubId: number; reason: string }>('/api/v1/join/apply', {
    clubId: Number(clubId),
    reason,
  })
  return mapJoin(dto)
}

/**
 * @description 我的入社申请列表
 * @return 列表
 */
export async function apiMyJoinRequests(): Promise<JoinRequest[]> {
  const list = await get<JoinRequestDTO[]>('/api/v1/join/myList')
  return list.map(mapJoin)
}

/**
 * @param clubId 社团ID
 * @description 待审核入社申请列表（社团管理员）
 * @return 列表
 */
export async function apiPendingJoinRequests(clubId: string): Promise<JoinRequest[]> {
  const list = await get<JoinRequestDTO[]>(`/api/v1/join/pendingList?clubId=${encodeURIComponent(clubId)}`)
  return list.map(mapJoin)
}

/**
 * @param requestId 入社申请ID
 * @param pass 是否通过
 * @param rejectReason 驳回原因
 * @description 审核入社申请
 * @return 无
 */
export async function apiReviewJoinRequest(requestId: string, pass: boolean, rejectReason?: string): Promise<void> {
  await post<void, { requestId: number; pass: boolean; rejectReason?: string }>('/api/v1/join/review', {
    requestId: Number(requestId),
    pass,
    rejectReason,
  })
}

