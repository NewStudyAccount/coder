<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <el-form :model="registerForm" @submit.prevent="handleRegister">
      <el-form-item label="用户名">
        <el-input v-model="registerForm.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleRegister">注册</el-button>
        <el-button @click="$router.push('/login')">去登录</el-button>
      </el-form-item>
    </el-form>
    <div v-if="msg" class="msg" :class="{ error: isError }">{{ msg }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

const router = useRouter()

const registerForm = ref({
  username: '',
  password: ''
})
const msg = ref('')
const isError = ref(false)

const handleRegister = async () => {
  try {
    const res = await register(registerForm.value)
    if (res.code === 0) {
        msg.value = '注册成功，即将跳转登录...'
        isError.value = false
        setTimeout(() => {
            router.push('/login')
        }, 1500)
    } else {
        msg.value = '注册失败: ' + res.msg
        isError.value = true
    }
  } catch (error) {
    msg.value = '注册请求失败'
    isError.value = true
  }
}
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}
.msg {
  margin-top: 10px;
  color: green;
}
.msg.error {
  color: red;
}
</style>
