import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    username: '访客',
    count: 0
  }),
  actions: {
    setUsername(name) {
      this.username = name
    },
    increment() {
      this.count++
    }
  }
})