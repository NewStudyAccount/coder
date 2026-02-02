import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getUserInfo } from '@/api/library'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const userId = computed(() => userInfo.value?.id || null)

  // 登录
  const login = async (loginForm) => {
    try {
      const res = await loginApi(loginForm)
      if (res.code === 200 || res.code === 0) {
        token.value = res.data.token
        userInfo.value = res.data.userInfo

        // 持久化存储
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))

        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(res.message || '登录失败')
        return false
      }
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error('登录失败，请重试')
      return false
    }
  }

  // 退出登录
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录请求失败:', error)
    } finally {
      // 清除本地存储
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')

      ElMessage.success('已退出登录')
      router.push('/login')
    }
  }

  // 检查登录状态
  const checkLoginStatus = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')

    if (savedToken && savedUserInfo) {
      token.value = savedToken
      try {
        userInfo.value = JSON.parse(savedUserInfo)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        clearLoginInfo()
      }
    }
  }

  // 更新用户信息
  const updateUserInfo = async () => {
    try {
      const res = await getUserInfo()
      if (res.code === 200 || res.code === 0) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  // 清除登录信息
  const clearLoginInfo = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    // 状态
    token,
    userInfo,
    // 计算属性
    isLoggedIn,
    username,
    userId,
    // 方法
    login,
    logout,
    checkLoginStatus,
    updateUserInfo,
    clearLoginInfo
  }
})
