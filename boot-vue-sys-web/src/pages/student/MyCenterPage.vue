<template>
  <StudentLayout title="我的" hint="学生端 · 个人中心">
    <header class="glass profile">
      <div class="profile__row">
        <div class="avatar"></div>
        <div class="profile__info">
          <div class="profile__name">{{ auth.user?.name }}</div>
          <div class="profile__sub">
            学号：{{ auth.user?.studentNo || auth.user?.account }} · {{ auth.user?.college || '—' }} ·
            {{ auth.user?.grade || '—' }}
          </div>
        </div>
      </div>

      <div class="kpis">
        <div class="kpi">
          <div class="kpi__label">我的社团</div>
          <div class="kpi__value">{{ myClubs.length }}</div>
        </div>
        <div class="kpi">
          <div class="kpi__label">我的报名</div>
          <div class="kpi__value">{{ mySignups.length }}</div>
        </div>
        <div class="kpi kpi--warn">
          <div class="kpi__label">我的申请</div>
          <div class="kpi__value">{{ requestCount }}</div>
        </div>
      </div>
    </header>

    <div class="grid">
      <main class="glass mainCard">
        <div class="tabs">
          <button class="chip" :class="{ 'chip--active': tab === 'clubs' }" type="button" @click="setTab('clubs')">
            我的社团
          </button>
          <button class="chip" :class="{ 'chip--active': tab === 'signups' }" type="button" @click="setTab('signups')">
            我的报名
          </button>
          <button class="chip" :class="{ 'chip--active': tab === 'requests' }" type="button" @click="setTab('requests')">
            我的申请
          </button>
        </div>

        <section v-if="tab === 'clubs'" class="section">
          <div class="section__title">已加入社团</div>
          <div class="section__sub">通过入社审核后会出现在这里</div>

          <div class="list">
            <article v-for="x in myClubs" :key="x.club.id" class="item">
              <div class="item__body">
                <div class="item__row">
                  <div class="item__title">{{ x.club.name }}</div>
                  <span class="pill pill--ok">{{ x.member.role === 'admin' ? '管理员' : '成员' }}</span>
                </div>
                <div class="item__desc">{{ x.club.intro }}</div>
              </div>
              <RouterLink class="btn" :to="`/clubs/${x.club.id}`">查看详情</RouterLink>
            </article>
            <div v-if="myClubs.length === 0" class="empty">暂无加入的社团</div>
          </div>
        </section>

        <section v-else-if="tab === 'signups'" class="section">
          <div class="section__head">
            <div>
              <div class="section__title">我的报名</div>
              <div class="section__sub">MVP 规则：截止前可取消，截止后不可取消</div>
            </div>
            <button class="btn" type="button" @click="loadAll">刷新</button>
          </div>

          <div class="list">
            <article v-for="x in mySignups" :key="x.activity.id" class="item">
              <div class="item__body">
                <div class="item__title">{{ x.activity.title }}</div>
                <div class="item__desc">时间：{{ formatRange(x.activity.startAt, x.activity.endAt) }} · 地点：{{ x.activity.location }}</div>
                <div class="item__meta">
                  <span class="pill">截止：{{ formatShort(x.activity.deadlineAt) }}</span>
                </div>
              </div>
              <div class="item__actions">
                <button class="btn btn--warn" type="button" @click="cancelSignup(x.activity.id)">取消报名</button>
              </div>
            </article>
            <div v-if="mySignups.length === 0" class="empty">暂无报名记录</div>
          </div>
        </section>

        <section v-else class="section">
          <div class="section__title">我的申请</div>
          <div class="section__sub">包括：入社申请 / 创建社团申请</div>

          <div class="list">
            <article v-for="r in myJoinRequests" :key="r.id" class="item">
              <div class="item__body">
                <div class="item__row">
                  <div class="item__title">入社申请</div>
                  <span class="pill" :class="statusPill(r.status)">{{ statusText(r.status) }}</span>
                </div>
                <div class="item__desc">理由：{{ r.reason }}</div>
                <div class="item__meta">
                  <span class="pill">提交：{{ formatShort(r.createdAt) }}</span>
                  <span v-if="r.rejectReason" class="pill pill--warn">驳回原因：{{ r.rejectReason }}</span>
                </div>
              </div>
            </article>

            <article v-for="c in myCreatedClubs" :key="c.id" class="item">
              <div class="item__body">
                <div class="item__row">
                  <div class="item__title">创建社团申请 · {{ c.name }}</div>
                  <span class="pill" :class="clubStatusPill(c.status)">{{ clubStatusText(c.status) }}</span>
                </div>
                <div class="item__desc">简介：{{ c.intro }}</div>
                <div class="item__meta">
                  <span class="pill">提交：{{ formatShort(c.createdAt) }}</span>
                </div>
              </div>
              <RouterLink class="btn" to="/clubs/create">查看申请</RouterLink>
            </article>

            <div v-if="myJoinRequests.length + myCreatedClubs.length === 0" class="empty">暂无申请记录</div>
          </div>
        </section>
      </main>

      <aside class="glass side">
        <div class="side__title">快捷入口</div>
        <div class="side__list">
          <RouterLink class="btn btn--primary" to="/clubs">去社团广场</RouterLink>
          <RouterLink class="btn" to="/clubs/create">创建社团申请</RouterLink>
          <RouterLink v-if="auth.role === 'clubAdmin'" class="btn" to="/admin/club">社团管理员后台</RouterLink>
          <RouterLink v-if="auth.role === 'platformAdmin'" class="btn" to="/admin/platform">平台管理员后台</RouterLink>
        </div>
      </aside>
    </div>
  </StudentLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import StudentLayout from '../../layouts/StudentLayout.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import { apiActivityCancelSignup } from '../../api/activity'
