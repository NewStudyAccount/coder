<template>
  <div class="order-service-composer">
    <h2>订单构建服务编排</h2>
    <div class="search-bar">
      <input v-model="searchText" placeholder="搜索 user_id / trade_type_code / scene_type" @input="onSearch" />
    </div>
    <div>
      <label>订单ID：</label>
      <input v-model="orderId" placeholder="请输入order_id" />
      <button @click="fetchOrderLines">查询</button>
    </div>
    <div v-if="loading">查询中...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="filteredOrderLines.length">
      <h3>成员订单行</h3>
      <table border="1">
        <thead>
          <tr>
            <th>user_id</th>
            <th>trade_type_code</th>
            <th>scene_type</th>
            <th>需编排服务</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(line, idx) in filteredOrderLines" :key="idx">
            <td>{{ line.user_id }}</td>
            <td>{{ line.trade_type_code }}</td>
            <td>{{ line.scene_type }}</td>
            <td>
              <span v-if="getServices(line.trade_type_code, line.scene_type).length">
                {{ getServices(line.trade_type_code, line.scene_type).join('、') }}
              </span>
              <span v-else>无匹配服务</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 服务映射表
const serviceMap = [
  { code: '160', scene: '16000', services: ['二', '十四'] },
  { code: '70', scene: '70000', services: ['十四'] },
  { code: '279', scene: '27901', services: ['五', '二十', '八', '二十八-2'] },
  { code: '340', scene: '34005', services: ['十五', '二十八-1'] },
  { code: '3410', scene: ['34100', '34101'], services: ['三'] },
  { code: '192', scene: '19201', services: ['九', '十', '十一', '十二', '四'] },
  { code: '192', scene: '19200', services: ['九', '十', '十一', '十二', '四', '二十七', '三十二', '三十一'] },
  { code: '340', scene: '34006', services: ['十六', '二十八-1'] },
  { code: '340', scene: '34007', services: ['二十三', '二十八-1'] },
  { code: '340', scene: '34004', services: ['十八', '十七', '二十六'], order: [18, 17] },
  { code: '340', scene: '34000', services: ['一', '二', '三', '四', '五', '六', '七', '二十四', '二十五', '三十四', '三十', '二十九', '三十四'] },
  { code: '340', scene: '34001', services: ['一', '十七', '三十'] },
  { code: '340', scene: '34002', services: ['一', '二', '三', '五', '七', '二十五', '十七', '三十'] },
  { code: '10', scene: '10000', services: ['二十四', '三十', '二十九', '三十四'] },
  { code: '340', scene: '34003', services: ['二十四', '28'] },
  { code: '101', scene: '10100', services: ['二', '七', '十四'] },
  { code: '340', scene: '34008', services: ['一', '三十三', '十七', '二十五'] },
  { code: '340', scene: '34009', services: ['44'] },
  { code: '127', scene: '12701', services: ['45'] }
]

// 查询结果
const orderId = ref('')
const orderLines = ref([])
const loading = ref(false)
const error = ref('')
const searchText = ref('')

// 搜索过滤
const filteredOrderLines = computed(() => {
  if (!searchText.value.trim()) return orderLines.value
  const kw = searchText.value.trim().toLowerCase()
  return orderLines.value.filter(line =>
    (line.user_id && line.user_id.toLowerCase().includes(kw)) ||
    (line.trade_type_code && String(line.trade_type_code).toLowerCase().includes(kw)) ||
    (line.scene_type && String(line.scene_type).toLowerCase().includes(kw))
  )
})

function onSearch() {
  // 仅触发过滤，computed已自动处理
}

// 服务匹配函数
function getServices(tradeTypeCode, sceneType) {
  for (const item of serviceMap) {
    if (
      item.code === String(tradeTypeCode) &&
      (Array.isArray(item.scene)
        ? item.scene.includes(String(sceneType))
        : item.scene === String(sceneType))
    ) {
      return item.services
    }
  }
  return []
}

import { onMounted, defineEmits } from 'vue'
const emit = defineEmits(['error'])

async function fetchOrderLines() {
  loading.value = true
  error.value = ''
  orderLines.value = []
  try {
    // TODO: 替换为真实API
    // const resp = await fetch(`/api/order-lines?order_id=${orderId.value}`)
    // orderLines.value = await resp.json()
    // mock数据
    orderLines.value = [
      { user_id: 'U001', trade_type_code: '340', scene_type: '34000' },
      { user_id: 'U002', trade_type_code: '192', scene_type: '19201' },
      { user_id: 'U003', trade_type_code: '10', scene_type: '10000' }
    ]
    // 模拟错误
    if (!orderId.value) {
      const msg = '订单ID不能为空'
      error.value = msg
      emit('error', msg)
    }
  } catch (e) {
    error.value = '查询失败：' + (e.message || e)
    emit('error', error.value)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.order-service-composer {
  max-width: 800px;
  margin: 30px auto;
  padding: 20px;
  background: #f8f8f8;
  border-radius: 8px;
}
.search-bar {
  margin-bottom: 12px;
}
.search-bar input {
  width: 320px;
  padding: 6px 12px;
  font-size: 15px;
  border: 1px solid #bbb;
  border-radius: 4px;
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
</style>
