<template>
  <nav class="navbar">
    <div class="navbar-title">订单服务编排系统</div>
    <ul class="navbar-menu">
      <li :class="{active: active==='home'}">
        <a href="#" @click.prevent="switchPage('home')">首页</a>
      </li>
      <li :class="{active: active==='compose'}">
        <a href="#" @click.prevent="switchPage('compose')">订单编排</a>
      </li>
      <li :class="{active: active==='list'}">
        <a href="#" @click.prevent="switchPage('list')">列表展示</a>
      </li>
      <li>
        <a href="#">帮助</a>
      </li>
    </ul>
  </nav>
</template>

<script setup>
const props = defineProps({
  active: { type: String, default: 'home' }
})
const emit = defineEmits(['switch'])
function switchPage(page) {
  emit('switch', page)
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  background: #2d8cf0;
  color: #fff;
  padding: 0 24px;
  height: 56px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.navbar-title {
  font-size: 20px;
  font-weight: bold;
  margin-right: 40px;
}
.navbar-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
}
.navbar-menu li {
  margin-right: 24px;
}
.navbar-menu a {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  transition: color 0.2s;
  padding: 2px 8px;
  border-radius: 3px;
}
.navbar-menu a:hover {
  color: #ffd700;
  background: rgba(255,255,255,0.08);
}
.navbar-menu .active a {
  background: #ffd700;
  color: #2d8cf0;
  font-weight: bold;
}
</style>