import { apiMyCenter } from '../../api/me'
import type { Club, JoinRequest, JoinRequestStatus } from '../../types/domain'

const auth = useAuthStore()
const ui = useUiStore()
const route = useRoute()
const router = useRouter()

const tab = ref<'clubs' | 'signups' | 'requests'>('clubs')

const myClubs = ref<Array<{ club: Club; member: any }>>([])
const mySignups = ref<Array<{ activity: any; signup: any }>>([])
const myJoinRequests = ref<JoinRequest[]>([])
const myCreatedClubs = ref<Club[]>([])

const requestCount = computed(() => myJoinRequests.value.length + myCreatedClubs.value.length)

/**
 * @param nextTab Tab标识
 * @description 切换Tab并同步到URL
 * @return 无
 */
function setTab(nextTab: 'clubs' | 'signups' | 'requests') {
  tab.value = nextTab
  router.replace({ path: '/me', query: { tab: nextTab } })
}

function formatShort(iso: string) {
  const d = new Date(iso)
  const m = `${d.getMonth() + 1}`.padStart(2, '0')
  const day = `${d.getDate()}`.padStart(2, '0')
  const hh = `${d.getHours()}`.padStart(2, '0')
  const mm = `${d.getMinutes()}`.padStart(2, '0')
  return `${m}-${day} ${hh}:${mm}`
}

function formatRange(startIso: string, endIso: string) {
  const s = new Date(startIso)
  const e = new Date(endIso)
  const m = `${s.getMonth() + 1}`.padStart(2, '0')
  const day = `${s.getDate()}`.padStart(2, '0')
  const sh = `${s.getHours()}`.padStart(2, '0')
  const sm = `${s.getMinutes()}`.padStart(2, '0')
  const eh = `${e.getHours()}`.padStart(2, '0')
  const em = `${e.getMinutes()}`.padStart(2, '0')
  return `${m}-${day} ${sh}:${sm}-${eh}:${em}`
}

function statusText(status: JoinRequestStatus) {
  if (status === 'PENDING') return '审核中'
  if (status === 'APPROVED') return '已通过'
  return '已驳回'
}

