<template>
  <AuthLayout>
    <div class="grid">
      <section class="glass panel">
        <div class="head">
          <div class="logo"></div>
          <div>
            <div class="sub">大学生社团管理系统</div>
            <h1 class="h1">登录</h1>
          </div>
        </div>

        <div class="roleRow">
          <button
            class="btn roleBtn"
            :class="{ 'roleBtn--active': role === 'student' }"
            type="button"
            @click="role = 'student'"
          >
            学生
          </button>
          <button
            class="btn roleBtn"
            :class="{ 'roleBtn--active': role === 'clubAdmin' }"
            type="button"
            @click="role = 'clubAdmin'"
          >
            社团管理员
          </button>
          <button
            class="btn roleBtn"
            :class="{ 'roleBtn--active': role === 'platformAdmin' }"
            type="button"
            @click="role = 'platformAdmin'"
          >
            平台管理员
          </button>
        </div>

        <form class="form" @submit.prevent="handleLogin">
          <div>
            <label class="label">账号</label>
            <input v-model.trim="account" class="input" placeholder="手机号 / 邮箱 / 学号" />
          </div>
          <div>
            <label class="label">密码</label>
            <div class="pwdRow">
              <input
                v-model.trim="password"
                class="input"
                :type="showPwd ? 'text' : 'password'"
                placeholder="8-20位密码"
              />
              <button class="btn" type="button" @click="showPwd = !showPwd">
                {{ showPwd ? '隐藏' : '显示' }}
              </button>
            </div>
          </div>

          <div class="metaRow">
            <label class="meta">
              <input v-model="remember" type="checkbox" />
              记住我
            </label>
            <button class="linkBtn" type="button" @click="toast('暂不支持', 'MVP版本不包含找回密码流程')">
              忘记密码
            </button>
          </div>

          <button class="btn btn--primary submit" :disabled="loading" type="submit">
            {{ loading ? '登录中...' : '登录并进入系统' }}
          </button>

          <div class="foot">
            <div class="foot__left">
              还没有账号？
              <RouterLink class="link" to="/register">去注册</RouterLink>
            </div>
            <RouterLink class="link" to="/clubs" title="快速进入（演示用）">跳过登录</RouterLink>
          </div>
        </form>
      </section>

      <section class="glass panel panel--right">
        <div class="rightHead">
          <div>
            <div class="rightSub">MVP 覆盖能力</div>
            <div class="rightTitle">更快完成社团运营闭环</div>
          </div>
          <div class="tag">鉴权 + 权限最小集</div>
        </div>

        <div class="featureList">
          <div class="feature">
            <div class="feature__icon feature__icon--green"></div>
            <div>
              <div class="feature__title">社团广场</div>
              <div class="feature__desc">搜索、筛选、详情展示，支持申请加入。</div>
            </div>
          </div>
          <div class="feature">
            <div class="feature__icon feature__icon--brand"></div>
            <div>
              <div class="feature__title">活动管理</div>
              <div class="feature__desc">发布活动、报名统计与导出，支持上限与截止校验。</div>
            </div>
          </div>
          <div class="feature">
            <div class="feature__icon feature__icon--amber"></div>
            <div>
              <div class="feature__title">审核与治理</div>
              <div class="feature__desc">社团创建审核、入社审核、关键操作留痕。</div>
            </div>
          </div>
        </div>

        <div class="demo">
          <div class="demo__head">
            <div class="demo__title">演示账号</div>
            <RouterLink class="demo__link" to="/admin/platform">平台后台演示</RouterLink>
          </div>
          <div class="demo__grid">
            <div class="demo__item">
              <div class="demo__label">学生</div>
              <div class="demo__value">20210001 / 12345678</div>
            </div>
            <div class="demo__item">
              <div class="demo__label">社团管理员</div>
              <div class="demo__value">A10001 / 12345678</div>
            </div>
            <div class="demo__item">
              <div class="demo__label">平台管理员</div>
              <div class="demo__value">P10001 / 12345678</div>
            </div>
          </div>
          <div class="demo__hint">仅用于演示，不代表最终安全方案。</div>
        </div>
      </section>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import AuthLayout from '../../layouts/AuthLayout.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import type { UserRole } from '../../types/domain'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const ui = useUiStore()

