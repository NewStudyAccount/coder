<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 应用初始化时检查登录状态
onMounted(() => {
  userStore.checkLoginStatus()
})
</script>

<style>
/* 全局样式已在 main.css 中定义 */
</style>
