<script setup>
import { ref } from 'vue'

const amendOrdersInput = ref('') // 逗号分隔订单ID，如：1001,1002
const tradeTypeCode = ref('')
const cancelTag = ref(0)
const cancelDnQty = ref(0)

const loading = ref(false)
const errorMsg = ref('')
const result = ref(null)

async function submitCalc() {
  errorMsg.value = ''
  result.value = null
  const amendOrderList = amendOrdersInput.value
    .split(',')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => Number(s))

  if (amendOrderList.length === 0) {
    errorMsg.value = '请填写至少一个订单ID'
    return
  }
  if (cancelTag.value !== 3 && !tradeTypeCode.value) {
    errorMsg.value = '当 cancel_tag != 3 时必须填写 trade_type_code'
    return
  }

  const payload = {
    amendOrderList,
    trade_type_code: tradeTypeCode.value || null,
    cancel_tag: Number(cancelTag.value),
    cancelDnQty: Number(cancelDnQty.value || 0)
  }

  loading.value = true
  try {
    const resp = await fetch('/api/otc/calc', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    if (!resp.ok) {
      const text = await resp.text().catch(() => '')
      throw new Error('HTTP ' + resp.status + (text ? (': ' + text) : ''))
    }
    result.value = await resp.json()
  } catch (e) {
    errorMsg.value = String(e)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="otc-calc-page">
    <h2>DN级OTC费用计算</h2>
    <div class="form">
      <label>
        amendOrderList (逗号分隔)：
        <input v-model="amendOrdersInput" placeholder="例如：1001,1002" />
      </label>
      <label>
        trade_type_code：
        <input v-model="tradeTypeCode" placeholder="当 cancel_tag != 3 时必填" />
      </label>
      <label>
        cancel_tag：
        <select v-model="cancelTag">
          <option :value="0">0</option>
          <option :value="3">3</option>
        </select>
      </label>
      <label>
        cancelDnQty：
        <input v-model="cancelDnQty" type="number" min="0" />
      </label>
      <button @click="submitCalc" :disabled="loading">提交计算</button>
    </div>

    <div v-if="loading">计算中...</div>
    <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

    <div v-if="result">
      <h3>结果</h3>
      <div>dnQty: {{ result.dnQty }}</div>

      <h4>dnLevelOtcProductList</h4>
      <table v-if="result.dnLevelOtcProductList && result.dnLevelOtcProductList.length">
        <thead>
          <tr>
            <th>trade_type_code</th>
            <th>product_id</th>
            <th>prod_item_id</th>
            <th>package_id</th>
            <th>element_id</th>
            <th>element_item_id</th>
            <th>start_date</th>
            <th>end_date</th>
            <th>standard_fee</th>
            <th>otc_fee</th>
            <th>qty</th>
            <th>waived_fee</th>
            <th>total_fee</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(it, idx) in result.dnLevelOtcProductList" :key="'dn-'+idx">
            <td>{{ it.trade_type_code }}</td>
            <td>{{ it.product_id }}</td>
            <td>{{ it.prod_item_id }}</td>
            <td>{{ it.package_id }}</td>
            <td>{{ it.element_id }}</td>
            <td>{{ it.element_item_id }}</td>
            <td>{{ it.start_date }}</td>
            <td>{{ it.end_date }}</td>
            <td>{{ it.standard_fee }}</td>
            <td>{{ it.otc_fee }}</td>
            <td>{{ it.qty }}</td>
            <td>{{ it.waived_fee }}</td>
            <td>{{ it.total_fee }}</td>
          </tr>
        </tbody>
      </table>

      <h4>orderLevelOtcProductList</h4>
      <table v-if="result.orderLevelOtcProductList && result.orderLevelOtcProductList.length">
        <thead>
          <tr>
            <th>trade_type_code</th>
            <th>product_id</th>
            <th>prod_item_id</th>
            <th>package_id</th>
            <th>element_id</th>
            <th>element_item_id</th>
            <th>start_date</th>
            <th>end_date</th>
            <th>standard_fee</th>
            <th>otc_fee</th>
            <th>rebate_fee</th>
            <th>qty</th>
            <th>waived_fee</th>
            <th>total_fee</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(it, idx) in result.orderLevelOtcProductList" :key="'ol-'+idx">
            <td>{{ it.trade_type_code }}</td>
            <td>{{ it.product_id }}</td>
            <td>{{ it.prod_item_id }}</td>
            <td>{{ it.package_id }}</td>
            <td>{{ it.element_id }}</td>
            <td>{{ it.element_item_id }}</td>
            <td>{{ it.start_date }}</td>
            <td>{{ it.end_date }}</td>
            <td>{{ it.standard_fee }}</td>
            <td>{{ it.otc_fee }}</td>
            <td>{{ it.rebate_fee }}</td>
            <td>{{ it.qty }}</td>
            <td>{{ it.waived_fee }}</td>
            <td>{{ it.total_fee }}</td>
          </tr>
        </tbody>
      </table>

      <div v-if="!result.dnLevelOtcProductList?.length && !result.orderLevelOtcProductList?.length">
        暂无OTC计算项
      </div>
    </div>
  </div>
</template>

<style scoped>
.otc-calc-page {
  padding: 16px;
}
.form {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
  margin-bottom: 16px;
}
.form label {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.error {
  color: #c00;
  margin: 8px 0;
}
table {
  border-collapse: collapse;
  margin: 8px 0 16px;
  width: 100%;
}
th, td {
  border: 1px solid #ddd;
  padding: 6px 8px;
  text-align: left;
  font-size: 12px;
}
</style>