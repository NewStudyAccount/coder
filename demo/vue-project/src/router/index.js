import { createRouter, createWebHistory } from 'vue-router'

// 页面组件
const Home = { template: '<div>首页</div>' }
const About = { template: '<div>关于我们</div>' }
const User = { 
  template: '<div>用户页面，ID: {{ $route.params.id }}</div>' 
}

// 路由配置
const UserManage = () => import('../components/UserManage.vue')
const UserPermission = () => import('../components/UserPermission.vue')

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', component: Home },
  { path: '/about', component: About },
  { path: '/users', component: UserManage },
  { path: '/permissions', component: UserPermission },
  // 动态路由，带守卫
  { 
    path: '/user/:id', 
    component: User,
    beforeEnter: (to, from, next) => {
      // 简单守卫：id 必须为数字
      if (/^\d+$/.test(to.params.id)) {
        next()
      } else {
        alert('用户ID必须为数字')
        next(false)
      }
    }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由拦截器（守卫）
import { useUserStore } from '../store/user'
router.beforeEach((to, from, next) => {
  // 只拦截 /user/:id 路由
  if (to.path.startsWith('/user')) {
    // 这里直接创建 store 实例（setup 外部用法）
    const userStore = useUserStore()
    if (userStore.username === '访客') {
      alert('请先登录（点击“切换用户名”按钮）')
      next('/home')
      return
    }
  }
  next()
})

// 全局响应拦截器（后置钩子）
router.afterEach((to, from) => {
  // 路由切换后自动滚动到顶部
  window.scrollTo(0, 0)
  // 打印路由切换日志
  console.log(`[路由切换] 从 ${from.fullPath} 到 ${to.fullPath}`)
})

export default router
