<template>
  <StudentLayout title="创建社团申请" hint="学生端 · 社团入驻">
    <div class="grid">
      <main class="glass card">
        <div class="head">
          <div>
            <div class="title">申请表单</div>
            <div class="sub">提交后进入待审核，审核结果会通过站内通知反馈（演示环境）</div>
          </div>
          <span class="tag">模块：/api/v1/club/...</span>
        </div>

        <form class="form" @submit.prevent="handleSubmit">
          <div>
            <label class="label">社团名称（必填，2-30字，唯一）</label>
            <input v-model.trim="name" class="input" placeholder="如：校园手作社" />
          </div>

          <div class="row2">
            <div>
              <label class="label">社团类型（必填）</label>
              <select v-model="type" class="input">
                <option>学术</option>
                <option>文体</option>
                <option>公益</option>
                <option>兴趣</option>
              </select>
            </div>
            <div>
              <label class="label">指导老师（可选）</label>
              <input v-model.trim="advisor" class="input" placeholder="如：王老师" />
            </div>
          </div>

          <div>
            <label class="label">社团简介（必填，10-500字）</label>
            <textarea
              v-model.trim="intro"
              class="input textarea"
              placeholder="请描述社团定位、活动形式、招新对象等"
            ></textarea>
          </div>

          <div>
            <label class="label">联系方式（必填，最长50字）</label>
            <input v-model.trim="contact" class="input" placeholder="手机号 / 邮箱 / 微信号" />
          </div>

          <div class="p1">
            <div class="p1__head">
              <div>
                <div class="p1__title">证明材料（P1）</div>
                <div class="p1__sub">MVP 可先不做上传，后续迭代引入图片/文件</div>
              </div>
              <span class="p1__tag">P1</span>
            </div>
            <div class="p1__row">
              <div class="p1__hint">上传功能暂不可用（演示）</div>
              <button class="btn" type="button" disabled>选择文件</button>
            </div>
          </div>

          <div class="actions">
            <button class="btn" type="button" @click="resetForm">重置</button>
            <button class="btn btn--primary" :disabled="loading" type="submit">
              {{ loading ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </form>
      </main>

      <aside class="glass side">
        <div class="sideHead">
          <div>
            <div class="sideSub">审核流程</div>
            <div class="sideTitle">平台审核</div>
          </div>
          <div class="sideIcon"></div>
        </div>

        <div class="steps">
          <div class="step">
            <div class="step__num step__num--ok">1</div>
            <div>
              <div class="step__title">提交资料</div>
              <div class="step__desc">名称、类型、简介、联系方式等</div>
            </div>
          </div>
          <div class="step">
            <div class="step__num">2</div>
            <div>
              <div class="step__title">平台审核</div>
              <div class="step__desc">通过 / 驳回（填写原因）</div>
            </div>
          </div>
          <div class="step">
            <div class="step__num step__num--brand">3</div>
            <div>
              <div class="step__title">结果通知</div>
              <div class="step__desc">站内通知告知结果，可再次提交</div>
            </div>
          </div>
        </div>

        <div class="note">
          提交后，审核通过时申请人会自动成为社团管理员。
          <RouterLink class="note__link" to="/admin/platform">查看平台后台演示</RouterLink>
        </div>
      </aside>
    </div>
  </StudentLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import StudentLayout from '../../layouts/StudentLayout.vue'
import { useAuthStore } from '../../stores/auth'
import { useUiStore } from '../../stores/ui'
import { apiApplyCreateClub } from '../../api/club'
import type { ClubType } from '../../types/domain'

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()

const name = ref('校园手作社')
const type = ref<ClubType>('兴趣')
const advisor = ref('')
const intro = ref('手工制作、材料分享、作品展出与交流活动。欢迎喜欢手作的同学一起玩。')
const contact = ref('handmade-club@school.edu')
const loading = ref(false)

/**
 * @description 重置表单
 * @return 无
 */
function resetForm() {
  name.value = ''
  type.value = '兴趣'
  advisor.value = ''
  intro.value = ''
  contact.value = ''
}

/**
 * @description 提交创建社团申请
 * @return 无
 */
async function handleSubmit() {
  if (!auth.user?.id) return
  if (name.value.trim().length < 2 || name.value.trim().length > 30) {
    ui.toast('提示', '社团名称需2-30字')
    return
  }
  if (intro.value.trim().length < 10 || intro.value.trim().length > 500) {
    ui.toast('提示', '社团简介需10-500字')
    return
  }
  if (!contact.value.trim() || contact.value.trim().length > 50) {
    ui.toast('提示', '联系方式必填且最长50字')
    return
  }

  loading.value = true
  try {
    const typeCode = type.value === '学术' ? 1 : type.value === '文体' ? 2 : type.value === '公益' ? 3 : 4
    await apiApplyCreateClub({
      name: name.value.trim(),
      type: typeCode,
      intro: intro.value.trim(),
      advisorName: advisor.value.trim() || undefined,
      contact: contact.value.trim(),
    })
    ui.toast('提交成功', '已提交创建社团申请，请等待平台审核')
    router.replace('/me?tab=requests')
  } catch (e) {
    ui.toast('提交失败', e instanceof Error ? e.message : '提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 18px;
  align-items: start;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.card {
  overflow: hidden;
}

.head {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.title {
  font-size: 14px;
  font-weight: 800;
  color: #ffffff;
}

.sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
  line-height: 1.6;
}

.tag {
  padding: 8px 10px;
  font-size: 12px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}

.form {
  padding: 16px;
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

.textarea {
  min-height: 128px;
  resize: none;
}

.p1 {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
}

.p1__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.p1__title {
  font-size: 14px;
  font-weight: 800;
  color: #ffffff;
}

.p1__sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
  line-height: 1.6;
}

.p1__tag {
  padding: 5px 8px;
  border-radius: 12px;
  font-size: 12px;
  background: rgba(251, 191, 36, 0.14);
  border: 1px solid rgba(251, 191, 36, 0.22);
  color: #fef3c7;
}

.p1__row {
  margin-top: 12px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.p1__hint {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.6);
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.side {
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
  font-weight: 800;
  color: #ffffff;
}

.sideIcon {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
}

.steps {
  margin-top: 14px;
  display: grid;
  gap: 12px;
}

.step {
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
  display: grid;
  grid-template-columns: 34px 1fr;
  gap: 12px;
}

.step__num {
  width: 34px;
  height: 34px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #ffffff;
}

.step__num--ok {
  background: rgba(52, 211, 153, 0.14);
  border-color: rgba(52, 211, 153, 0.22);
  color: #dcfce7;
}

.step__num--brand {
  background: rgba(97, 112, 190, 0.2);
  border-color: rgba(97, 112, 190, 0.28);
  color: #e0e7ff;
}

.step__title {
  font-size: 14px;
  font-weight: 800;
  color: #ffffff;
}

.step__desc {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
  line-height: 1.6;
}

.note {
  margin-top: 14px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  padding: 14px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
  line-height: 1.6;
}

.note__link {
  margin-left: 6px;
  color: rgba(167, 243, 208, 0.9);
}

.note__link:hover {
  color: #ffffff;
}
</style>
