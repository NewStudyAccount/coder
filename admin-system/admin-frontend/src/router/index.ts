import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/oauth2/callback',
      name: 'Oauth2Callback',
      component: () => import('@/views/login/Oauth2Callback.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/Dashboard.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'system/user',
          name: 'UserList',
          component: () => import('@/views/system/user/UserList.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'system/role',
          name: 'RoleList',
          component: () => import('@/views/system/role/RoleList.vue'),
          meta: { title: '角色管理' }
        },
        {
          path: 'system/menu',
          name: 'MenuList',
          component: () => import('@/views/system/menu/MenuList.vue'),
          meta: { title: '菜单管理' }
        },
        {
          path: 'system/dept',
          name: 'DeptList',
          component: () => import('@/views/system/dept/DeptList.vue'),
          meta: { title: '部门管理' }
        },
        {
          path: 'system/oss',
          name: 'OssList',
          component: () => import('@/views/system/oss/OssList.vue'),
          meta: { title: '文件管理' }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/profile/Profile.vue'),
          meta: { title: '个人中心' }
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.requiresAuth !== false) {
    if (!token) {
      next('/login')
      return
    }
  }

  if (token && (to.path === '/login')) {
    next('/')
    return
  }

  next()
})

export default router
