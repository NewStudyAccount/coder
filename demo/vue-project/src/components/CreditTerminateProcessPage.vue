<template>
  <div class="credit-terminate-process-container">
    <h2>信控拆机业务处理</h2>
    <div class="form-container">
      <div class="form-item">
        <label>订单ID (Order ID):</label>
        <input v-model="form.orderId" type="text" placeholder="请输入订单ID" />
      </div>
      <div class="form-item">
        <label>订单行ID (Order Line ID):</label>
        <input v-model="form.orderLineId" type="text" placeholder="请输入订单行ID" />
      </div>
      <button @click="submitProcess" :disabled="loading">
        {{ loading ? '处理中...' : '提交处理' }}
      </button>
    </div>
    
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>

    <div v-if="resultData" class="result-container">
      <h3>处理结果</h3>
      
      <div v-if="resultData.cancelOrderDetailList && resultData.cancelOrderDetailList.length > 0">
        <h4>需撤销订单明细 (Cancel Order Detail List)</h4>
        <table>
          <thead>
            <tr>
              <th>Serial Number</th>
              <th>Order ID</th>
              <th>Order Line ID</th>
              <th>Trade Type Code</th>
              <th>SRD</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in resultData.cancelOrderDetailList" :key="index">
              <td>{{ item.serialNumber }}</td>
              <td>{{ item.orderId }}</td>
              <td>{{ item.orderLineId }}</td>
              <td>{{ item.tradeTypeCode }}</td>
              <td>{{ item.srd }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-else>
        <p>无撤单明细</p>
      </div>

      <div v-if="resultData.notProcessSNList && resultData.notProcessSNList.length > 0">
        <h4>不处理用户列表 (Not Process SN List)</h4>
        <ul>
          <li v-for="(userId, index) in resultData.notProcessSNList" :key="index">
            User ID: {{ userId }}
          </li>
        </ul>
      </div>
       <div v-else>
        <p>无跳过处理的用户</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CreditTerminateProcessPage',
  data() {
    return {
      form: {
        orderId: '',
        orderLineId: ''
      },
      loading: false,
      message: '',
      messageType: 'info',
      resultData: null
    };
  },
  methods: {
    submitProcess() {
      if (!this.form.orderId || !this.form.orderLineId) {
        this.showMessage('请输入完整的订单信息', 'error');
        return;
      }

      this.loading = true;
      this.message = '';
      this.resultData = null;

      fetch('http://localhost:8080/api/credit-terminate/process', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          orderId: Number(this.form.orderId),
          orderLineId: Number(this.form.orderLineId)
        })
      })
      .then(async response => {
        if (response.ok) {
          const data = await response.json();
          this.resultData = data;
          this.showMessage(data.message || '处理成功', 'success');
        } else {
          const text = await response.text();
          this.showMessage(text, 'error');
        }
      })
      .catch(error => {
        this.showMessage('请求失败: ' + error.message, 'error');
      })
      .finally(() => {
        this.loading = false;
      });
    },
    showMessage(text, type) {
      this.message = text;
      this.messageType = type;
    }
  }
};
</script>

<style scoped>
.credit-terminate-process-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-item label {
  font-weight: bold;
}

input {
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

button {
  padding: 10px;
  background-color: #c0392b;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #e6b0aa;
  cursor: not-allowed;
}

.message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
}

.success {
  background-color: #f0f9eb;
  color: #67c23a;
}

.error {
  background-color: #fef0f0;
  color: #f56c6c;
}

.info {
  background-color: #f4f4f5;
  color: #909399;
}

.result-container {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}
</style>
