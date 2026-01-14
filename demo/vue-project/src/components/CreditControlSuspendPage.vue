<template>
  <div class="credit-control-suspend-page">
    <h1>Credit Control Suspend Process</h1>
    <div class="form-group">
      <label for="accountId">Account ID:</label>
      <input type="text" id="accountId" v-model="accountId" placeholder="Enter Account ID" />
    </div>
    <div class="form-group">
      <label for="orderId">Order ID:</label>
      <input type="text" id="orderId" v-model="orderId" placeholder="Enter Order ID" />
    </div>
    <button @click="submitProcess">Submit</button>
    <div v-if="message" class="message">{{ message }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      accountId: '',
      orderId: '',
      message: ''
    };
  },
  methods: {
    async submitProcess() {
      if (!this.accountId || !this.orderId) {
        this.message = 'Please enter both Account ID and Order ID.';
        return;
      }

      try {
        const response = await fetch(`/api/credit-control/suspend?accountId=${this.accountId}&orderId=${this.orderId}`, {
          method: 'POST'
        });

        if (response.ok) {
          this.message = await response.text();
        } else {
          this.message = 'Error: ' + response.statusText;
        }
      } catch (error) {
        this.message = 'Error: ' + error.message;
      }
    }
  }
};
</script>

<style scoped>
.credit-control-suspend-page {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

button {
  padding: 10px 15px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

.message {
  margin-top: 20px;
  font-weight: bold;
}
</style>
