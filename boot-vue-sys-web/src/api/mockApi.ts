import type {
  Activity,
  Announcement,
  Club,
  ClubMember,
  JoinRequest,
  User,
  UserRole,
} from '../types/domain'
import {
  createActivity,
  createAnnouncement,
  createAuditLog,
  createClub,
  createJoinRequest,
  createMember,
  createSignup,
  loadDb,
  saveDb,
} from '../mock/db'

function delay(ms: number) {
  return new Promise<void>(resolve => window.setTimeout(resolve, ms))
}

export async function mockLogin(account: string, roleHint: UserRole): Promise<User> {
  await delay(200)
  const db = loadDb()
  const existed = db.users.find(u => u.account === account)
  if (!existed) {
    throw new Error('账号不存在，请先注册（演示环境）')
  }
  if (existed.role !== roleHint) {
    throw new Error('账号角色不匹配，请切换角色后登录')
  }
  return existed
}

export async function mockRegisterStudent(input: Omit<User, 'id' | 'role'>): Promise<User> {
  await delay(200)
  const db = loadDb()
  if (db.users.some(u => u.account === input.account)) {
    throw new Error('账号已存在')
  }
  const newUser: User = {
    ...input,
    id: `u_${Math.random().toString(16).slice(2)}_${Date.now().toString(16)}`,
    role: 'student',
  }
  db.users.unshift(newUser)
  saveDb(db)
  return newUser
}

export async function mockClubList(params: { keyword?: string; type?: string }): Promise<Club[]> {
  await delay(150)
  const db = loadDb()
  const keyword = (params.keyword || '').trim()
  const type = (params.type || '').trim()
  return db.clubs
    .filter(c => c.status === 'APPROVED')
    .filter(c => (type && type !== '全部' ? c.type === type : true))
    .filter(c =>
      keyword
        ? c.name.includes(keyword) || c.intro.includes(keyword) || c.type.includes(keyword)
        : true
    )
}

export async function mockClubDetail(clubId: string): Promise<{
  club: Club
  members: ClubMember[]
  activities: Activity[]
  announcements: Announcement[]
}> {
  await delay(150)
  const db = loadDb()
  const club = db.clubs.find(c => c.id === clubId)
  if (!club) throw new Error('社团不存在')
  const members = db.clubMembers.filter(m => m.clubId === clubId && m.status === 'ACTIVE')
  const activities = db.activities
    .filter(a => a.clubId === clubId)
    .filter(a => a.status === 'PUBLISHED')
    .sort((a, b) => b.startAt.localeCompare(a.startAt))
  const announcements = db.announcements
    .filter(n => n.clubId === clubId)
    .filter(n => n.status === 'PUBLISHED')
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt))
  return { club, members, activities, announcements }
}

export async function mockApplyJoin(input: {
  clubId: string
  userId: string
  reason: string
}): Promise<JoinRequest> {
  await delay(220)
  const db = loadDb()
  const club = db.clubs.find(c => c.id === input.clubId)
  if (!club || club.status !== 'APPROVED') throw new Error('社团不可用')
  if (club.recruitStatus !== 'OPEN') throw new Error('当前不可申请加入')

  const isMember = db.clubMembers.some(
    m => m.clubId === input.clubId && m.userId === input.userId && m.status === 'ACTIVE'
  )
  if (isMember) throw new Error('你已是社团成员')

  const duplicated = db.joinRequests.some(
    r => r.clubId === input.clubId && r.userId === input.userId && r.status === 'PENDING'
  )
  if (duplicated) throw new Error('已提交申请，请等待审核')

  const request = createJoinRequest({
    clubId: input.clubId,
    userId: input.userId,
    reason: input.reason,
    status: 'PENDING',
  })
  db.joinRequests.unshift(request)
  saveDb(db)
  return request
}

export async function mockApplyCreateClub(input: {
  userId: string
  name: string
  type: Club['type']
  intro: string
  advisor?: string
  contact: string
}): Promise<Club> {
  await delay(240)
  const db = loadDb()
  const duplicated = db.clubs.some(c => c.name === input.name && c.status !== 'REJECTED')
  if (duplicated) throw new Error('社团名称已存在')

  const club = createClub({
    name: input.name,
    type: input.type,
    intro: input.intro,
    advisor: input.advisor || undefined,
    contact: input.contact,
    status: 'PENDING',
    recruitStatus: 'OPEN',
    recommended: false,
    createdByUserId: input.userId,
    coverUrl: undefined,
  })
  db.clubs.unshift(club)
  db.auditLogs.unshift(
    createAuditLog({
      objectType: 'club',
      objectId: club.id,
      action: 'CLUB_APPLY_CREATE',
      operatorUserId: input.userId,
      reason: undefined,
    })
  )
  saveDb(db)
  return club
}

