import type { Activity, ActivityStatus } from '../types/domain'
import { get, post } from './http'

type ActivityDTO = {
  id: number
  clubId: number
  title: string
  startTime: string
  endTime: string
  place: string
  signupDeadline: string
  signupLimit: number
  status: number
  createdBy: number
  createdAt?: string
}

function mapActivityStatus(code: number): ActivityStatus {
  if (code === 0) return 'DRAFT'
  if (code === 2) return 'OFFLINE'
  return 'PUBLISHED'
}

function mapActivity(dto: ActivityDTO): Activity {
  return {
    id: String(dto.id),
    clubId: String(dto.clubId),
    title: dto.title,
    category: '活动',
    startAt: dto.startTime,
    endAt: dto.endTime,
    location: dto.place,
    capacity: dto.signupLimit,
    deadlineAt: dto.signupDeadline,
    description: '',
    status: mapActivityStatus(dto.status),
    createdByUserId: String(dto.createdBy),
    createdAt: dto.createdAt || new Date().toISOString(),
  }
}

/**
 * @param clubId 社团ID
 * @description 活动列表
 * @return 活动列表
 */
export async function apiActivityList(clubId: string): Promise<Activity[]> {
  const list = await get<ActivityDTO[]>(`/api/v1/activity/list?clubId=${encodeURIComponent(clubId)}`)
  return list.map(mapActivity)
}

/**
 * @param input 创建活动
 * @description 创建并发布活动（社团管理员）
 * @return 活动
 */
export async function apiActivityCreate(input: {
  clubId: string
  title: string
  startTime: string
  endTime: string
  place: string
  signupDeadline: string
  signupLimit: number
}): Promise<Activity> {
  const normalize = (s: string) => (s.length === 16 ? `${s}:00` : s)
  const dto = await post<ActivityDTO, any>('/api/v1/activity/create', {
    clubId: Number(input.clubId),
    title: input.title,
    startTime: normalize(input.startTime),
    endTime: normalize(input.endTime),
    place: input.place,
    signupDeadline: normalize(input.signupDeadline),
    signupLimit: input.signupLimit,
  })
  return mapActivity(dto)
}

/**
 * @param activityId 活动ID
 * @description 报名活动
 * @return 无
 */
export async function apiActivitySignup(activityId: string): Promise<void> {
  await post<void, { activityId: number }>('/api/v1/activity/signup', { activityId: Number(activityId) })
}

/**
 * @param activityId 活动ID
 * @description 取消报名
 * @return 无
 */
export async function apiActivityCancelSignup(activityId: string): Promise<void> {
  await post<void, { activityId: number }>('/api/v1/activity/cancelSignup', { activityId: Number(activityId) })
}
