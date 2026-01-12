import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/HelloWorld.vue'

// 懒加载或直接导入新建的 OIDC 与 Penrose 组件
import OidcLogin from '../components/OidcLogin.vue'
import OidcCallback from '../components/OidcCallback.vue'
import OidcUserinfo from '../components/OidcUserinfo.vue'
import SSOLogin from '../components/SSOLogin.vue'
import ExternalLogin from '../components/ExternalLogin.vue'
import PenroseTiling from '../components/PenroseTiling.vue'
import OssManage from '../components/OssManage.vue'
import IddProcessPage from '../components/IddProcessPage.vue'
import SuspendProcessPage from '../components/SuspendProcessPage.vue'
import IddSuspendProcessPage from '../components/IddSuspendProcessPage.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/sso', name: 'sso-login', component: SSOLogin },
  // OIDC 演示路由（直连自建 IdP /oidc2/**）
  { path: '/oidc2/login', name: 'oidc-login', component: OidcLogin },
  { path: '/external-login', name: 'external-login', component: ExternalLogin },
  { path: '/callback', name: 'oidc-callback', component: OidcCallback },
  { path: '/userinfo', name: 'oidc-userinfo', component: OidcUserinfo },
  // Penrose Tiling 演示
  { path: '/penrose', name: 'penrose', component: PenroseTiling },
  // OSS 管理演示
  { path: '/oss', name: 'oss-manage', component: OssManage },
  // IDD 业务处理演示
  { path: '/idd', name: 'idd-process', component: IddProcessPage },
  // 停机业务处理演示
  { path: '/suspend', name: 'suspend-process', component: SuspendProcessPage },
  // 仅停 IDD 业务处理演示
  { path: '/idd-suspend', name: 'idd-suspend-process', component: IddSuspendProcessPage },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
