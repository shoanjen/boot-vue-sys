<template>
  <div class="wrap">
    <header class="glass header">
      <div class="header__left">
        <div class="logo"></div>
        <div>
          <div class="hint">{{ hint }}</div>
          <div class="title">{{ title }}</div>
        </div>
      </div>
      <div class="header__right">
        <RouterLink class="btn" to="/me">我的</RouterLink>
        <RouterLink v-if="auth.role === 'clubAdmin'" class="btn" to="/admin/club"
          >社团后台</RouterLink
        >
        <RouterLink
          v-if="auth.role === 'platformAdmin'"
          class="btn"
          to="/admin/platform"
          >平台后台</RouterLink
        >
        <button class="btn" type="button" @click="handleLogout">退出</button>
      </div>
    </header>

    <main class="container main">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const props = defineProps<{
  title: string
  hint: string
}>()

const auth = useAuthStore()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.replace('/login')
}
</script>

<style scoped>
.wrap {
  min-height: 100vh;
  padding: 24px 0 40px;
}

.header {
  width: min(1120px, calc(100vw - 48px));
  margin: 0 auto;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.header__left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.logo {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.hint {
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
}

.header__right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.main {
  margin-top: 18px;
}
</style>

