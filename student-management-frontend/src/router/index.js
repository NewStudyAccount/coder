import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
      meta: { title: '注册' }
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
          meta: { title: '首页', icon: 'House' }
        },
        {
          path: 'students',
          name: 'Students',
          component: () => import('@/views/StudentList.vue'),
          meta: { title: '学生管理', icon: 'User' }
        },
        {
          path: 'courses',
          name: 'Courses',
          component: () => import('@/views/CourseList.vue'),
          meta: { title: '课程管理', icon: 'Reading' }
        },
        {
          path: 'grades',
          name: 'Grades',
          component: () => import('@/views/GradeList.vue'),
          meta: { title: '成绩管理', icon: 'Document' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard'
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = !!userStore.token

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生管理系统` : '学生管理系统'

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (isAuthenticated) {
      next()
    } else {
      next('/login')
    }
  } else {
    // 已登录用户访问登录/注册页面，重定向到首页
    if (isAuthenticated && (to.path === '/login' || to.path === '/register')) {
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
