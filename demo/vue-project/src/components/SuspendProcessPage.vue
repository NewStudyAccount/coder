<template>
  <div class="suspend-process-container">
    <h2>停机业务处理</h2>
    <div class="form-container">
      <div class="form-item">
        <label>订单ID (Order ID):</label>
        <input v-model="form.orderId" type="text" placeholder="请输入订单ID" />
      </div>
      <div class="form-item">
        <label>订单行ID (Order Line ID):</label>
        <input v-model="form.orderLineId" type="text" placeholder="请输入订单行ID" />
      </div>
      <div class="form-item">
        <label>业务类型代码 (Trade Type Code):</label>
        <select v-model="form.tradeTypeCode">
          <option value="">请选择</option>
          <option value="126">126 (普通停机)</option>
          <option value="7220">7220 (信控停机)</option>
        </select>
      </div>
      <button @click="submitProcess" :disabled="loading">
        {{ loading ? '处理中...' : '提交处理' }}
      </button>
    </div>
    
    <div v-if="message" :class="['message', messageType]">
      {{ message }}
    </div>
  </div>
</template>

<script>
export default {
  name: 'SuspendProcessPage',
  data() {
    return {
      form: {
        orderId: '',
        orderLineId: '',
        tradeTypeCode: ''
      },
      loading: false,
      message: '',
      messageType: 'info'
    };
  },
  methods: {
    submitProcess() {
      if (!this.form.orderId || !this.form.orderLineId || !this.form.tradeTypeCode) {
        this.showMessage('请输入完整的业务信息', 'error');
        return;
      }

      this.loading = true;
      this.message = '';

      fetch('http://localhost:8080/api/suspend/process', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          orderId: Number(this.form.orderId),
          orderLineId: Number(this.form.orderLineId),
          tradeTypeCode: this.form.tradeTypeCode
        })
      })
      .then(async response => {
        const text = await response.text();
        if (response.ok) {
          this.showMessage(text, 'success');
        } else {
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
.suspend-process-container {
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

input, select {
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

button {
  padding: 10px;
  background-color: #e6a23c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #f3d19e;
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
</style>
