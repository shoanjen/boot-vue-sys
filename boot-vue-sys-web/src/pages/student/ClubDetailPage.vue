<template>
  <StudentLayout title="社团详情" hint="学生端 · 社团详情">
    <div v-if="loading" class="glass loading">加载中...</div>

    <template v-else>
      <header class="glass hero" v-if="detail">
        <div class="hero__cover" :style="{ backgroundImage: detail.club.coverUrl ? `url(${detail.club.coverUrl})` : '' }">
          <div class="hero__mask"></div>
          <div class="hero__top">
            <RouterLink class="btn" to="/clubs">返回广场</RouterLink>
            <RouterLink class="btn" to="/me">我的</RouterLink>
          </div>
          <div class="hero__bottom">
            <div class="hero__info">
              <div class="hero__chips">
                <span class="pill">{{ detail.club.type }}</span>
                <span class="pill" :class="detail.club.recruitStatus === 'OPEN' ? 'pill--ok' : 'pill--warn'">
                  {{ detail.club.recruitStatus === 'OPEN' ? '可加入' : '暂停招新' }}
                </span>
                <span v-if="detail.club.recommended" class="pill">推荐</span>
              </div>
              <div class="hero__name">{{ detail.club.name }}</div>
              <div class="hero__intro">{{ detail.club.intro }}</div>
            </div>
            <div class="hero__actions">
              <button class="btn" type="button" @click="ui.toast('已收藏', '已加入收藏（演示）')">收藏</button>
              <button
                class="btn btn--primary"
                :disabled="detail.club.recruitStatus !== 'OPEN' || isMember"
                type="button"
                @click="openJoin"
              >
                {{ isMember ? '已加入' : '申请加入' }}
              </button>
            </div>
          </div>
        </div>
      </header>

      <div v-if="detail" class="grid">
        <main class="glass mainCard">
          <div class="tabs">
            <button class="chip" :class="{ 'chip--active': tab === 'activity' }" type="button" @click="tab = 'activity'">活动</button>
            <button class="chip" :class="{ 'chip--active': tab === 'announcement' }" type="button" @click="tab = 'announcement'">公告</button>
            <button class="chip" :class="{ 'chip--active': tab === 'about' }" type="button" @click="tab = 'about'">社团介绍</button>
            <div class="tabs__meta">
              <span class="pill">成员：{{ detail.members.length }}</span>
              <span class="pill">活动：{{ detail.activities.length }}</span>
            </div>
          </div>

          <section v-if="tab === 'activity'" class="section">
            <div class="section__head">
              <div>
                <div class="section__title">近期活动</div>
                <div class="section__sub">报名支持：人数上限 + 截止时间校验</div>
              </div>
              <button class="btn" type="button" @click="ui.toast('筛选已应用', '当前默认按时间倒序展示（演示）')">
                筛选
              </button>
            </div>

            <div class="items">
              <article v-for="a in detail.activities" :key="a.id" class="item">
                <div class="item__top">
                  <div class="item__chips">
                    <span class="pill pill--brand">{{ a.category }}</span>
                    <span class="pill pill--ok">{{ remainHint(a.id) }}</span>
                    <span class="pill">报名截止：{{ formatShort(a.deadlineAt) }}</span>
                  </div>
                  <div class="item__title">{{ a.title }}</div>
                  <div class="item__desc">
                    时间：{{ formatRange(a.startAt, a.endAt) }} · 地点：{{ a.location }}
                  </div>
                </div>
                <div class="item__actions">
                  <button class="btn" type="button" @click="ui.toast('已加入待办', '可在“我的报名/待办”查看（演示）')">待办</button>
                  <button class="btn btn--primary" type="button" @click="openSignup(a.id)">报名</button>
                </div>
              </article>
              <div v-if="detail.activities.length === 0" class="empty">暂无活动</div>
            </div>
          </section>

          <section v-else-if="tab === 'announcement'" class="section">
            <div class="section__head">
              <div>
                <div class="section__title">社团公告</div>
                <div class="section__sub">MVP：公告发布与阅读；已读状态为 P1</div>
              </div>
              <button class="btn" type="button" @click="ui.toast('已标记', '已读状态为P1能力，后续可扩展（演示）')">
                标记已读（P1）
              </button>
            </div>
            <div class="items">
              <article v-for="n in detail.announcements" :key="n.id" class="item">
                <div class="item__top">
                  <div class="item__row">
                    <div class="item__title">{{ n.title }}</div>
                    <span class="pill">{{ formatShort(n.createdAt) }}</span>
                  </div>
                  <div class="item__desc">{{ n.content }}</div>
                </div>
                <div class="item__actions">
                  <button class="btn" type="button" @click="openAnnouncement(n.id)">查看详情</button>
                </div>
              </article>
              <div v-if="detail.announcements.length === 0" class="empty">暂无公告</div>
            </div>
          </section>

          <section v-else class="section">
            <div class="section__title">社团介绍</div>
            <div class="about">
              <div class="about__row">
                <div class="about__label">联系方式</div>
                <div class="about__value">{{ detail.club.contact }}</div>
              </div>
              <div class="about__row" v-if="detail.club.advisor">
                <div class="about__label">指导老师</div>
                <div class="about__value">{{ detail.club.advisor }}</div>
              </div>
              <div class="about__row">
                <div class="about__label">简介</div>
                <div class="about__value">{{ detail.club.intro }}</div>
              </div>
            </div>
          </section>
        </main>

        <aside class="glass sideCard">
          <div class="sideTitle">提示</div>
          <div class="sideText">
            非成员无法报名活动；社团暂停招新或下架时，会隐藏入社与报名入口（MVP）。
          </div>
        </aside>
      </div>
    </template>

    <AppModal :open="joinOpen" title="入社申请" @close="joinOpen = false">
      <div class="modalForm">
        <div class="modalLabel">申请理由（10-200字）</div>
        <textarea v-model.trim="joinReason" class="input textarea" placeholder="请输入加入动机与自我介绍"></textarea>
      </div>
      <template #footer>
        <button class="btn" type="button" @click="joinOpen = false">取消</button>
        <button class="btn btn--primary" type="button" :disabled="joinLoading" @click="submitJoin">
          {{ joinLoading ? '提交中...' : '提交申请' }}
        </button>
      </template>
    </AppModal>

    <AppModal :open="signupOpen" title="活动报名" @close="signupOpen = false">
      <div class="modalForm">
        <div class="modalText">确认报名该活动？系统将校验成员身份、名额上限与截止时间。</div>
      </div>
      <template #footer>
        <button class="btn" type="button" @click="signupOpen = false">取消</button>
        <button class="btn btn--primary" type="button" :disabled="signupLoading" @click="submitSignup">
          {{ signupLoading ? '提交中...' : '确认报名' }}
        </button>
      </template>
    </AppModal>

    <AppModal :open="announcementOpen" title="公告详情" @close="announcementOpen = false">
      <div v-if="activeAnnouncement" class="modalForm">
        <div class="modalTitle">{{ activeAnnouncement.title }}</div>
        <div class="modalTime">{{ formatShort(activeAnnouncement.createdAt) }}</div>
        <div class="modalText modalText--block">{{ activeAnnouncement.content }}</div>
      </div>
      <template #footer>
        <button class="btn btn--primary" type="button" @click="announcementOpen = false">关闭</button>
      </template>
    </AppModal>
  </StudentLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import StudentLayout from '../../layouts/StudentLayout.vue'
