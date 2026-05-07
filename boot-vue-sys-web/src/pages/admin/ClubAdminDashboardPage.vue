<template>
  <AdminLayout side-title="社团管理员后台" :side-sub="clubName">
    <template #nav>
      <button class="navBtn" :class="{ 'navBtn--active': section === 'join' }" type="button" @click="section = 'join'">
        入社审核
      </button>
      <button class="navBtn" :class="{ 'navBtn--active': section === 'members' }" type="button" @click="section = 'members'">
        成员管理
      </button>
      <button class="navBtn" :class="{ 'navBtn--active': section === 'activities' }" type="button" @click="section = 'activities'">
        活动管理
      </button>
      <button class="navBtn" :class="{ 'navBtn--active': section === 'announcements' }" type="button" @click="section = 'announcements'">
        公告管理
      </button>
      <button class="navBtn" type="button" @click="handleLogout">退出</button>
    </template>

    <header class="glass head">
      <div>
        <div class="head__sub">管理面板</div>
        <div class="head__title">{{ sectionTitle }}</div>
      </div>
      <div class="head__actions">
        <button class="btn" type="button" @click="loadAll">刷新</button>
      </div>
    </header>

    <section class="glass kpis">
      <div class="kpi">
        <div class="kpi__label">待审入社</div>
        <div class="kpi__value kpi__value--warn">{{ joinPendingCount }}</div>
      </div>
      <div class="kpi">
        <div class="kpi__label">成员数</div>
        <div class="kpi__value">{{ members.length }}</div>
      </div>
      <div class="kpi">
        <div class="kpi__label">活动数</div>
        <div class="kpi__value">{{ activities.length }}</div>
      </div>
    </section>

    <section v-if="section === 'join'" class="glass card">
      <div class="card__head">
        <div>
          <div class="card__title">入社申请列表</div>
          <div class="card__sub">通过/驳回需记录审核人、时间与原因（演示环境）</div>
        </div>
      </div>

      <div class="tableWrap">
        <table class="table">
          <thead>
            <tr>
              <th>申请人</th>
              <th>理由摘要</th>
              <th>提交时间</th>
              <th>状态</th>
              <th class="right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="r in joinRequests" :key="r.id">
              <td>{{ userName(r.userId) }}</td>
              <td class="muted">{{ r.reason }}</td>
              <td class="muted">{{ formatShort(r.createdAt) }}</td>
              <td>
                <span class="pill" :class="joinStatusPill(r.status)">{{ joinStatusText(r.status) }}</span>
              </td>
              <td class="right">
                <button class="btn btn--primary" type="button" :disabled="r.status !== 'PENDING'" @click="openReview(r.id, 'APPROVE')">
                  通过
                </button>
                <button class="btn btn--warn" type="button" :disabled="r.status !== 'PENDING'" @click="openReview(r.id, 'REJECT')">
                  驳回
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="joinRequests.length === 0" class="empty">暂无入社申请</div>
      </div>
    </section>

    <section v-else-if="section === 'members'" class="glass card">
      <div class="card__head">
        <div>
          <div class="card__title">成员管理</div>
          <div class="card__sub">MVP：成员列表/移除（角色设置为P1）</div>
        </div>
        <button class="btn" type="button" @click="ui.toast('已导出', '已生成CSV（演示）')">导出名单（P1）</button>
      </div>

      <div class="tableWrap">
        <table class="table">
          <thead>
            <tr>
              <th>成员</th>
              <th>角色</th>
              <th>加入时间</th>
              <th class="right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="m in members" :key="m.id">
              <td>{{ userName(m.userId) }}</td>
              <td class="muted">{{ m.role === 'admin' ? '管理员' : '成员' }}</td>
              <td class="muted">{{ formatShort(m.joinedAt) }}</td>
              <td class="right">
                <button class="btn btn--warn" type="button" :disabled="m.role === 'admin'" @click="removeMember(m.id)">
                  移除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="members.length === 0" class="empty">暂无成员</div>
      </div>
    </section>

    <section v-else-if="section === 'activities'" class="glass card">
      <div class="card__head">
        <div>
          <div class="card__title">活动管理</div>
          <div class="card__sub">创建/发布/下架；报名名单查看与导出为P1（演示环境）</div>
        </div>
        <button class="btn btn--primary" type="button" @click="openActivity">创建活动</button>
      </div>

      <div class="list">
        <article v-for="a in activities" :key="a.id" class="item">
          <div class="item__body">
            <div class="item__row">
              <div class="item__title">{{ a.title }}</div>
              <span class="pill">{{ a.status }}</span>
            </div>
            <div class="item__desc">时间：{{ formatRange(a.startAt, a.endAt) }} · 地点：{{ a.location }}</div>
          </div>
          <div class="item__actions">
            <button class="btn" type="button" @click="ui.toast('提示', '报名名单导出为P1（演示）')">报名名单（P1）</button>
          </div>
        </article>
        <div v-if="activities.length === 0" class="empty">暂无活动</div>
      </div>
    </section>

    <section v-else class="glass card">
      <div class="card__head">
        <div>
          <div class="card__title">公告管理</div>
          <div class="card__sub">发布公告（站内）；已读状态为P1（演示环境）</div>
        </div>
        <button class="btn btn--primary" type="button" @click="openAnnouncement">发布公告</button>
      </div>

      <div class="list">
        <article v-for="n in announcements" :key="n.id" class="item">
          <div class="item__body">
            <div class="item__row">
              <div class="item__title">{{ n.title }}</div>
              <span class="pill">{{ formatShort(n.createdAt) }}</span>
            </div>
            <div class="item__desc">{{ n.content }}</div>
          </div>
        </article>
        <div v-if="announcements.length === 0" class="empty">暂无公告</div>
      </div>
    </section>

    <AppModal :open="reviewOpen" :title="reviewTitle" @close="closeReview">
      <div class="modal">
        <div class="modal__hint">驳回时需要填写原因</div>
        <textarea v-model.trim="rejectReason" class="input textarea" placeholder="如：理由不清晰 / 信息不完整"></textarea>
      </div>
      <template #footer>
        <button class="btn" type="button" @click="closeReview">取消</button>
        <button class="btn btn--primary" type="button" :disabled="reviewLoading" @click="submitReview">
          {{ reviewLoading ? '提交中...' : '确认' }}
        </button>
      </template>
    </AppModal>

    <AppModal :open="activityOpen" title="创建活动" @close="activityOpen = false">
      <div class="modal">
        <div class="row2">
          <div>
            <div class="modal__label">活动标题</div>
            <input v-model.trim="activityTitle" class="input" placeholder="2-50字" />
          </div>
          <div>
            <div class="modal__label">地点</div>
            <input v-model.trim="activityLocation" class="input" placeholder="2-100字" />
          </div>
        </div>
        <div class="row2">
          <div>
            <div class="modal__label">开始时间</div>
            <input v-model="activityStart" class="input" type="datetime-local" />
          </div>
          <div>
            <div class="modal__label">结束时间</div>
            <input v-model="activityEnd" class="input" type="datetime-local" />
          </div>
        </div>
        <div class="row2">
          <div>
            <div class="modal__label">报名截止</div>
            <input v-model="activityDeadline" class="input" type="datetime-local" />
          </div>
          <div>
            <div class="modal__label">人数上限（0不限）</div>
            <input v-model.number="activityCapacity" class="input" type="number" min="0" />
          </div>
        </div>
        <div>
          <div class="modal__label">活动说明</div>
          <textarea v-model.trim="activityDesc" class="input textarea" placeholder="10-1000字"></textarea>
        </div>
      </div>
      <template #footer>
        <button class="btn" type="button" @click="activityOpen = false">取消</button>
        <button class="btn btn--primary" type="button" :disabled="activityLoading" @click="submitActivity">
          {{ activityLoading ? '提交中...' : '创建并发布' }}
        </button>
      </template>
    </AppModal>

    <AppModal :open="announcementOpen" title="发布公告" @close="announcementOpen = false">
      <div class="modal">
        <div>
          <div class="modal__label">标题</div>
          <input v-model.trim="announcementTitle" class="input" placeholder="2-50字" />
        </div>
        <div>
          <div class="modal__label">内容</div>
          <textarea v-model.trim="announcementContent" class="input textarea" placeholder="10-3000字"></textarea>
        </div>
      </div>
      <template #footer>
        <button class="btn" type="button" @click="announcementOpen = false">取消</button>
        <button class="btn btn--primary" type="button" :disabled="announcementLoading" @click="submitAnnouncement">
          {{ announcementLoading ? '提交中...' : '发布' }}
        </button>
      </template>
    </AppModal>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminLayout from '../../layouts/AdminLayout.vue'