export async function mockMyData(userId: string) {
  await delay(120)
  const db = loadDb()

  const myMembers = db.clubMembers.filter(m => m.userId === userId && m.status === 'ACTIVE')
  const myClubs = myMembers
    .map(m => ({ club: db.clubs.find(c => c.id === m.clubId), member: m }))
    .filter(x => Boolean(x.club))
    .map(x => x as { club: Club; member: ClubMember })

  const mySignups = db.signups
    .filter(s => s.userId === userId && s.status === 'SIGNED')
    .map(s => ({ signup: s, activity: db.activities.find(a => a.id === s.activityId) }))
    .filter(x => Boolean(x.activity))
    .map(x => x as { signup: { id: string; activityId: string }; activity: Activity })

  const myJoinRequests = db.joinRequests
    .filter(r => r.userId === userId)
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt))

  const myCreatedClubs = db.clubs
    .filter(c => c.createdByUserId === userId && c.status !== 'APPROVED')
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt))

  return { myClubs, mySignups, myJoinRequests, myCreatedClubs }
}

export async function mockCancelSignup(input: { userId: string; activityId: string }): Promise<void> {
  await delay(180)
  const db = loadDb()
  const activity = db.activities.find(a => a.id === input.activityId)
  if (!activity) throw new Error('活动不存在')
  const deadline = new Date(activity.deadlineAt).getTime()
  if (Date.now() > deadline) throw new Error('报名已截止，无法取消')

  const signup = db.signups.find(
    s => s.userId === input.userId && s.activityId === input.activityId && s.status === 'SIGNED'
  )
  if (!signup) throw new Error('未找到报名记录')
  signup.status = 'CANCELED'
  signup.canceledAt = new Date().toISOString()
  saveDb(db)
}

export async function mockSignupActivity(input: { userId: string; activityId: string }): Promise<void> {
  await delay(180)
  const db = loadDb()
  const activity = db.activities.find(a => a.id === input.activityId)
  if (!activity || activity.status !== 'PUBLISHED') throw new Error('活动不可报名')

  const deadline = new Date(activity.deadlineAt).getTime()
  if (Date.now() > deadline) throw new Error('报名已截止')

  const isMember = db.clubMembers.some(
    m => m.clubId === activity.clubId && m.userId === input.userId && m.status === 'ACTIVE'
  )
  if (!isMember) throw new Error('加入社团后可报名')

  const existed = db.signups.find(
    s => s.userId === input.userId && s.activityId === input.activityId && s.status === 'SIGNED'
  )
  if (existed) throw new Error('你已报名该活动')

  const signedCount = db.signups.filter(s => s.activityId === input.activityId && s.status === 'SIGNED')
    .length
  if (activity.capacity > 0 && signedCount >= activity.capacity) throw new Error('报名人数已满')

  db.signups.unshift(
    createSignup({
      activityId: input.activityId,
      userId: input.userId,
      status: 'SIGNED',
    })
  )
  saveDb(db)
}

export async function mockClubAdminDashboard(input: { userId: string; clubId?: string }) {
  await delay(120)
  const db = loadDb()
  const clubId =
    input.clubId ||
    db.clubMembers.find(m => m.userId === input.userId && m.role === 'admin' && m.status === 'ACTIVE')
      ?.clubId
  if (!clubId) throw new Error('未找到可管理的社团')
  const club = db.clubs.find(c => c.id === clubId)
  if (!club) throw new Error('社团不存在')

  const joinRequests = db.joinRequests
    .filter(r => r.clubId === clubId)
    .sort((a, b) => b.createdAt.localeCompare(a.createdAt))
  const members = db.clubMembers.filter(m => m.clubId === clubId && m.status === 'ACTIVE')
  const activities = db.activities.filter(a => a.clubId === clubId).sort((a, b) => b.createdAt.localeCompare(a.createdAt))
  const announcements = db.announcements.filter(n => n.clubId === clubId).sort((a, b) => b.createdAt.localeCompare(a.createdAt))

  return { club, joinRequests, members, activities, announcements, users: db.users }
}

