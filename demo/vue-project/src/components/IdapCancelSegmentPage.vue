<template>
  <div class="idap-cancel-segment">
    <h2>取消号段并连带对散号拆机（IDAP）</h2>

    <div class="form">
      <label>
        Order ID:
        <input v-model.number="form.orderId" type="number" placeholder="例如：1001" />
      </label>
      <label>
        User ID:
        <input v-model="form.userId" type="text" placeholder="例如：U-GROUP-001" />
      </label>
      <button @click="execute" :disabled="loading">
        {{ loading ? '处理中...' : '执行' }}
      </button>
      <button @click="loadDemo" :disabled="loading">加载演示入参</button>
    </div>

    <div class="result" v-if="result">
      <h3>执行结果</h3>
      <p><strong>success:</strong> {{ result.success }}</p>
      <p><strong>message:</strong> {{ result.message }}</p>

      <div v-if="result.createdLines && result.createdLines.length">
        <h4>新增拆机订单行（{{ result.createdLines.length }}）</h4>
        <table>
          <thead>
            <tr>
              <th>orderId</th>
              <th>orderLineId</th>
              <th>tradeTypeCode</th>
              <th>netTypeCode</th>
              <th>lineLevel</th>
              <th>serialNumber</th>
              <th>userId</th>
              <th>custId</th>
              <th>mainProductId</th>
              <th>mainProductName</th>
              <th>mainProductType</th>
              <th>productFamily</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(line, idx) in result.createdLines" :key="idx">
              <td>{{ line.orderId }}</td>
              <td>{{ line.orderLineId }}</td>
              <td>{{ line.tradeTypeCode }}</td>
              <td>{{ line.netTypeCode }}</td>
              <td>{{ line.lineLevel }}</td>
              <td>{{ line.serialNumber }}</td>
              <td>{{ line.userId }}</td>
              <td>{{ line.custId }}</td>
              <td>{{ line.mainProductId }}</td>
              <td>{{ line.mainProductName }}</td>
              <td>{{ line.mainProductType }}</td>
              <td>{{ line.productFamily }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="result.buildSteps && result.buildSteps.length">
        <h4>编排步骤</h4>
        <ul>
          <li v-for="(step, i) in result.buildSteps" :key="i">{{ step }}</li>
        </ul>
      </div>
    </div>

    <div class="error" v-if="error">
      <h3>错误</h3>
      <pre>{{ error }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  name: 'IdapCancelSegmentPage',
  data() {
    return {
      form: {
        orderId: null,
        userId: ''
      },
      result: null,
      error: null,
      loading: false
    };
  },
  methods: {
    loadDemo() {
      fetch('/api/idap/cancel-segment/demo-request')
        .then(res => res.json())
        .then(data => {
          this.form.orderId = data.orderId;
          this.form.userId = data.userId;
        })
        .catch(err => {
          this.error = String(err);
        });
    },
    execute() {
      this.error = null;
      this.result = null;
      this.loading = true;

      fetch('/api/idap/cancel-segment/execute', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(this.form)
      })
        .then(res => res.json())
        .then(data => {
          this.result = data;
        })
        .catch(err => {
          this.error = String(err);
        })
        .finally(() => {
          this.loading = false;
        });
    }
  }
};
</script>

<style scoped>
.idap-cancel-segment {
  padding: 16px;
}
.form {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}
.form label {
  display: flex;
  flex-direction: column;
  font-size: 14px;
}
.form input {
  padding: 6px 8px;
  font-size: 14px;
}
.form button {
  padding: 8px 12px;
}
.result table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 8px;
}
.result th, .result td {
  border: 1px solid #ddd;
  padding: 6px 8px;
  font-size: 12px;
}
.error pre {
  background: #ffecec;
  border: 1px solid #ffb3b3;
  padding: 8px;
  white-space: pre-wrap;
}
</style>