import AppModal from '../../components/AppModal.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import { apiMyCenter } from '../../api/me'
import { apiPendingJoinRequests, apiReviewJoinRequest } from '../../api/join'
import { apiMemberList, apiRemoveMember } from '../../api/member'
import { apiActivityCreate, apiActivityList } from '../../api/activity'
import { apiAnnouncementList, apiAnnouncementPublish } from '../../api/announcement'
import type { Activity, Announcement, ClubMember, JoinRequest, JoinRequestStatus } from '../../types/domain'

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()

const section = ref<'join' | 'members' | 'activities' | 'announcements'>('join')

const clubName = ref('—')
const clubId = ref('')
const joinRequests = ref<JoinRequest[]>([])
const members = ref<ClubMember[]>([])
const activities = ref<Activity[]>([])
const announcements = ref<Announcement[]>([])

const joinPendingCount = computed(() => joinRequests.value.filter(r => r.status === 'PENDING').length)

const sectionTitle = computed(() => {
  if (section.value === 'join') return '入社审核'
  if (section.value === 'members') return '成员管理'
  if (section.value === 'activities') return '活动管理'
  return '公告管理'
})

function userName(userId: string) {
  return userId
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

function joinStatusText(status: JoinRequestStatus) {
  if (status === 'PENDING') return '审核中'
  if (status === 'APPROVED') return '已通过'
  return '已驳回'
}

function joinStatusPill(status: JoinRequestStatus) {
  if (status === 'PENDING') return 'pill--warn'
  if (status === 'APPROVED') return 'pill--ok'
  return 'pill--danger'
}

/**
 * @description 拉取后台数据（入社/成员/活动/公告）
 * @return 无
 */
async function loadAll() {
  if (!auth.isAuthed) return
  try {
    const me = await apiMyCenter()
    const adminClub = me.myClubs.find(x => x.member.role === 'admin')
    if (!adminClub) {
      clubId.value = ''
      clubName.value = '—'
      joinRequests.value = []
      members.value = []
      activities.value = []
      announcements.value = []
      ui.toast('提示', '当前账号不是任何社团的管理员')
      return
    }
    clubId.value = adminClub.club.id
    clubName.value = adminClub.club.name

    const [jr, mems, acts, anns] = await Promise.all([
      apiPendingJoinRequests(clubId.value),
      apiMemberList(clubId.value),
      apiActivityList(clubId.value),
      apiAnnouncementList(clubId.value),
    ])
    joinRequests.value = jr
    members.value = mems
    activities.value = acts
    announcements.value = anns
  } catch (e) {
    ui.toast('加载失败', e instanceof Error ? e.message : '加载失败')
  }
}

function handleLogout() {
  auth.logout()
  router.replace('/login')
}

const reviewOpen = ref(false)
const reviewLoading = ref(false)
const reviewRequestId = ref('')
const reviewAction = ref<'APPROVE' | 'REJECT'>('APPROVE')
const rejectReason = ref('')

const reviewTitle = computed(() => (reviewAction.value === 'APPROVE' ? '通过入社申请' : '驳回入社申请'))

/**
 * @param requestId 入社申请ID
 * @param action 审核动作
 * @description 打开审核弹窗
 * @return 无
 */
function openReview(requestId: string, action: 'APPROVE' | 'REJECT') {
  reviewRequestId.value = requestId
  reviewAction.value = action
  rejectReason.value = ''
  reviewOpen.value = true
}

/**
 * @description 关闭审核弹窗
 * @return 无
 */
function closeReview() {
  reviewOpen.value = false
}

/**
 * @description 提交审核结果
 * @return 无
 */
async function submitReview() {
  if (!auth.isAuthed) return
  reviewLoading.value = true
  try {
    await apiReviewJoinRequest(reviewRequestId.value, reviewAction.value === 'APPROVE', rejectReason.value.trim() || undefined)
    ui.toast('已处理', '审核结果已提交')
    closeReview()
    await loadAll()
  } catch (e) {
    ui.toast('提交失败', e instanceof Error ? e.message : '提交失败')
  } finally {
    reviewLoading.value = false
  }
}

/**
 * @param memberId 成员ID
 * @description 移除成员
 * @return 无
 */
async function removeMember(memberId: string) {
  if (!auth.isAuthed) return
  try {
    await apiRemoveMember(memberId)
    ui.toast('已移除', '成员已移除')
    await loadAll()
  } catch (e) {
    ui.toast('操作失败', e instanceof Error ? e.message : '操作失败')
  }
}

const activityOpen = ref(false)
const activityLoading = ref(false)
const activityTitle = ref('')
const activityLocation = ref('')
const activityStart = ref('')
const activityEnd = ref('')
const activityDeadline = ref('')
const activityCapacity = ref(0)
const activityDesc = ref('')

/**
 * @description 打开创建活动弹窗
 * @return 无
 */
function openActivity() {
  activityTitle.value = ''
  activityLocation.value = ''
  activityStart.value = ''
  activityEnd.value = ''
  activityDeadline.value = ''
  activityCapacity.value = 0
  activityDesc.value = ''
  activityOpen.value = true
}

/**
 * @description 提交创建活动
 * @return 无
 */
async function submitActivity() {
  if (!auth.isAuthed || !clubId.value) return
  if (activityTitle.value.trim().length < 2) {
    ui.toast('提示', '活动标题至少2字')
    return
  }
  if (activityLocation.value.trim().length < 2) {
    ui.toast('提示', '地点至少2字')
    return
  }
  if (!activityStart.value || !activityEnd.value) {
    ui.toast('提示', '请选择开始/结束时间')
    return
  }
  if (!activityDeadline.value) {
    ui.toast('提示', '请选择报名截止时间')
    return
  }
  if (activityDesc.value.trim().length < 10) {
    ui.toast('提示', '活动说明至少10字')
    return
  }
  activityLoading.value = true
  try {
    await apiActivityCreate({
      clubId: clubId.value,
      title: activityTitle.value.trim(),
      startTime: activityStart.value,
      endTime: activityEnd.value,
      place: activityLocation.value.trim(),
      signupDeadline: activityDeadline.value,
      signupLimit: activityCapacity.value || 0,
    })
    ui.toast('已发布', '活动已创建并发布')
    activityOpen.value = false
    await loadAll()
  } catch (e) {
    ui.toast('提交失败', e instanceof Error ? e.message : '提交失败')
  } finally {
    activityLoading.value = false
  }
}

const announcementOpen = ref(false)
const announcementLoading = ref(false)
const announcementTitle = ref('')
const announcementContent = ref('')

/**
 * @description 打开发布公告弹窗
 * @return 无
 */
function openAnnouncement() {
  announcementTitle.value = ''
  announcementContent.value = ''
  announcementOpen.value = true
}

/**
 * @description 提交发布公告
 * @return 无
 */
async function submitAnnouncement() {
  if (!auth.isAuthed || !clubId.value) return
  if (announcementTitle.value.trim().length < 2) {
    ui.toast('提示', '标题至少2字')
    return
  }
  if (announcementContent.value.trim().length < 10) {
    ui.toast('提示', '内容至少10字')
    return
  }
  announcementLoading.value = true
  try {
    await apiAnnouncementPublish({
      clubId: clubId.value,
      title: announcementTitle.value.trim(),
      content: announcementContent.value.trim(),
    })
    ui.toast('已发布', '公告已发布')
    announcementOpen.value = false
    await loadAll()
  } catch (e) {
    ui.toast('提交失败', e instanceof Error ? e.message : '提交失败')
  } finally {
    announcementLoading.value = false
  }
}

onMounted(loadAll)
</script>

<style scoped>
.navBtn {
  width: 100%;
  text-align: left;
  border-radius: 14px;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(229, 231, 235, 0.9);
  cursor: pointer;
}
.navBtn + .navBtn {
  margin-top: 10px;
}
.navBtn--active {
  background: rgba(97, 112, 190, 0.2);
  border-color: rgba(97, 112, 190, 0.28);
  color: #ffffff;
}
.head {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.head__sub {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
}
.head__title {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 800;
  color: #ffffff;
}
.kpis {
  margin-top: 14px;
  padding: 14px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
@media (max-width: 980px) {
  .kpis {
    grid-template-columns: 1fr;
  }
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
.kpi__value--warn {
  color: #fbbf24;
}
.card {
  margin-top: 14px;
  overflow: hidden;
}
.card__head {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}
.card__title {
  font-size: 14px;
  font-weight: 900;
  color: #ffffff;
}
.card__sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}
.tableWrap {
  padding: 16px;
  overflow-x: auto;
}
.table {
  width: 100%;
  border-collapse: collapse;
  min-width: 760px;
}
.table th {
  text-align: left;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
  font-weight: 700;
  padding-bottom: 10px;
}
.table td {
  padding: 10px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  vertical-align: top;
}
.right {
  text-align: right;
  white-space: nowrap;
}
.muted {
  color: rgba(229, 231, 235, 0.7);
  max-width: 420px;
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
.list {
  padding: 16px;
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
.item__title {
  font-weight: 900;
  color: #ffffff;
}
.item__row {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}
.item__desc {
  margin-top: 8px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  line-height: 1.7;
}
.empty {
  padding: 16px;
  text-align: center;
  color: rgba(229, 231, 235, 0.65);
}
.modal {
  display: grid;
  gap: 10px;
}
.modal__hint {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}
.modal__label {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  margin-bottom: 6px;
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
.textarea {
  min-height: 110px;
  resize: none;
}
</style>
