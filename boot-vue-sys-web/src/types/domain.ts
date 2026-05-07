export type UserRole = 2 | '1' | '3'

export interface User {
  id: string
  name: string
  account: string
  role: UserRole
  studentNo?: string
  phone?: string
  email?: string
  college?: string
  grade?: string
}

export type ClubType = '学术' | '文体' | '公益' | '兴趣'
export type ClubStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'OFFLINE'
export type RecruitStatus = 'OPEN' | 'PAUSED'

export interface Club {
  id: string
  name: string
  type: ClubType
  intro: string
  advisor?: string
  contact: string
  status: ClubStatus
  recruitStatus: RecruitStatus
  coverUrl?: string
  recommended?: boolean
  createdByUserId: string
  createdAt: string
}

export type JoinRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export interface JoinRequest {
  id: string
  clubId: string
  userId: string
  reason: string
  status: JoinRequestStatus
  rejectReason?: string
  createdAt: string
  reviewedAt?: string
  reviewerUserId?: string
}

export interface ClubMember {
  id: string
  clubId: string
  userId: string
  role: 'member' | 'admin'
  joinedAt: string
  status: 'ACTIVE' | 'REMOVED'
}

export type ActivityStatus = 'DRAFT' | 'PUBLISHED' | 'OFFLINE'

export interface Activity {
  id: string
  clubId: string
  title: string
  category: string
  startAt: string
  endAt: string
  location: string
  capacity: number
  deadlineAt: string
  description: string
  status: ActivityStatus
  createdByUserId: string
  createdAt: string
}

export type SignupStatus = 'SIGNED' | 'CANCELED'

export interface ActivitySignup {
  id: string
  activityId: string
  userId: string
  status: SignupStatus
  createdAt: string
  canceledAt?: string
}

export type AnnouncementStatus = 'PUBLISHED' | 'OFFLINE'

export interface Announcement {
  id: string
  clubId: string
  title: string
  content: string
  status: AnnouncementStatus
  createdByUserId: string
  createdAt: string
}

export interface AuditLog {
  id: string
  objectType: string
  objectId: string
  action: string
  reason?: string
  operatorUserId: string
  createdAt: string
}