function statusPill(status: JoinRequestStatus) {
  if (status === 'PENDING') return 'pill--warn'
  if (status === 'APPROVED') return 'pill--ok'
  return 'pill--danger'
}

function clubStatusText(status: Club['status']) {
  if (status === 'PENDING') return '已提交'
  if (status === 'REJECTED') return '已驳回'
  if (status === 'OFFLINE') return '已下架'
  return '已上架'
}

function clubStatusPill(status: Club['status']) {
  if (status === 'PENDING') return 'pill--warn'
  if (status === 'REJECTED') return 'pill--danger'
  if (status === 'OFFLINE') return 'pill--danger'
  return 'pill--ok'
}

/**
 * @description 拉取我的数据（社团/报名/申请）
 * @return 无
 */
async function loadAll() {
  if (!auth.isAuthed) return
  try {
    const data = await apiMyCenter()
    myClubs.value = data.myClubs
    mySignups.value = data.mySignups
    myJoinRequests.value = data.myJoinRequests
    myCreatedClubs.value = data.myCreatedClubs
  } catch (e) {
    ui.toast('加载失败', e instanceof Error ? e.message : '加载失败')
  }
}

/**
 * @param activityId 活动ID
 * @description 取消报名（截止前可取消）
 * @return 无
 */
async function cancelSignup(activityId: string) {
  if (!auth.isAuthed) return
  try {
    await apiActivityCancelSignup(activityId)
    ui.toast('已取消', '报名已取消')
    await loadAll()
  } catch (e) {
    ui.toast('取消失败', e instanceof Error ? e.message : '取消失败')
  }
}

onMounted(async () => {
  const q = (route.query.tab as string | undefined) || 'clubs'
  if (q === 'signups' || q === 'requests' || q === 'clubs') tab.value = q
  await loadAll()
})
</script>

<style scoped>
.profile {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.profile__row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}

.profile__name {
  font-size: 16px;
  font-weight: 800;
  color: #ffffff;
}

.profile__sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}

.kpis {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  width: min(420px, 100%);
}

.kpi {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
  text-align: center;
}

.kpi__label {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
}

.kpi__value {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 900;
  color: #e5e7eb;
}

.kpi--warn .kpi__value {
  color: #fbbf24;
}

.grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: 1fr 260px;
  gap: 18px;
  align-items: start;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.mainCard {
  overflow: hidden;
}

.tabs {
  padding: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.section {
  padding: 16px;
}

.section__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.section__title {
  font-size: 14px;
  font-weight: 900;
  color: #ffffff;
}

.section__sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}

.list {
  margin-top: 14px;
  display: grid;
  gap: 12px;
}

.item {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.item__body {
  min-width: 0;
}

.item__row {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.item__title {
  font-size: 14px;
  font-weight: 900;
  color: #ffffff;
}

.item__desc {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  line-height: 1.7;
}

.item__meta {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.item__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.empty {
  padding: 18px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  text-align: center;
  color: rgba(229, 231, 235, 0.65);
}

.pill {
  padding: 5px 9px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  font-size: 12px;
  color: rgba(229, 231, 235, 0.9);
}

.pill--ok {
  background: rgba(52, 211, 153, 0.14);
  border-color: rgba(52, 211, 153, 0.22);
  color: #dcfce7;
}

.pill--warn {
  background: rgba(251, 191, 36, 0.14);
  border-color: rgba(251, 191, 36, 0.22);
  color: #fef3c7;
}

.pill--danger {
  background: rgba(248, 113, 113, 0.14);
  border-color: rgba(248, 113, 113, 0.22);
  color: #fee2e2;
}

.side {
  padding: 16px;
}

.side__title {
  font-size: 14px;
  font-weight: 900;
  color: #ffffff;
}

.side__list {
  margin-top: 12px;
  display: grid;
  gap: 10px;
}
</style>
