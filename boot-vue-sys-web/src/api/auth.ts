import type { User, UserRole } from '../types/domain'
import { post } from './http'

interface LoginResponseDTO {
  token: string
  user: {
    id: number
    account: string
    name: string
    studentNo?: string | null
    phone?: string | null
    email?: string | null
    role: number
  }
}

function mapRole(roleCode: number): UserRole {
  if (roleCode === 2) return 'clubAdmin'
  if (roleCode === 3) return 'platformAdmin'
  return 'student'
}

function mapUser(dto: LoginResponseDTO['user']): User {
  return {
    id: String(dto.id),
    account: dto.account,
    name: dto.name,
    studentNo: dto.studentNo || undefined,
    phone: dto.phone || undefined,
    email: dto.email || undefined,
    role: mapRole(dto.role),
  }
}

/**
 * @param account 账号
 * @param password 密码
 * @description 登录
 * @return token与用户信息
 */
export async function apiLogin(account: string, password: string): Promise<{ token: string; user: User }> {
  const data = await post<LoginResponseDTO, { account: string; password: string }>('/api/v1/auth/login', {
    account,
    password,
  })
  return { token: data.token, user: mapUser(data.user) }
}

/**
 * @param input 注册信息
 * @description 学生注册（注册后直接登录）
 * @return token与用户信息
 */
export async function apiRegisterStudent(input: {
  account: string
  password: string
  name: string
  studentNo?: string
  phone?: string
  email?: string
}): Promise<{ token: string; user: User }> {
  const data = await post<LoginResponseDTO, typeof input>('/api/v1/auth/register', input)
  return { token: data.token, user: mapUser(data.user) }
}

