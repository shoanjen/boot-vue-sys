import type { Activity, ActivitySignup, Club, ClubMember, JoinRequest } from '../types/domain'
import { get } from './http'
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

type ClubMemberDTO = {
  id: number
  clubId: number
  userId: number
  role: number
  status: number
  joinedAt: string
}

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

type ActivitySignupDTO = {
  id: number
  activityId: number
  userId: number
  status: number
  signupAt: string
  cancelAt?: string | null
  createdAt?: string
}

interface MyCenterResponseDTO {
  myCreatedClubs: ClubDTO[]
  myJoinRequests: JoinRequestDTO[]
  myClubMembers: ClubMemberDTO[]
  myClubs: ClubDTO[]
  mySignups: ActivitySignupDTO[]
  myActivities: ActivityDTO[]
}

function mapJoinStatus(code: number): JoinRequest['status'] {
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
    status: dto.status === 2 ? 'OFFLINE' : dto.status === 0 ? 'DRAFT' : 'PUBLISHED',
    createdByUserId: String(dto.createdBy),
    createdAt: dto.createdAt || new Date().toISOString(),
  }
}

function mapSignup(dto: ActivitySignupDTO): ActivitySignup {
  return {
    id: String(dto.id),
    activityId: String(dto.activityId),
    userId: String(dto.userId),
    status: dto.status === 2 ? 'CANCELED' : 'SIGNED',
    createdAt: dto.signupAt || dto.createdAt || new Date().toISOString(),
    canceledAt: dto.cancelAt || undefined,
  }
}

/**
 * @description 获取我的中心数据并转换为页面需要的结构
 * @return 我的中心数据
 */
export async function apiMyCenter(): Promise<{
  myCreatedClubs: Club[]
  myJoinRequests: JoinRequest[]
  myClubs: Array<{ club: Club; member: ClubMember }>
  mySignups: Array<{ activity: Activity; signup: ActivitySignup }>
}> {
  const data = await get<MyCenterResponseDTO>('/api/v1/me/center')
  const clubs = data.myClubs.map(mapClub)
  const clubMap = new Map(clubs.map(c => [c.id, c]))

  const members = data.myClubMembers.map(mapMember)
  const myClubs = members
    .map(m => {
      const club = clubMap.get(m.clubId)
      if (!club) return null
      return { club, member: m }
    })
    .filter(Boolean) as Array<{ club: Club; member: ClubMember }>

  const activities = data.myActivities.map(mapActivity)
  const activityMap = new Map(activities.map(a => [a.id, a]))
  const signups = data.mySignups.map(mapSignup)
  const mySignups = signups
    .map(s => {
      const a = activityMap.get(s.activityId)
      if (!a) return null
      return { activity: a, signup: s }
    })
    .filter(Boolean) as Array<{ activity: Activity; signup: ActivitySignup }>

  return {
    myCreatedClubs: data.myCreatedClubs.map(mapClub),
    myJoinRequests: data.myJoinRequests.map(mapJoin),
    myClubs,
    mySignups,
  }
}

