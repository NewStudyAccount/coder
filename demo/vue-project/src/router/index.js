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
import ChangeGroupProcessPage from '../components/ChangeGroupProcessPage.vue'
import AddHuntingProcessPage from '../components/AddHuntingProcessPage.vue'
import CreditTerminateProcessPage from '../components/CreditTerminateProcessPage.vue'
import CreditControlSuspendPage from '../components/CreditControlSuspendPage.vue'
import GroupMemberTermination from '../components/GroupMemberTermination.vue'
import AccountStatusPage from '../components/AccountStatusPage.vue'
import WebFluxDemo from '../components/WebFluxDemo.vue'

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
  // 群组改号业务处理演示
  { path: '/change-group', name: 'change-group-process', component: ChangeGroupProcessPage },
  // 加装 Hunting 业务处理演示
  { path: '/add-hunting', name: 'add-hunting-process', component: AddHuntingProcessPage },
  // 信控拆机业务处理演示
  { path: '/credit-terminate', name: 'credit-terminate-process', component: CreditTerminateProcessPage },
  // 信控停机业务处理演示
  { path: '/credit-control-suspend', name: 'credit-control-suspend', component: CreditControlSuspendPage },
  // 群组成员拆机增补业务处理演示
  { path: '/group-member-termination', name: 'group-member-termination', component: GroupMemberTermination },
  // 账户状态检查演示
  { path: '/account-status-check', name: 'account-status-check', component: AccountStatusPage },
  // WebFlux 演示
  { path: '/webflux', name: 'webflux-demo', component: WebFluxDemo },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
