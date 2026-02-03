import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
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
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'assets',
          name: 'Assets',
          component: () => import('@/views/asset/AssetList.vue'),
          meta: { title: '资产管理' }
        },
        {
          path: 'assets/add',
          name: 'AssetAdd',
          component: () => import('@/views/asset/AssetForm.vue'),
          meta: { title: '添加资产' }
        },
        {
          path: 'assets/edit/:id',
          name: 'AssetEdit',
          component: () => import('@/views/asset/AssetForm.vue'),
          meta: { title: '编辑资产' }
        },
        {
          path: 'assets/detail/:id',
          name: 'AssetDetail',
          component: () => import('@/views/asset/AssetDetail.vue'),
          meta: { title: '资产详情' }
        },
        {
          path: 'categories',
          name: 'Categories',
          component: () => import('@/views/category/CategoryList.vue'),
          meta: { title: '分类管理', requiresAdmin: true }
        },
        {
          path: 'borrows',
          name: 'Borrows',
          component: () => import('@/views/borrow/BorrowList.vue'),
          meta: { title: '借用管理' }
        },
        {
          path: 'borrows/my',
          name: 'MyBorrows',
          component: () => import('@/views/borrow/MyBorrows.vue'),
          meta: { title: '我的借用' }
        },
        {
          path: 'maintenances',
          name: 'Maintenances',
          component: () => import('@/views/maintenance/MaintenanceList.vue'),
          meta: { title: '维护管理' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/user/UserList.vue'),
          meta: { title: '用户管理', requiresAdmin: true }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/user/Profile.vue'),
          meta: { title: '个人中心' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  // 需要认证的路由
  if (to.meta.requiresAuth !== false) {
    if (!token) {
      next('/login')
      return
    }
  }

  // 需要管理员权限的路由
  if (to.meta.requiresAdmin && !userStore.isAdmin()) {
    next('/')
    return
  }

  // 已登录用户访问登录页，重定向到首页
  if (token && (to.path === '/login' || to.path === '/register')) {
    next('/')
    return
  }

  next()
})

export default router
