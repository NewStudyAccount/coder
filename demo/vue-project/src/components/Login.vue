<template>
  <div class="login-container">
    <h2>用户登录</h2>
    <el-form :model="loginForm" @submit.prevent="handleLogin">
      <el-form-item label="用户名">
        <el-input v-model="loginForm.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button @click="$router.push('/register')">去注册</el-button>
      </el-form-item>
    </el-form>
    <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: '',
  password: ''
})
const errorMsg = ref('')

const handleLogin = async () => {
  try {
    await userStore.login(loginForm.value)
    router.push('/dashboard') // 登录成功后跳转到仪表盘或首页
  } catch (error) {
    errorMsg.value = '登录失败，请检查用户名或密码'
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}
.error-msg {
  color: red;
  margin-top: 10px;
}
</style>
