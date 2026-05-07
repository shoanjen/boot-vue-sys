import type { Announcement, AnnouncementStatus } from '../types/domain'
import { get, post } from './http'

type AnnouncementDTO = {
  id: number
  clubId: number
  title: string
  content: string
  status: number
  publishedAt?: string
  publishedBy: number
  createdAt?: string
}

function mapStatus(code: number): AnnouncementStatus {
  if (code === 2) return 'OFFLINE'
  return 'PUBLISHED'
}

function mapAnnouncement(dto: AnnouncementDTO): Announcement {
  return {
    id: String(dto.id),
    clubId: String(dto.clubId),
    title: dto.title,
    content: dto.content,
    status: mapStatus(dto.status),
    createdByUserId: String(dto.publishedBy),
    createdAt: dto.publishedAt || dto.createdAt || new Date().toISOString(),
  }
}

/**
 * @param clubId 社团ID
 * @description 公告列表
 * @return 公告列表
 */
export async function apiAnnouncementList(clubId: string): Promise<Announcement[]> {
  const list = await get<AnnouncementDTO[]>(`/api/v1/announcement/list?clubId=${encodeURIComponent(clubId)}`)
  return list.map(mapAnnouncement)
}

/**
 * @param announcementId 公告ID
 * @description 公告详情
 * @return 公告
 */
export async function apiAnnouncementDetail(announcementId: string): Promise<Announcement> {
  const dto = await get<AnnouncementDTO>(`/api/v1/announcement/detail?announcementId=${encodeURIComponent(announcementId)}`)
  return mapAnnouncement(dto)
}

/**
 * @param input 发布公告
 * @description 发布公告（社团管理员）
 * @return 公告
 */
export async function apiAnnouncementPublish(input: { clubId: string; title: string; content: string }): Promise<Announcement> {
  const dto = await post<AnnouncementDTO, any>('/api/v1/announcement/publish', {
    clubId: Number(input.clubId),
    title: input.title,
    content: input.content,
  })
  return mapAnnouncement(dto)
}

