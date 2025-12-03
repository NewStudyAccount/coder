<script setup>
import { ref } from 'vue'

const orderId = ref('')
const orderLineId = ref('')
const serialNumber = ref('')
const parentSerialNumber = ref('')
const snUserId = ref('')

const loading = ref(false)
const errorMsg = ref('')
const result = ref(null)

async function submitGenerate() {
  errorMsg.value = ''
  result.value = null

  if (!orderId.value || !orderLineId.value || !serialNumber.value || !parentSerialNumber.value || !snUserId.value) {
    errorMsg.value = '请填写所有参数'
    return
  }

  const payload = {
    order_id: Number(orderId.value),
    order_line_id: Number(orderLineId.value),
    serial_number: serialNumber.value,
    parent_serial_number: parentSerialNumber.value,
    sn_user_id: snUserId.value
  }

  loading.value = true
  try {
    const resp = await fetch('/api/user-relation/generate', {
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
  <div class="user-relation-page">
    <h2>成员改号 user_relation 台账生成</h2>
    <div class="form">
      <label>
        order_id：
        <input v-model="orderId" type="number" />
      </label>
      <label>
        order_line_id：
        <input v-model="orderLineId" type="number" />
      </label>
      <label>
        serial_number：
        <input v-model="serialNumber" />
      </label>
      <label>
        parent_serial_number：
        <input v-model="parentSerialNumber" />
      </label>
      <label>
        sn_user_id：
        <input v-model="snUserId" />
      </label>
      <button @click="submitGenerate" :disabled="loading">生成台账</button>
    </div>

    <div v-if="loading">处理中...</div>
    <div v-if="errorMsg" class="error">{{ errorMsg }}</div>

    <div v-if="result">
      <h3>结果</h3>
      <div v-if="result.success">
        <div>台账生成成功</div>
        <table v-if="result.ledger">
          <thead>
            <tr>
              <th>order_id</th>
              <th>order_line_id</th>
              <th>relation_type_code</th>
              <th>user_id_a</th>
              <th>user_id_b</th>
              <th>serial_number_a</th>
              <th>serial_number_b</th>
              <th>call_sequence</th>
              <th>is_primary_number</th>
              <th>modify_tag</th>
              <th>start_date</th>
              <th>end_date</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ result.ledger.order_id }}</td>
              <td>{{ result.ledger.order_line_id }}</td>
              <td>{{ result.ledger.relation_type_code }}</td>
              <td>{{ result.ledger.user_id_a }}</td>
              <td>{{ result.ledger.user_id_b }}</td>
              <td>{{ result.ledger.serial_number_a }}</td>
              <td>{{ result.ledger.serial_number_b }}</td>
              <td>{{ result.ledger.call_sequence }}</td>
              <td>{{ result.ledger.is_primary_number }}</td>
              <td>{{ result.ledger.modify_tag }}</td>
              <td>{{ result.ledger.start_date }}</td>
              <td>{{ result.ledger.end_date }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else>
        <div class="error">{{ result.message }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-relation-page {
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