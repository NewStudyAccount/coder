<template>
  <div class="navigator">
    <el-avatar src="https://cdn.jsdelivr.net/gh/element-plus/element-plus@2.2.31/docs/public/images/element-plus-logo-small.svg" size="small" />
    <span class="username">{{ userStore.username }}</span>
    <router-link to="/home">
      <el-button type="default" size="small" style="margin-left: 20px;">首页</el-button>
    </router-link>
    <el-button type="default" size="small" style="margin-left: 10px;" @click="aboutDialogVisible = true">关于</el-button>
    <router-link to="/roles">
      <el-button type="default" size="small" style="margin-left: 10px;">角色管理</el-button>
    </router-link>
    <router-link to="/user/1">
      <el-button type="default" size="small" style="margin-left: 10px;">用户1</el-button>
    </router-link>
    <router-link to="/user/abc">
      <el-button type="default" size="small" style="margin-left: 10px;">用户abc</el-button>
    </router-link>
    <el-button type="success" size="small" style="margin-left: 20px;" @click="userStore.increment">
      计数器: {{ userStore.count }}
    </el-button>
    <el-button type="primary" size="small" style="margin-left: 20px;" @click="userStore.setUsername('新用户')">
      切换用户名
    </el-button>
    <el-button type="primary" size="small" style="margin-left: 10px;" @click="ssoLogin">SSO登录</el-button>
    <el-button type="danger" size="small" style="margin-left: 10px;">退出</el-button>
    <el-switch
      v-model="isDark"
      style="margin-left: 20px;"
      active-text="深色"
      inactive-text="浅色"
      @change="toggleTheme"
    />
  </div>
  <el-dialog v-model="aboutDialogVisible" title="关于本项目" width="400px">
    <div>
      <p>本项目为前后端分离的权限管理系统，包含用户、角色、权限等功能。</p>
      <p>前端基于 Vue3 + Element Plus，后端基于 Spring Boot。</p>
      <p>开发者：AI智能助手</p>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '../store/user'
const userStore = useUserStore()

const aboutDialogVisible = ref(false)
const isDark = ref(false)

function toggleTheme() {
  if (isDark.value) {
    document.body.classList.add('dark-theme')
  } else {
    document.body.classList.remove('dark-theme')
  }
}

// 初始化时同步主题
watch(isDark, (val) => {
  toggleTheme()
})

function ssoLogin() {
  window.location.href = '/sso/login'
}
</script>

<style scoped>
.navigator {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}
.username {
  margin-left: 10px;
  font-size: 16px;
}
</style>
/* 可选：全局深色主题样式，实际项目可放到 base.css/main.css */
.dark-theme {
  background: #181818 !important;
  color: #e0e0e0 !important;
}
