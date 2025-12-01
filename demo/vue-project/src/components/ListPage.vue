<template>
  <div class="list-page">
    <h2>通用列表展示</h2>
    <div v-if="loading">数据加载中...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <table v-if="columns.length && data.length" border="1">
      <thead>
        <tr>
          <th v-for="col in columns" :key="col.key">{{ col.label }}</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, idx) in data" :key="idx">
          <td v-for="col in columns" :key="col.key">{{ row[col.key] }}</td>
          <td>
            <button @click="showDetail(row.order_id)">查询详情</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="!loading && !error && !data.length" style="margin-top:20px;">暂无数据</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const emit = defineEmits(['detail'])

const columns = ref([])
const data = ref([])
const loading = ref(false)
const error = ref('')

onMounted(fetchListData)

async function fetchListData() {
  loading.value = true
  error.value = ''
  try {
    const resp = await fetch('/api/list-data')
    if (!resp.ok) throw new Error('接口请求失败')
    const result = await resp.json()
    columns.value = result.columns || []
    data.value = result.data || []
  } catch (e) {
    error.value = '数据加载失败：' + (e.message || e)
  } finally {
    loading.value = false
  }
}

function showDetail(orderId) {
  emit('detail', orderId)
}
</script>

<style scoped>
.list-page {
  max-width: 800px;
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
th, td {
  padding: 6px 12px;
  text-align: center;
}
button {
  padding: 4px 12px;
  font-size: 14px;
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
