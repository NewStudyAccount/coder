import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// 路由配置
const routes = [
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
    meta: { requiresAuth: true },
    redirect: '/books',
    children: [
      {
        path: '/books',
        name: 'Books',
        component: () => import('@/views/Books.vue'),
        meta: { title: '图书查询' }
      },
      {
        path: '/borrow',
        name: 'Borrow',
        component: () => import('@/views/Borrow.vue'),
        meta: { title: '借阅管理' }
      },
      {
        path: '/return',
        name: 'Return',
        component: () => import('@/views/Return.vue'),
        meta: { title: '归还管理' }
      },
      {
        path: '/my-borrows',
        name: 'MyBorrows',
        component: () => import('@/views/MyBorrows.vue'),
        meta: { title: '我的借阅' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/books'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth) {
    // 需要登录的页面
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 不需要登录的页面
    if (userStore.isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      // 已登录用户访问登录/注册页面，重定向到首页
      next('/books')
    } else {
      next()
    }
  }
})

// 全局后置钩子
router.afterEach((to) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 图书馆管理系统` : '图书馆管理系统'
})

export default router
