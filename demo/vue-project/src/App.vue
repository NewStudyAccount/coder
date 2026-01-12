<template>
  <div>
    <NavBar :active="activePage" @switch="switchPage" />
    <ErrorConsole :errors="errors" @close="removeError" />
    <main>
      <div v-if="activePage === 'home'">
        <h2 style="text-align:center;margin-top:40px;">欢迎使用订单服务编排系统</h2>
      </div>
      <OrderServiceComposer v-if="activePage === 'compose'" @error="addError" />
      <ListPage v-if="activePage === 'list'" @detail="showDetail" />
      <DetailPage v-if="activePage === 'detail'" :orderId="detailOrderId" @back="switchPage('list')" />
      <OssManage v-if="activePage === 'oss'" />
      <IddProcessPage v-if="activePage === 'idd'" />
      <SuspendProcessPage v-if="activePage === 'suspend'" />
      <IddSuspendProcessPage v-if="activePage === 'idd-suspend'" />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import NavBar from './components/NavBar.vue'
import ErrorConsole from './components/ErrorConsole.vue'
import OrderServiceComposer from './components/OrderServiceComposer.vue'
import ListPage from './components/ListPage.vue'
import DetailPage from './components/DetailPage.vue'
import OssManage from './components/OssManage.vue'
import IddProcessPage from './components/IddProcessPage.vue'
import SuspendProcessPage from './components/SuspendProcessPage.vue'
import IddSuspendProcessPage from './components/IddSuspendProcessPage.vue'

const errors = ref([])
const activePage = ref('home')
const detailOrderId = ref('')

function addError(msg) {
  errors.value.push(msg)
}
function removeError(idx) {
  errors.value.splice(idx, 1)
}
function switchPage(page) {
  activePage.value = page
}
function showDetail(orderId) {
  detailOrderId.value = orderId
  activePage.value = 'detail'
}
</script>

<style>
body {
  margin: 0;
  font-family: "Microsoft YaHei", Arial, sans-serif;
  background: #f4f6fa;
}
main {
  padding: 20px 0;
}
</style>
