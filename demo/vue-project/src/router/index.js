import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/HelloWorld.vue'

// 懒加载或直接导入新建的 OIDC 组件
import OidcLogin from '../components/OidcLogin.vue'
import OidcCallback from '../components/OidcCallback.vue'
import OidcUserinfo from '../components/OidcUserinfo.vue'
import ExternalLogin from '../components/ExternalLogin.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  // OIDC 演示路由（直连自建 IdP /oidc2/**）
  { path: '/oidc2/login', name: 'oidc-login', component: OidcLogin },
  { path: '/external-login', name: 'external-login', component: ExternalLogin },
  { path: '/callback', name: 'oidc-callback', component: OidcCallback },
  { path: '/userinfo', name: 'oidc-userinfo', component: OidcUserinfo },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router