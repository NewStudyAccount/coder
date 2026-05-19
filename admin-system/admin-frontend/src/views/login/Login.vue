<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">后台管理系统</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider v-if="oauth2Providers.length > 0">第三方登录</el-divider>

      <div class="oauth2-buttons" v-if="oauth2Providers.length > 0">
        <el-button
          v-for="provider in oauth2Providers"
          :key="provider.name"
          class="oauth2-btn"
          :class="getProviderBtnClass(provider.name)"
          @click="handleOauth2Login(provider.name)"
        >
          <component :is="getProviderIcon(provider.name)" v-if="getProviderIcon(provider.name) !== 'span'" style="margin-right: 6px" />
          {{ provider.displayName }} 登录
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { login } from '@/api/auth'
import { getOauth2AuthorizeUrl, getAvailableProviders } from '@/api/oauth2'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import type { Oauth2ProviderInfo } from '@/types/user'

const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const oauth2Providers = ref<Oauth2ProviderInfo[]>([])

const loginForm = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.userInfo)
        permissionStore.clearMenus()
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleOauth2Login = async (provider: string) => {
  try {
    const res = await getOauth2AuthorizeUrl(provider)
    window.location.href = res.data.authorizeUrl
  } catch (error) {
    ElMessage.error('获取授权地址失败')
    console.error(error)
  }
}

const getProviderBtnClass = (name: string): string => {
  const classMap: Record<string, string> = {
    github: 'btn-github',
    google: 'btn-google',
    wechat: 'btn-wechat',
    dingtalk: 'btn-dingtalk',
    feishu: 'btn-feishu',
    gitee: 'btn-gitee',
    microsoft: 'btn-microsoft'
  }
  return classMap[name] || 'btn-default'
}

const getProviderIcon = (name: string) => {
  return 'span'
}

const loadProviders = async () => {
  try {
    const res = await getAvailableProviders()
    oauth2Providers.value = res.data || []
  } catch {
    oauth2Providers.value = []
  }
}

onMounted(() => {
  loadProviders()
})
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  padding: 20px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
  font-size: 24px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
}

.oauth2-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.oauth2-btn {
  min-width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-github {
  background-color: #24292e;
  border-color: #24292e;
  color: #fff;
}
.btn-github:hover {
  background-color: #2f363d;
  border-color: #2f363d;
}

.btn-google {
  background-color: #fff;
  border-color: #dadce0;
  color: #3c4043;
}
.btn-google:hover {
  background-color: #f8f9fa;
  border-color: #dadce0;
}

.btn-wechat {
  background-color: #07c160;
  border-color: #07c160;
  color: #fff;
}
.btn-wechat:hover {
  background-color: #06ad56;
  border-color: #06ad56;
}

.btn-dingtalk {
  background-color: #0089ff;
  border-color: #0089ff;
  color: #fff;
}
.btn-dingtalk:hover {
  background-color: #007ae6;
  border-color: #007ae6;
}

.btn-feishu {
  background-color: #3370ff;
  border-color: #3370ff;
  color: #fff;
}
.btn-feishu:hover {
  background-color: #2a5fe6;
  border-color: #2a5fe6;
}

.btn-gitee {
  background-color: #c71d23;
  border-color: #c71d23;
  color: #fff;
}
.btn-gitee:hover {
  background-color: #b01a1f;
  border-color: #b01a1f;
}

.btn-microsoft {
  background-color: #00a4ef;
  border-color: #00a4ef;
  color: #fff;
}
.btn-microsoft:hover {
  background-color: #0093d6;
  border-color: #0093d6;
}

.btn-default {
  background-color: #409EFF;
  border-color: #409EFF;
  color: #fff;
}
.btn-default:hover {
  background-color: #337ecc;
  border-color: #337ecc;
}
</style>
