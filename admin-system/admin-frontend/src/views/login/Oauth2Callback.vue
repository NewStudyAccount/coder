<template>
  <div class="oauth2-callback">
    <el-card class="callback-card">
      <div v-if="loading" class="callback-content">
        <el-icon class="loading-icon" :size="40"><Loading /></el-icon>
        <p>正在处理第三方登录...</p>
      </div>
      <div v-else-if="errorMessage" class="callback-content">
        <el-icon :size="40" style="color: #F56C6C"><CircleCloseFilled /></el-icon>
        <p class="error-text">{{ errorMessage }}</p>
        <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const loading = ref(true)
const errorMessage = ref('')

onMounted(() => {
  const token = route.query.token as string
  const userInfoStr = route.query.userInfo as string
  const errorStr = route.query.error as string

  if (errorStr) {
    try {
      errorMessage.value = decodeURIComponent(atob(errorStr))
    } catch {
      errorMessage.value = errorStr
    }
    loading.value = false
    return
  }

  if (token && userInfoStr) {
    try {
      let userInfo: any
      try {
        const decoded = atob(userInfoStr)
        userInfo = JSON.parse(decoded)
      } catch {
        userInfo = JSON.parse(userInfoStr)
      }

      userStore.setToken(token)
      userStore.setUserInfo(userInfo)
      permissionStore.clearMenus()
      ElMessage.success('登录成功')
      router.push('/')
    } catch (e) {
      errorMessage.value = '登录信息解析失败'
      loading.value = false
    }
  } else {
    errorMessage.value = '未收到有效的登录信息'
    loading.value = false
  }
})
</script>

<style scoped>
.oauth2-callback {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.callback-card {
  width: 400px;
  padding: 20px;
}

.callback-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  padding: 30px 0;
}

.loading-icon {
  animation: rotate 1.5s linear infinite;
  color: #409EFF;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error-text {
  color: #F56C6C;
  font-size: 14px;
  text-align: center;
}
</style>
