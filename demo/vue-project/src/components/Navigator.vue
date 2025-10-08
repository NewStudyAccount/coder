<template>
  <div class="navigator">
    <el-avatar src="https://cdn.jsdelivr.net/gh/element-plus/element-plus@2.2.31/docs/public/images/element-plus-logo-small.svg" size="small" />
    <span class="username">{{ userStore.username }}</span>
    <router-link to="/home">
      <el-button type="default" size="small" style="margin-left: 20px;">首页</el-button>
    </router-link>
    <router-link to="/about">
      <el-button type="default" size="small" style="margin-left: 10px;">关于</el-button>
    </router-link>
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
  </div>
</template>

<script setup>
import { useUserStore } from '../store/user'
const userStore = useUserStore()

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
