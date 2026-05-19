import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { MenuInfo } from '@/types/menu'
import { getUserMenus } from '@/api/auth'

export const usePermissionStore = defineStore('permission', () => {
  const menuList = ref<MenuInfo[]>([])
  const menuLoaded = ref(false)

  const loadMenus = async () => {
    if (menuLoaded.value) return
    try {
      const res = await getUserMenus()
      menuList.value = res.data
      menuLoaded.value = true
    } catch (error) {
      console.error('加载菜单失败:', error)
    }
  }

  const clearMenus = () => {
    menuList.value = []
    menuLoaded.value = false
  }

  return {
    menuList,
    menuLoaded,
    loadMenus,
    clearMenus
  }
})
