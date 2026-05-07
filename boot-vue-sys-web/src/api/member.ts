import type { ClubMember } from '../types/domain'
import { get, post } from './http'

type ClubMemberDTO = {
  id: number
  clubId: number
  userId: number
  role: number
  status: number
  joinedAt: string
}

function mapMember(dto: ClubMemberDTO): ClubMember {
  return {
    id: String(dto.id),
    clubId: String(dto.clubId),
    userId: String(dto.userId),
    role: dto.role === 2 ? 'admin' : 'member',
    joinedAt: dto.joinedAt,
    status: dto.status === 2 ? 'REMOVED' : 'ACTIVE',
  }
}

/**
 * @param clubId 社团ID
 * @description 成员列表（社团管理员）
 * @return 成员列表
 */
export async function apiMemberList(clubId: string): Promise<ClubMember[]> {
  const list = await get<ClubMemberDTO[]>(`/api/v1/member/list?clubId=${encodeURIComponent(clubId)}`)
  return list.map(mapMember)
}

/**
 * @param memberId 成员记录ID
 * @description 移除成员
 * @return 无
 */
export async function apiRemoveMember(memberId: string): Promise<void> {
  await post<void, { memberId: number }>('/api/v1/member/remove', { memberId: Number(memberId) })
}