import AppModal from '../../components/AppModal.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import { apiClubDetail } from '../../api/club'
import { apiActivityList, apiActivitySignup } from '../../api/activity'
import { apiAnnouncementList } from '../../api/announcement'
import { apiJoinApply } from '../../api/join'
import { apiMyCenter } from '../../api/me'
import type { Announcement, Activity, Club, ClubMember } from '../../types/domain'

const route = useRoute()
const auth = useAuthStore()
const ui = useUiStore()

const tab = ref<'activity' | 'announcement' | 'about'>('activity')
const loading = ref(true)
const detail = ref<null | { club: Club; members: ClubMember[]; activities: Activity[]; announcements: Announcement[] }>(null)
const memberClubIds = ref<Set<string>>(new Set())

const joinOpen = ref(false)
const joinReason = ref('')
const joinLoading = ref(false)

const signupOpen = ref(false)
const signupLoading = ref(false)
const signupActivityId = ref<string>('')

const announcementOpen = ref(false)
const announcementId = ref<string>('')

const isMember = computed(() => {
  if (!auth.isAuthed) return false
  const clubId = (route.params.clubId as string) || ''
  return memberClubIds.value.has(clubId)
})

const activeAnnouncement = computed<Announcement | null>(() => {
  if (!detail.value) return null
  return detail.value.announcements.find(n => n.id === announcementId.value) || null
})

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

