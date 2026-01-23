import { defineStore } from 'pinia'
import { login, getUserInfo, logout } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    username: '',
    roles: []
  }),
  actions: {
    // 登录
    async login(userInfo) {
      try {
        const res = await login(userInfo)
        if (res.token) {
          this.token = res.token
          localStorage.setItem('token', res.token)
          // 登录成功后获取用户信息
          await this.getInfo()
          return true
        }
        return false
      } catch (error) {
        console.error('Login failed:', error)
        throw error
      }
    },
    // 获取用户信息
    async getInfo() {
      try {
        const res = await getUserInfo()
        if (res.code === 0) {
          this.username = res.username
          this.roles = res.roles
          return res
        } else {
            // Token 失效或获取失败
            this.logout()
            throw new Error(res.msg)
        }
      } catch (error) {
        console.error('Get info failed:', error)
        throw error
      }
    },
    // 退出
    async logout() {
      try {
        // await logout() // 如果后端有退出接口
        this.token = ''
        this.username = ''
        this.roles = []
        localStorage.removeItem('token')
      } catch (error) {
        console.error('Logout failed:', error)
      }
    }
  }
})
