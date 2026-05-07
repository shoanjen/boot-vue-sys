import type { Club, ClubStatus, ClubType } from '../types/domain'

function mapClubType(typeCode: number): ClubType {
  if (typeCode === 1) return '学术'
  if (typeCode === 2) return '文体'
  if (typeCode === 3) return '公益'
  return '兴趣'
}

function mapClubStatus(statusCode: number): ClubStatus {
  if (statusCode === 1) return 'APPROVED'
  if (statusCode === 2) return 'REJECTED'
  if (statusCode === 3) return 'OFFLINE'
  return 'PENDING'
}

/**
 * @param dto 后端社团对象
 * @description 映射为前端 Club
 * @return Club
 */
export function mapClub(dto: {
  id: number
  name: string
  type: number
  intro: string
  advisorName?: string | null
  contact: string
  status: number
  ownerUserId: number
  createdAt?: string
}): Club {
  return {
    id: String(dto.id),
    name: dto.name,
    type: mapClubType(dto.type),
    intro: dto.intro,
    advisor: dto.advisorName || undefined,
    contact: dto.contact,
    status: mapClubStatus(dto.status),
    recruitStatus: 'OPEN',
    createdByUserId: String(dto.ownerUserId),
    createdAt: dto.createdAt || new Date().toISOString(),
  }
}

