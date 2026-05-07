import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { User, UserRole } from '../types/domain'
import { apiLogin, apiRegisterStudent } from '../api/auth'

const AUTH_KEY = 'boot_vue_sys_auth_v1'

interface AuthState {
  token: string
  user: User
}

function loadAuthState(): AuthState | null {
  const raw = sessionStorage.getItem(AUTH_KEY) || localStorage.getItem(AUTH_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthState
  } catch {
    return null
  }
}

function saveAuthState(state: AuthState | null, remember: boolean) {
  if (!state) {
    sessionStorage.removeItem(AUTH_KEY)
    localStorage.removeItem(AUTH_KEY)
    return
  }
  const raw = JSON.stringify(state)
  if (remember) {
    sessionStorage.removeItem(AUTH_KEY)
    localStorage.setItem(AUTH_KEY, raw)
    return
  }
  localStorage.removeItem(AUTH_KEY)
  sessionStorage.setItem(AUTH_KEY, raw)
}

export const useAuthStore = defineStore('auth', () => {
  const state = ref<AuthState | null>(loadAuthState())

  const user = computed<User | null>(() => state.value?.user || null)
  const token = computed<string | null>(() => state.value?.token || null)

  const isAuthed = computed(() => Boolean(token.value && user.value))
  const role = computed<UserRole | null>(() => user.value?.role || null)

  /**
   * @description 退出登录
   * @return 无
   */
  function logout() {
    state.value = null
    sessionStorage.removeItem(AUTH_KEY)
    localStorage.removeItem(AUTH_KEY)
  }

  /**
   * @param account 账号
   * @param password 密码
   * @param roleHint 角色提示（用于前端校验）
   * @description 登录
   * @return 用户信息
   */
  async function login(account: string, password: string, roleHint: UserRole, remember: boolean): Promise<User> {
    const result = await apiLogin(account, password)
    if (result.user.role !== roleHint) {
      throw new Error('账号角色不匹配，请切换角色后登录')
    }
    state.value = { token: result.token, user: result.user }
    saveAuthState(state.value, remember)
    return result.user
  }

  /**
   * @param input 注册信息
   * @description 学生注册（注册后直接登录）
   * @return 用户信息
   */
  async function registerStudent(input: Omit<User, 'id' | 'role'> & { role?: never; password: string }): Promise<User> {
    const result = await apiRegisterStudent({
      account: input.account,
      password: input.password,
      name: input.name,
      studentNo: input.studentNo,
      phone: input.phone,
      email: input.email,
    })
    state.value = { token: result.token, user: result.user }
    saveAuthState(state.value, true)
    return result.user
  }

  return {
    user,
    token,
    role,
    isAuthed,
    login,
    registerStudent,
    logout,
  }
})
