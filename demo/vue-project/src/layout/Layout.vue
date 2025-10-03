<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background: #2d3a4b; color: #fff;">
      <div style="padding: 20px; font-size: 20px; text-align: center;">聊天室</div>
      <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" background-color="#2d3a4b" text-color="#fff" active-text-color="#ffd04b">
        <el-menu-item index="1">
          <router-link to="/home" style="color: inherit; text-decoration: none;">首页</router-link>
        </el-menu-item>
        <el-menu-item index="2">
          <router-link to="/users" style="color: inherit; text-decoration: none;">用户管理</router-link>
        </el-menu-item>
        <el-menu-item index="3">
          <router-link to="/permissions" style="color: inherit; text-decoration: none;">权限管理</router-link>
        </el-menu-item>
        <el-menu-item index="4">聊天室</el-menu-item>
        <el-menu-item index="5">设置</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: #409EFF; color: #fff; text-align: right; padding: 0 30px;">
        <Navigator />
      </el-header>
      <el-main style="background: #f5f5f5;">
        <slot />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import Navigator from '../components/Navigator.vue'
const activeMenu = ref('2')
</script>

<style scoped>
.el-header {
  height: 60px;
  line-height: 60px;
  font-size: 18px;
}
</style>