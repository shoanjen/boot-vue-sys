import type {
  Activity,
  ActivitySignup,
  Announcement,
  AuditLog,
  Club,
  ClubMember,
  JoinRequest,
  User,
  UserRole,
} from '../types/domain'

export interface MockDb {
  users: User[]
  clubs: Club[]
  joinRequests: JoinRequest[]
  clubMembers: ClubMember[]
  activities: Activity[]
  signups: ActivitySignup[]
  announcements: Announcement[]
  auditLogs: AuditLog[]
}

const STORAGE_KEY = 'boot_vue_sys_mock_db_v1'

function nowIso() {
  return new Date().toISOString()
}

function uid(prefix: string) {
  return `${prefix}_${Math.random().toString(16).slice(2)}_${Date.now().toString(16)}`
}

function seed(): MockDb {
  const student: User = {
    id: uid('u'),
    name: '张同学',
    account: '20210001',
    role: 'student',
    studentNo: '20210001',
    phone: '13800000000',
    email: 'student@example.com',
    college: '计算机学院',
    grade: '2024级',
  }
  const clubAdmin: User = {
    id: uid('u'),
    name: '社团管理员',
    account: 'A10001',
    role: 'clubAdmin',
  }
  const platformAdmin: User = {
    id: uid('u'),
    name: '平台管理员',
    account: 'P10001',
    role: 'platformAdmin',
  }

  const club1: Club = {
    id: uid('c'),
    name: 'AI 学术社',
    type: '学术',
    intro: '一起做项目、读论文、办分享。每周例会 + 工作坊 + 公开课。',
    contact: 'ai-club@school.edu',
    status: 'APPROVED',
    recruitStatus: 'OPEN',
    coverUrl:
      'https://images.unsplash.com/photo-1526378722484-bd91ca387e72?auto=format&fit=crop&w=1800&q=80',
    recommended: true,
    createdByUserId: clubAdmin.id,
    createdAt: nowIso(),
  }

  const club2: Club = {
    id: uid('c'),
    name: '光影摄影社',
    type: '兴趣',
    intro: '一起记录校园、学习构图与后期，定期组织外拍与作品分享。',
    contact: 'photo-club@school.edu',
    status: 'APPROVED',
    recruitStatus: 'PAUSED',
    coverUrl:
      'https://images.unsplash.com/photo-1526481280695-3c687fd5432c?auto=format&fit=crop&w=1800&q=80',
    recommended: false,
    createdByUserId: clubAdmin.id,
    createdAt: nowIso(),
  }

  const pendingClub: Club = {
    id: uid('c'),
    name: '校园手作社',
    type: '兴趣',
    intro: '手工制作、材料分享、作品展出与交流活动。欢迎喜欢手作的同学一起玩。',
    contact: 'handmade-club@school.edu',
    status: 'PENDING',
    recruitStatus: 'OPEN',
    recommended: false,
    createdByUserId: student.id,
    createdAt: nowIso(),
  }

  const members: ClubMember[] = [
    {
      id: uid('m'),
      clubId: club1.id,
      userId: clubAdmin.id,
      role: 'admin',
      joinedAt: nowIso(),
      status: 'ACTIVE',
    },
    {
      id: uid('m'),
      clubId: club1.id,
      userId: student.id,
      role: 'member',
      joinedAt: nowIso(),
      status: 'ACTIVE',
    },
  ]

  const joinRequests: JoinRequest[] = [
    {
      id: uid('jr'),
      clubId: club2.id,
      userId: student.id,
      reason: '希望跟社团一起记录校园活动，学习构图和后期。',
      status: 'PENDING',
      createdAt: nowIso(),
    },
  ]

  const activity1: Activity = {
    id: uid('a'),
    clubId: club1.id,
    title: '从零训练一个小模型：数据 → 训练 → 部署',
    category: '工作坊',
    startAt: new Date(Date.now() + 7 * 24 * 3600 * 1000).toISOString(),
    endAt: new Date(Date.now() + 7 * 24 * 3600 * 1000 + 2 * 3600 * 1000).toISOString(),
    location: '图书馆 3F 创新空间',
    capacity: 30,
    deadlineAt: new Date(Date.now() + 5 * 24 * 3600 * 1000).toISOString(),
    description: '适合有编程基础的同学，现场带你走完数据准备、训练与部署。',
    status: 'PUBLISHED',
    createdByUserId: clubAdmin.id,
    createdAt: nowIso(),
  }

  const activity2: Activity = {
    id: uid('a'),
    clubId: club1.id,
    title: '论文精读：RAG 评测与落地经验',
    category: '分享会',
    startAt: new Date(Date.now() + 2 * 24 * 3600 * 1000).toISOString(),
    endAt: new Date(Date.now() + 2 * 24 * 3600 * 1000 + 3600 * 1000).toISOString(),
    location: '线上会议链接（报名后可见）',
    capacity: 0,
    deadlineAt: new Date(Date.now() + 24 * 3600 * 1000).toISOString(),
    description: '带你理解RAG的评测指标与真实落地中的坑。',
    status: 'PUBLISHED',
    createdByUserId: clubAdmin.id,
    createdAt: nowIso(),
  }

  const announcements: Announcement[] = [
    {
      id: uid('n'),
      clubId: club1.id,
      title: '本周例会：项目组招募与进度同步',
      content: '时间：周三 19:30 · 地点：教学楼 A-302 · 内容：项目组分工、报名与安排。',
      status: 'PUBLISHED',
      createdByUserId: clubAdmin.id,
      createdAt: nowIso(),
    },
    {
      id: uid('n'),
      clubId: club1.id,
      title: '工作坊报名须知：名额与截止规则',
      content: '报名截止前可取消；截止后不可取消（如需例外由管理员手动处理，P1）。',
      status: 'PUBLISHED',
      createdByUserId: clubAdmin.id,
      createdAt: nowIso(),
    },
  ]

  const signups: ActivitySignup[] = [
    {
      id: uid('s'),
      activityId: activity1.id,
      userId: student.id,
      status: 'SIGNED',
      createdAt: nowIso(),
    },
    {
      id: uid('s'),
      activityId: activity2.id,
      userId: student.id,
      status: 'SIGNED',
      createdAt: nowIso(),
    },
  ]

  return {
    users: [student, clubAdmin, platformAdmin],
    clubs: [club1, club2, pendingClub],
    joinRequests,
    clubMembers: members,
    activities: [activity1, activity2],
    signups,
    announcements,
    auditLogs: [],
  }
}

