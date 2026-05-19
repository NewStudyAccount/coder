import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo>(JSON.parse(localStorage.getItem('userInfo') || '{}') as UserInfo)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = {} as UserInfo
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const isAdmin = () => {
    return userInfo.value.role === 'admin'
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