const role = ref<UserRole>('student')
const account = ref('20210001')
const password = ref('12345678')
const remember = ref(true)
const showPwd = ref(false)
const loading = ref(false)

function toast(title: string, message: string) {
  ui.toast(title, message)
}

async function handleLogin() {
  if (!account.value.trim()) {
    ui.toast('提示', '请输入账号')
    return
  }
  if (password.value.length < 8) {
    ui.toast('提示', '密码至少8位')
    return
  }
  loading.value = true
  try {
    await auth.login(account.value.trim(), password.value, role.value, remember.value)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    if (redirect) {
      router.replace(redirect)
      return
    }
    if (role.value === 'clubAdmin') {
      router.replace('/admin/club')
      return
    }
    if (role.value === 'platformAdmin') {
      router.replace('/admin/platform')
      return
    }
    router.replace('/clubs')
  } catch (e) {
    ui.toast('登录失败', e instanceof Error ? e.message : '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  align-items: stretch;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .panel--right {
    display: none;
  }
}

.panel {
  padding: 18px;
}

.head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 46px;
  height: 46px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.sub {
  font-size: 13px;
  color: rgba(229, 231, 235, 0.65);
}

.h1 {
  margin: 2px 0 0;
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
}

.roleRow {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.roleBtn {
  justify-content: center;
}

.roleBtn--active {
  background: rgba(97, 112, 190, 0.2);
  border-color: rgba(97, 112, 190, 0.28);
  color: #ffffff;
}

.form {
  margin-top: 16px;
  display: grid;
  gap: 12px;
}

.label {
  display: block;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  margin-bottom: 6px;
}

.pwdRow {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
  align-items: center;
}

.metaRow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.meta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
}

.linkBtn {
  appearance: none;
  border: none;
  background: transparent;
  color: rgba(229, 231, 235, 0.7);
  font-size: 12px;
  cursor: pointer;
}

.linkBtn:hover {
  color: #ffffff;
}

.submit {
  padding: 12px 14px;
}

.foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 13px;
  color: rgba(229, 231, 235, 0.7);
}

.link {
  color: rgba(167, 243, 208, 0.9);
}

.link:hover {
  color: #ffffff;
}

.rightHead {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.rightSub {
  font-size: 12px;
  font-weight: 600;
  color: rgba(229, 231, 235, 0.6);
}

.rightTitle {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
}

.tag {
  padding: 8px 10px;
  font-size: 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}

.featureList {
  margin-top: 14px;
  display: grid;
  gap: 10px;
}

.feature {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
  display: flex;
  gap: 12px;
}

.feature__icon {
  width: 40px;
  height: 40px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}

.feature__icon--green {
  background: rgba(52, 211, 153, 0.14);
  border-color: rgba(52, 211, 153, 0.22);
}

.feature__icon--brand {
  background: rgba(97, 112, 190, 0.2);
  border-color: rgba(97, 112, 190, 0.28);
}

.feature__icon--amber {
  background: rgba(251, 191, 36, 0.14);
  border-color: rgba(251, 191, 36, 0.22);
}

.feature__title {
  font-weight: 700;
  color: #ffffff;
  font-size: 14px;
}

.feature__desc {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
}

.demo {
  margin-top: 14px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
}

.demo__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.demo__title {
  font-weight: 700;
  color: #ffffff;
  font-size: 14px;
}

.demo__link {
  font-size: 12px;
  color: rgba(167, 243, 208, 0.9);
}

.demo__grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.demo__item {
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 10px;
}

.demo__label {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
}

.demo__value {
  margin-top: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #e5e7eb;
}

.demo__hint {
  margin-top: 10px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.5);
}
</style>
