<template>
  <div class="detail-page">
    <h2>订单详情</h2>
    <div v-if="loading">详情加载中...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <table v-if="detail && Object.keys(detail).length" border="1">
      <tbody>
        <tr v-for="(val, key) in detail" :key="key">
          <td style="font-weight:bold;">{{ key }}</td>
          <td>{{ val }}</td>
        </tr>
      </tbody>
    </table>
    <div v-if="!loading && !error && (!detail || !Object.keys(detail).length)" style="margin-top:20px;">暂无详情数据</div>
    <button @click="goBack" style="margin-top:24px;">返回列表</button>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
const props = defineProps({
  orderId: { type: String, required: true }
})
const emit = defineEmits(['back'])

const detail = ref({})
const loading = ref(false)
const error = ref('')

onMounted(fetchDetail)
watch(() => props.orderId, fetchDetail)

async function fetchDetail() {
  if (!props.orderId) return
  loading.value = true
  error.value = ''
  detail.value = {}
  try {
    const resp = await fetch(`/api/detail-data?order_id=${encodeURIComponent(props.orderId)}`)
    if (!resp.ok) throw new Error('详情接口请求失败')
    detail.value = await resp.json()
  } catch (e) {
    error.value = '详情加载失败：' + (e.message || e)
  } finally {
    loading.value = false
  }
}

function goBack() {
  emit('back')
}
</script>

<style scoped>
.detail-page {
  max-width: 600px;
  margin: 30px auto;
  padding: 20px;
  background: #f8f8f8;
  border-radius: 8px;
}
.error {
  color: red;
  margin-top: 10px;
}
table {
  margin-top: 20px;
  width: 100%;
  border-collapse: collapse;
}
td {
  padding: 6px 12px;
  text-align: left;
}
button {
  padding: 6px 18px;
  font-size: 15px;
  border-radius: 4px;
  border: none;
  background: #2d8cf0;
  color: #fff;
  cursor: pointer;
  transition: background 0.2s;
}
button:hover {
  background: #1a6edb;
}
</style>