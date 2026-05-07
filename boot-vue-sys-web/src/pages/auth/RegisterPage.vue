<template>
  <AuthLayout>
    <div class="topBar">
      <RouterLink class="btn" to="/login">返回登录</RouterLink>
      <RouterLink class="link" to="/clubs">跳过注册（演示）</RouterLink>
    </div>

    <div class="grid">
      <section class="glass panel">
        <div class="head">
          <div class="logo"></div>
          <div>
            <div class="sub">创建你的账号</div>
            <h1 class="h1">注册</h1>
          </div>
        </div>

        <form class="form" @submit.prevent="handleRegister">
          <div class="row2">
            <div>
              <label class="label">姓名</label>
              <input v-model.trim="name" class="input" placeholder="如：张同学" />
            </div>
            <div>
              <label class="label">学号</label>
              <input v-model.trim="studentNo" class="input" placeholder="如：20210001" />
            </div>
          </div>

          <div class="row2">
            <div>
              <label class="label">手机号（可选）</label>
              <input v-model.trim="phone" class="input" placeholder="用于找回账号" />
            </div>
            <div>
              <label class="label">邮箱（可选）</label>
              <input v-model.trim="email" class="input" placeholder="用于接收通知" />
            </div>
          </div>

          <div class="row2">
            <div>
              <label class="label">密码</label>
              <div class="pwdRow">
                <input
                  v-model.trim="password"
                  class="input"
                  :type="showPwd ? 'text' : 'password'"
                  placeholder="8-20位"
                />
                <button class="btn" type="button" @click="showPwd = !showPwd">
                  {{ showPwd ? '隐藏' : '显示' }}
                </button>
              </div>
            </div>
            <div>
              <label class="label">确认密码</label>
              <input
                v-model.trim="password2"
                class="input"
                type="password"
                placeholder="再次输入"
              />
            </div>
          </div>

          <div class="row2">
            <div>
              <label class="label">学院（P1）</label>
              <select v-model="college" class="input">
                <option>计算机学院</option>
                <option>经管学院</option>
                <option>文学院</option>
                <option>理学院</option>
              </select>
            </div>
            <div>
              <label class="label">年级（P1）</label>
              <select v-model="grade" class="input">
                <option>2025级</option>
                <option selected>2024级</option>
                <option>2023级</option>
                <option>2022级</option>
              </select>
            </div>
          </div>

          <label class="agree">
            <input v-model="agree" type="checkbox" />
            我已阅读并同意《用户协议》和《隐私政策》（演示）
          </label>

          <button class="btn btn--primary submit" :disabled="loading" type="submit">
            {{ loading ? '提交中...' : '完成注册并进入社团广场' }}
          </button>
        </form>
      </section>

      <section class="glass panel panel--right">
        <div class="rightSub">注册后你可以</div>
        <div class="rightTitle">快速找到适合你的社团</div>

        <div class="featureList">
          <div class="feature">
            <div class="feature__icon"></div>
            <div>
              <div class="feature__title">浏览与搜索</div>
              <div class="feature__desc">按类型筛选：学术 / 文体 / 公益等。</div>
            </div>
          </div>
          <div class="feature">
            <div class="feature__icon feature__icon--green"></div>
            <div>
              <div class="feature__title">申请加入</div>
              <div class="feature__desc">提交理由，等待社团管理员审核。</div>
            </div>
          </div>
          <div class="feature">
            <div class="feature__icon feature__icon--brand"></div>
            <div>
              <div class="feature__title">报名活动</div>
              <div class="feature__desc">支持人数上限与报名截止时间校验。</div>
            </div>
          </div>
        </div>

        <div class="note">
          <div class="note__title">站内通知（MVP）</div>
          <div class="note__desc">
            审核结果与关键操作通过站内消息触达（阅读率作为成功指标之一）。
          </div>
        </div>
      </section>
    </div>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import AuthLayout from '../../layouts/AuthLayout.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()

const name = ref('张同学')
const studentNo = ref('20210002')
const phone = ref('13800000000')
const email = ref('student@example.com')
const password = ref('12345678')
const password2 = ref('12345678')
const showPwd = ref(false)
const college = ref('计算机学院')
const grade = ref('2024级')
const agree = ref(true)
const loading = ref(false)

async function handleRegister() {
  if (!agree.value) {
    ui.toast('提示', '请先勾选协议')
    return
  }
  if (!name.value.trim()) {
    ui.toast('提示', '请输入姓名')
    return
  }
  if (!studentNo.value.trim()) {
    ui.toast('提示', '请输入学号')
    return
  }
  if (password.value.length < 8) {
    ui.toast('提示', '密码至少8位')
    return
  }
  if (password.value !== password2.value) {
    ui.toast('提示', '两次密码不一致')
    return
  }

  loading.value = true
  try {
    await auth.registerStudent({
      name: name.value.trim(),
      account: studentNo.value.trim(),
      password: password.value,
      studentNo: studentNo.value.trim(),
      phone: phone.value.trim() || undefined,
      email: email.value.trim() || undefined,
    })
    router.replace('/clubs')
  } catch (e) {
    ui.toast('注册失败', e instanceof Error ? e.message : '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.topBar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.link {
  font-size: 13px;
  color: rgba(229, 231, 235, 0.75);
}

.link:hover {
  color: #ffffff;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 18px;
  align-items: start;
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

.form {
  margin-top: 16px;
  display: grid;
  gap: 12px;
}

.row2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

@media (max-width: 680px) {
  .row2 {
    grid-template-columns: 1fr;
  }
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

.agree {
  display: inline-flex;
  gap: 10px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
}

.submit {
  padding: 12px 14px;
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

.note {
  margin-top: 14px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
}

.note__title {
  font-size: 14px;
  font-weight: 700;
  color: #ffffff;
}

.note__desc {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  line-height: 1.6;
}
</style>
