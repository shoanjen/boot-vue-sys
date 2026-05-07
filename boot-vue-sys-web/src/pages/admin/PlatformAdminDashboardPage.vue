<template>
  <AdminLayout side-title="平台管理员后台" side-sub="社团创建审核">
    <template #nav>
      <button class="navBtn navBtn--active" type="button">社团审核</button>
      <button class="navBtn" type="button" @click="handleLogout">退出</button>
    </template>

    <header class="glass head">
      <div>
        <div class="head__sub">平台管理员后台</div>
        <div class="head__title">社团创建审核</div>
      </div>
      <div class="head__actions">
        <button class="btn" type="button" @click="loadAll">刷新</button>
      </div>
    </header>

    <section class="glass kpis">
      <div class="kpi">
        <div class="kpi__label">待审核</div>
        <div class="kpi__value kpi__value--warn">{{ pending.length }}</div>
      </div>
      <div class="kpi">
        <div class="kpi__label">今日通过</div>
        <div class="kpi__value">{{ approvedToday }}</div>
      </div>
      <div class="kpi">
        <div class="kpi__label">今日驳回</div>
        <div class="kpi__value">{{ rejectedToday }}</div>
      </div>
    </section>

    <section class="glass card">
      <div class="card__head">
        <div>
          <div class="card__title">待审核列表</div>
          <div class="card__sub">通过/驳回需记录原因，并可追溯（演示环境）</div>
        </div>
        <RouterLink class="btn" to="/clubs/create">学生提交入口</RouterLink>
      </div>

      <div class="tableWrap">
        <table class="table">
          <thead>
            <tr>
              <th>社团</th>
              <th>类型</th>
              <th>申请人</th>
              <th>提交时间</th>
              <th>状态</th>
              <th class="right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in pending" :key="c.id">
              <td>{{ c.name }}</td>
              <td class="muted">{{ c.type }}</td>
              <td class="muted">{{ userName(c.createdByUserId) }}</td>
              <td class="muted">{{ formatShort(c.createdAt) }}</td>
              <td><span class="pill pill--warn">待审核</span></td>
              <td class="right">
                <button class="btn btn--primary" type="button" @click="openReview(c.id, 'APPROVE')">通过</button>
                <button class="btn btn--warn" type="button" @click="openReview(c.id, 'REJECT')">驳回</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="pending.length === 0" class="empty">暂无待审核社团</div>
      </div>
    </section>

    <AppModal :open="reviewOpen" title="审核详情" @close="closeReview">
      <div v-if="activeClub" class="modal">
        <div class="grid2">
          <div class="box">
            <div class="box__label">社团名称</div>
            <div class="box__value">{{ activeClub.name }}</div>
            <div class="box__label box__label--mt">类型</div>
            <div class="box__value">{{ activeClub.type }}</div>
          </div>
          <div class="box">
            <div class="box__label">申请人</div>
            <div class="box__value">{{ userName(activeClub.createdByUserId) }}</div>
            <div class="box__label box__label--mt">联系方式</div>
            <div class="box__value">{{ activeClub.contact }}</div>
          </div>
        </div>
        <div class="box box--full">
          <div class="box__label">社团简介</div>
          <div class="box__text">{{ activeClub.intro }}</div>
        </div>
        <div>
          <div class="modal__label">驳回原因（驳回时必填）</div>
          <textarea v-model.trim="rejectReason" class="input textarea" placeholder="如：简介过短 / 联系方式不清晰"></textarea>
        </div>
      </div>
      <template #footer>
        <button class="btn" type="button" @click="closeReview">取消</button>
        <button class="btn btn--warn" type="button" :disabled="reviewLoading" @click="submitReview('REJECT')">
          {{ reviewLoading ? '提交中...' : '驳回' }}
        </button>
        <button class="btn btn--primary" type="button" :disabled="reviewLoading" @click="submitReview('APPROVE')">
          {{ reviewLoading ? '提交中...' : '通过' }}
        </button>
      </template>
    </AppModal>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import AdminLayout from '../../layouts/AdminLayout.vue'
import AppModal from '../../components/AppModal.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import { apiPlatformPendingClubs, apiPlatformReviewClub } from '../../api/platform'
import type { Club } from '../../types/domain'

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()

const pending = ref<Club[]>([])
const approvedToday = ref(0)
const rejectedToday = ref(0)

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

/**
 * @description 拉取平台审核数据
 * @return 无
 */
async function loadAll() {
  try {
    pending.value = await apiPlatformPendingClubs()
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
const activeClubId = ref('')
const rejectReason = ref('')

const activeClub = computed(() => pending.value.find(c => c.id === activeClubId.value) || null)

/**
 * @param clubId 社团ID
 * @param action 审核动作
 * @description 打开审核弹窗
 * @return 无
 */
function openReview(clubId: string, action: 'APPROVE' | 'REJECT') {
  activeClubId.value = clubId
  rejectReason.value = ''
  reviewOpen.value = true
  if (action === 'REJECT') {
    window.setTimeout(() => {
      const el = document.querySelector('textarea') as HTMLTextAreaElement | null
      el?.focus()
    }, 0)
  }
}

/**
 * @description 关闭审核弹窗
 * @return 无
 */
function closeReview() {
  reviewOpen.value = false
}

/**
 * @param action 审核动作
 * @description 提交审核结果
 * @return 无
 */
async function submitReview(action: 'APPROVE' | 'REJECT') {
  if (!auth.user?.id) return
  if (!activeClubId.value) return
  if (action === 'REJECT' && !rejectReason.value.trim()) {
    ui.toast('提示', '请填写驳回原因')
    return
  }
  reviewLoading.value = true
  try {
    await apiPlatformReviewClub(activeClubId.value, action === 'APPROVE', rejectReason.value.trim() || undefined)
    if (action === 'APPROVE') approvedToday.value += 1
    else rejectedToday.value += 1
    ui.toast('已处理', action === 'APPROVE' ? '已通过审核' : '已驳回审核')
    closeReview()
    await loadAll()
  } catch (e) {
    ui.toast('提交失败', e instanceof Error ? e.message : '提交失败')
  } finally {
    reviewLoading.value = false
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
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
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
  min-width: 860px;
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
.pill--warn {
  background: rgba(251, 191, 36, 0.14);
  border-color: rgba(251, 191, 36, 0.22);
  color: #fef3c7;
}
.pill--ok {
  background: rgba(52, 211, 153, 0.14);
  border-color: rgba(52, 211, 153, 0.22);
  color: #dcfce7;
}
.pill--danger {
  background: rgba(248, 113, 113, 0.14);
  border-color: rgba(248, 113, 113, 0.22);
  color: #fee2e2;
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
.modal__hint,
.modal__label {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}
.grid2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
@media (max-width: 820px) {
  .grid2 {
    grid-template-columns: 1fr;
  }
}
.box {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
}
.box--full {
  grid-column: 1 / -1;
}
.box__label {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
}
.box__label--mt {
  margin-top: 10px;
}
.box__value {
  margin-top: 6px;
  font-weight: 800;
  color: #ffffff;
}
.box__text {
  margin-top: 8px;
  color: rgba(229, 231, 235, 0.75);
  line-height: 1.7;
  font-size: 13px;
}
.textarea {
  min-height: 110px;
  resize: none;
}
</style>