function remainHint(activityId: string) {
  const a = detail.value?.activities.find(x => x.id === activityId)
  if (!a) return '可报名'
  if (a.capacity <= 0) return '不限名额'
  return `名额上限 ${a.capacity}`
}

function openJoin() {
  joinReason.value = ''
  joinOpen.value = true
}

async function submitJoin() {
  if (!auth.isAuthed || !detail.value) return
  if (joinReason.value.trim().length < 10) {
    ui.toast('提示', '申请理由至少10字')
    return
  }
  joinLoading.value = true
  try {
    await apiJoinApply(detail.value.club.id, joinReason.value.trim())
    ui.toast('已提交', '入社申请已提交，请等待审核')
    joinOpen.value = false
  } catch (e) {
    ui.toast('提交失败', e instanceof Error ? e.message : '提交失败')
  } finally {
    joinLoading.value = false
  }
}

function openSignup(activityId: string) {
  signupActivityId.value = activityId
  signupOpen.value = true
}

async function submitSignup() {
  if (!auth.isAuthed) return
  if (!signupActivityId.value) return
  signupLoading.value = true
  try {
    await apiActivitySignup(signupActivityId.value)
    ui.toast('报名成功', '你已报名该活动')
    signupOpen.value = false
  } catch (e) {
    ui.toast('报名失败', e instanceof Error ? e.message : '报名失败')
  } finally {
    signupLoading.value = false
  }
}

function openAnnouncement(id: string) {
  announcementId.value = id
  announcementOpen.value = true
}

async function loadDetail() {
  loading.value = true
  try {
    const clubId = route.params.clubId as string
    const [club, activities, announcements] = await Promise.all([
      apiClubDetail(clubId),
      apiActivityList(clubId),
      apiAnnouncementList(clubId),
    ])
    detail.value = { club, members: [], activities, announcements }
    if (auth.isAuthed) {
      const me = await apiMyCenter()
      memberClubIds.value = new Set(me.myClubs.map(x => x.club.id))
    } else {
      memberClubIds.value = new Set()
    }
  } catch (e) {
    ui.toast('加载失败', e instanceof Error ? e.message : '加载失败')
    detail.value = null
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<style scoped>
.loading {
  padding: 16px;
}

.hero {
  overflow: hidden;
}

.hero__cover {
  position: relative;
  min-height: 220px;
  background-color: rgba(255, 255, 255, 0.08);
  background-size: cover;
  background-position: center;
}

.hero__mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(9, 10, 14, 0.94), rgba(9, 10, 14, 0.3), transparent);
}

.hero__top {
  position: absolute;
  left: 16px;
  top: 16px;
  display: flex;
  gap: 10px;
}

.hero__bottom {
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: 16px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 14px;
}

.hero__info {
  min-width: 0;
}

.hero__chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.hero__name {
  margin-top: 10px;
  font-size: 22px;
  font-weight: 800;
  color: #ffffff;
}

.hero__intro {
  margin-top: 8px;
  font-size: 13px;
  color: rgba(229, 231, 235, 0.75);
  line-height: 1.6;
}

.hero__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: 1fr 320px;
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
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.tabs__meta {
  margin-left: auto;
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
  font-weight: 800;
  color: #ffffff;
}

.section__sub {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}

.items {
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
}

@media (max-width: 820px) {
  .item {
    flex-direction: column;
    align-items: stretch;
  }
  .item__actions {
    justify-content: flex-end;
  }
}

.item__top {
  min-width: 0;
}

.item__chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.item__title {
  margin-top: 8px;
  font-size: 14px;
  font-weight: 800;
  color: #ffffff;
}

.item__row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.item__desc {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  line-height: 1.6;
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

.sideCard {
  padding: 16px;
}

.sideTitle {
  font-size: 14px;
  font-weight: 800;
  color: #ffffff;
}

.sideText {
  margin-top: 10px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  line-height: 1.7;
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

.pill--brand {
  background: rgba(97, 112, 190, 0.2);
  border-color: rgba(97, 112, 190, 0.28);
  color: #e0e7ff;
}

.modalForm {
  display: grid;
  gap: 10px;
}

.modalLabel {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.75);
}

.textarea {
  min-height: 110px;
  resize: none;
}

.modalTitle {
  font-size: 16px;
  font-weight: 800;
  color: #ffffff;
}

.modalTime {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
}

.modalText {
  font-size: 13px;
  color: rgba(229, 231, 235, 0.75);
  line-height: 1.7;
}

.modalText--block {
  white-space: pre-wrap;
}
</style>
