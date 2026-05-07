<template>
  <div class="wrap">
    <div class="container">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style scoped>
.wrap {
  min-height: 100vh;
  padding: 40px 0;
  display: flex;
  align-items: center;
}
</style>

