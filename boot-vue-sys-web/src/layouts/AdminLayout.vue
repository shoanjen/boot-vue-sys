<template>
  <div class="wrap">
    <div class="container grid">
      <aside class="glass side">
        <div class="side__top">
          <div class="side__title">{{ sideTitle }}</div>
          <div class="side__sub">{{ sideSub }}</div>
        </div>
        <nav class="side__nav">
          <slot name="nav" />
        </nav>
        <div class="side__bottom">
          <RouterLink class="btn" to="/clubs">返回社团广场</RouterLink>
        </div>
      </aside>

      <main class="main">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'

defineProps<{
  sideTitle: string
  sideSub: string
}>()
</script>

<style scoped>
.wrap {
  min-height: 100vh;
  padding: 24px 0 40px;
}

.grid {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 18px;
  align-items: start;
}

@media (max-width: 980px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.side {
  overflow: hidden;
}

.side__top {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.side__title {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
}

.side__sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(229, 231, 235, 0.65);
}

.side__nav {
  padding: 10px;
}

.side__bottom {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
}

.main {
  min-width: 0;
}
</style>