export async function mockReviewJoinRequest(input: {
  operatorUserId: string
  requestId: string
  action: 'APPROVE' | 'REJECT'
  rejectReason?: string
}): Promise<void> {
  await delay(200)
  const db = loadDb()
  const req = db.joinRequests.find(r => r.id === input.requestId)
  if (!req) throw new Error('申请不存在')
  if (req.status !== 'PENDING') throw new Error('申请已处理')

  if (input.action === 'REJECT') {
    if (!input.rejectReason?.trim()) throw new Error('请填写驳回原因')
    req.status = 'REJECTED'
    req.rejectReason = input.rejectReason.trim()
  } else {
    req.status = 'APPROVED'
    db.clubMembers.unshift(
      createMember({
        clubId: req.clubId,
        userId: req.userId,
        role: 'member',
        status: 'ACTIVE',
      })
    )
  }
  req.reviewedAt = new Date().toISOString()
  req.reviewerUserId = input.operatorUserId

  db.auditLogs.unshift(
    createAuditLog({
      objectType: 'joinRequest',
      objectId: req.id,
      action: input.action === 'APPROVE' ? 'JOIN_APPROVE' : 'JOIN_REJECT',
      operatorUserId: input.operatorUserId,
      reason: input.rejectReason?.trim() || undefined,
    })
  )
  saveDb(db)
}

export async function mockRemoveMember(input: {
  operatorUserId: string
  memberId: string
}): Promise<void> {
  await delay(180)
  const db = loadDb()
  const member = db.clubMembers.find(m => m.id === input.memberId)
  if (!member) throw new Error('成员不存在')
  member.status = 'REMOVED'
  db.auditLogs.unshift(
    createAuditLog({
      objectType: 'clubMember',
      objectId: member.id,
      action: 'MEMBER_REMOVE',
      operatorUserId: input.operatorUserId,
      reason: undefined,
    })
  )
  saveDb(db)
}

export async function mockCreateActivity(input: {
  operatorUserId: string
  clubId: string
  title: string
  category: string
  startAt: string
  endAt: string
  location: string
  capacity: number
  deadlineAt: string
  description: string
}): Promise<Activity> {
  await delay(220)
  const db = loadDb()
  const activity = createActivity({
    clubId: input.clubId,
    title: input.title,
    category: input.category,
    startAt: input.startAt,
    endAt: input.endAt,
    location: input.location,
    capacity: input.capacity,
    deadlineAt: input.deadlineAt,
    description: input.description,
    status: 'PUBLISHED',
    createdByUserId: input.operatorUserId,
  })
  db.activities.unshift(activity)
  saveDb(db)
  return activity
}

export async function mockCreateAnnouncement(input: {
  operatorUserId: string
  clubId: string
  title: string
  content: string
}): Promise<Announcement> {
  await delay(220)
  const db = loadDb()
  const ann = createAnnouncement({
    clubId: input.clubId,
    title: input.title,
    content: input.content,
    status: 'PUBLISHED',
    createdByUserId: input.operatorUserId,
  })
  db.announcements.unshift(ann)
  saveDb(db)
  return ann
}

export async function mockPlatformDashboard() {
  await delay(150)
  const db = loadDb()
  const pending = db.clubs.filter(c => c.status === 'PENDING').sort((a, b) => b.createdAt.localeCompare(a.createdAt))
  return { pending, users: db.users }
}

export async function mockReviewClubCreate(input: {
  operatorUserId: string
  clubId: string
  action: 'APPROVE' | 'REJECT'
  rejectReason?: string
}): Promise<void> {
  await delay(220)
  const db = loadDb()
  const club = db.clubs.find(c => c.id === input.clubId)
  if (!club) throw new Error('社团不存在')
  if (club.status !== 'PENDING') throw new Error('该申请已处理')

  if (input.action === 'REJECT') {
    if (!input.rejectReason?.trim()) throw new Error('请填写驳回原因')
    club.status = 'REJECTED'
    db.auditLogs.unshift(
      createAuditLog({
        objectType: 'club',
        objectId: club.id,
        action: 'CLUB_REJECT',
        operatorUserId: input.operatorUserId,
        reason: input.rejectReason.trim(),
      })
    )
  } else {
    club.status = 'APPROVED'
    const applicantId = club.createdByUserId
    db.clubMembers.unshift(
      createMember({
        clubId: club.id,
        userId: applicantId,
        role: 'admin',
        status: 'ACTIVE',
      })
    )
    const applicant = db.users.find(u => u.id === applicantId)
    if (applicant) applicant.role = 'clubAdmin'

    db.auditLogs.unshift(
      createAuditLog({
        objectType: 'club',
        objectId: club.id,
        action: 'CLUB_APPROVE',
        operatorUserId: input.operatorUserId,
        reason: undefined,
      })
    )
  }
  saveDb(db)
}