export function loadDb(): MockDb {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    const db = seed()
    saveDb(db)
    return db
  }
  try {
    const parsed = JSON.parse(raw) as MockDb
    if (!parsed.users || !parsed.clubs) {
      const db = seed()
      saveDb(db)
      return db
    }
    return parsed
  } catch {
    const db = seed()
    saveDb(db)
    return db
  }
}

export function saveDb(db: MockDb) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(db))
}

export function resetDb() {
  localStorage.removeItem(STORAGE_KEY)
}

export function createUser(input: Omit<User, 'id'>): User {
  return { ...input, id: uid('u') }
}

export function createClub(input: Omit<Club, 'id' | 'createdAt'>): Club {
  return { ...input, id: uid('c'), createdAt: nowIso() }
}

export function createJoinRequest(input: Omit<JoinRequest, 'id' | 'createdAt'>): JoinRequest {
  return { ...input, id: uid('jr'), createdAt: nowIso() }
}

export function createMember(input: Omit<ClubMember, 'id' | 'joinedAt'>): ClubMember {
  return { ...input, id: uid('m'), joinedAt: nowIso() }
}

export function createActivity(input: Omit<Activity, 'id' | 'createdAt'>): Activity {
  return { ...input, id: uid('a'), createdAt: nowIso() }
}

export function createSignup(input: Omit<ActivitySignup, 'id' | 'createdAt'>): ActivitySignup {
  return { ...input, id: uid('s'), createdAt: nowIso() }
}

export function createAnnouncement(
  input: Omit<Announcement, 'id' | 'createdAt'>
): Announcement {
  return { ...input, id: uid('n'), createdAt: nowIso() }
}

export function createAuditLog(input: Omit<AuditLog, 'id' | 'createdAt'>): AuditLog {
  return { ...input, id: uid('al'), createdAt: nowIso() }
}

export function resolveRoleLabel(role: UserRole): string {
  if (role === 'student') return '学生'
  if (role === 'clubAdmin') return '社团管理员'
  return '平台管理员'
}
