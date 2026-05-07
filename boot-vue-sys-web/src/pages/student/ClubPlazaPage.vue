<template>
  <StudentLayout title="发现社团" hint="学生端 · 社团广场">
    <div class="glass searchBar">
      <div class="searchRow">
        <input v-model.trim="keyword" class="input" placeholder="搜索社团名称 / 关键词（如：摄影、公益、AI）" />
        <button class="btn" type="button" @click="keyword = ''">清空</button>
      </div>
      <div class="filterRow">
        <button
          v-for="t in types"
          :key="t"
          class="chip"
          :class="{ 'chip--active': type === t }"
          type="button"
          @click="type = t"
        >
          {{ t }}
        </button>
        <RouterLink class="btn btn--primary createBtn" to="/clubs/create">创建社团申请</RouterLink>
      </div>
    </div>

    <div class="grid">
      <main class="glass mainCard">
        <div class="mainHead">
          <div>
            <div class="mainTitle">社团列表</div>
            <div class="mainSub">共 {{ clubs.length }} 个社团</div>
          </div>
        </div>
        <div class="list">
          <RouterLink
            v-for="c in clubs"
            :key="c.id"
            class="club"
            :to="`/clubs/${c.id}`"
          >
            <div class="club__cover" :style="{ backgroundImage: c.coverUrl ? `url(${c.coverUrl})` : '' }">
              <div class="club__coverMask"></div>
            </div>
            <div class="club__body">
              <div class="club__row">
                <div class="club__name">{{ c.name }}</div>
                <span class="pill">{{ c.type }}</span>
              </div>
              <div class="club__intro">{{ c.intro }}</div>
              <div class="club__meta">
                <span class="pill" :class="c.recruitStatus === 'OPEN' ? 'pill--ok' : 'pill--warn'">
                  {{ c.recruitStatus === 'OPEN' ? '可加入' : '暂停招新' }}
                </span>
                <span v-if="c.recommended" class="pill">推荐</span>
              </div>
            </div>
          </RouterLink>
        </div>
      </main>

      <aside class="glass sideCard">
        <div class="sideHead">
          <div>
            <div class="sideSub">你的进度</div>
            <div class="sideTitle">我的社团旅程</div>
          </div>
          <div class="sideIcon"></div>
        </div>

        <div class="sideList">
          <div class="sideItem">
            <div class="sideItem__row">
              <div class="sideItem__label">待处理申请</div>
              <div class="sideItem__value">{{ pendingCount }}</div>
            </div>
            <div class="sideItem__hint" v-if="pendingHint">{{ pendingHint }}</div>
            <RouterLink class="sideLink" to="/me?tab=requests">查看我的申请</RouterLink>
          </div>

          <div class="sideItem">
            <div class="sideItem__row">
              <div class="sideItem__label">推荐活动</div>
              <div class="pill pill--ok">本周</div>
            </div>
            <div class="sideItem__hint">AI 学术社 · “从零训练一个小模型” 工作坊</div>
            <button class="btn" type="button" @click="ui.toast('已加入待办', '打开社团详情后可进行报名（演示）')">
              加入待办
            </button>
          </div>
        </div>

        <div class="sideNote">
          列表页仅做单表分页展示（避免多表关联），成员数/活动数可由后端汇总字段或异步聚合获得。
        </div>
      </aside>
    </div>
  </StudentLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import StudentLayout from '../../layouts/StudentLayout.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import { apiClubList } from '../../api/club'
import { apiMyJoinRequests } from '../../api/join'
import type { Club } from '../../types/domain'

const auth = useAuthStore()
const ui = useUiStore()

const types = ['全部', '学术', '文体', '公益', '兴趣'] as const
const type = ref<(typeof types)[number]>('全部')
const keyword = ref('')
const clubs = ref<Club[]>([])

const pendingCount = ref(0)
const pendingHint = ref('')

async function loadList() {
  try {
    const typeCode =
      type.value === '学术' ? 1 : type.value === '文体' ? 2 : type.value === '公益' ? 3 : type.value === '兴趣' ? 4 : undefined
    clubs.value = await apiClubList(keyword.value, typeCode)
  } catch (e) {
    ui.toast('加载失败', e instanceof Error ? e.message : '加载失败')
  }
}

async function loadMyProgress() {
  if (!auth.isAuthed) return
  try {
    const list = await apiMyJoinRequests()
    const count = list.filter(r => r.status === 'PENDING').length
    pendingCount.value = count
    const first = list.find(r => r.status === 'PENDING')
    if (first) {
      pendingHint.value = '有入社申请正在审核中'
    } else {
      pendingHint.value = ''
    }
  } catch {
    pendingCount.value = 0
    pendingHint.value = ''
  }
}

watch([keyword, type], loadList, { deep: false })

onMounted(async () => {
  await Promise.all([loadList(), loadMyProgress()])
})
</script>

<style scoped>
.searchBar {
  padding: 16px;
}

.searchRow {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: center;
}

.filterRow {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.createBtn {
  margin-left: auto;
}

.grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 18px;
  align-items: start;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .createBtn {
    margin-left: 0;
  }
}

.mainCard {
  overflow: hidden;
}

.mainHead {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.mainTitle {
  font-size: 14px;
  font-weight: 700;
  color: #ffffff;
}

.mainSub {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}

.list {
  padding: 16px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

@media (max-width: 820px) {
  .list {
    grid-template-columns: 1fr;
  }
}

.club {
  display: grid;
  grid-template-columns: 96px 1fr;
  gap: 12px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  overflow: hidden;
  transition: background 0.15s ease;
}

.club:hover {
  background: rgba(255, 255, 255, 0.09);
}

.club__cover {
  background-size: cover;
  background-position: center;
  position: relative;
  min-height: 96px;
  background-color: rgba(255, 255, 255, 0.08);
}

.club__coverMask {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.55), transparent);
}

.club__body {
  padding: 12px 12px 12px 0;
  min-width: 0;
}

.club__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.club__name {
  font-weight: 700;
  color: #ffffff;
  font-size: 14px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.club__intro {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.7);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.club__meta {
  margin-top: 8px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
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

.sideCard {
  padding: 16px;
}

.sideHead {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sideSub {
  font-size: 12px;
  font-weight: 600;
  color: rgba(229, 231, 235, 0.6);
}

.sideTitle {
  margin-top: 6px;
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
}

.sideIcon {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}

.sideList {
  margin-top: 14px;
  display: grid;
  gap: 12px;
}

.sideItem {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
  display: grid;
  gap: 10px;
}

.sideItem__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.sideItem__label {
  font-size: 13px;
  color: #e5e7eb;
}

.sideItem__value {
  font-size: 18px;
  font-weight: 800;
  color: #fbbf24;
}

.sideItem__hint {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
  line-height: 1.6;
}

.sideLink {
  font-size: 12px;
  color: rgba(167, 243, 208, 0.9);
}

.sideLink:hover {
  color: #ffffff;
}

.sideNote {
  margin-top: 14px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
  line-height: 1.6;
}
</style>
