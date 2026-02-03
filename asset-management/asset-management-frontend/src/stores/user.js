import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 清除用户信息
  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 判断是否是管理员
  const isAdmin = () => {
    return userInfo.value.role === 'ADMIN'
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    clearUserInfo,
    isAdmin
  }
})
