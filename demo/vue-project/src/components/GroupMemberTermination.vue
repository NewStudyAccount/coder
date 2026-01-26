<template>
  <div class="termination-container">
    <h2>Group Member Termination Supplement</h2>
    <div class="form-group">
      <label>Order ID:</label>
      <input v-model="form.orderId" type="number" placeholder="Enter Order ID" />
    </div>
    <div class="form-group">
      <label>Parent Serial Number (Group Number):</label>
      <input v-model="form.parentSerialNumber" type="text" placeholder="Enter Group Number" />
    </div>
    <div class="form-group">
      <label>Group User ID:</label>
      <input v-model="form.groupUserId" type="number" placeholder="Enter Group User ID" />
    </div>
    <button @click="submit" :disabled="loading" class="submit-btn">
      {{ loading ? 'Processing...' : 'Execute Supplement' }}
    </button>
    <div v-if="message" :class="['message', success ? 'success' : 'error']">
      {{ message }}
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'GroupMemberTermination',
  data() {
    return {
      form: {
        orderId: '',
        parentSerialNumber: '',
        groupUserId: ''
      },
      loading: false,
      message: '',
      success: false
    }
  },
  methods: {
    async submit() {
      if (!this.form.orderId || !this.form.parentSerialNumber || !this.form.groupUserId) {
        this.message = 'Please fill in all fields'
        this.success = false
        return
      }

      this.loading = true
      this.message = ''
      
      try {
        const response = await request.post('/api/group-member-termination/process', this.form)
        this.message = response || 'Success'
        this.success = true
      } catch (error) {
        this.message = error.message || 'Operation failed'
        this.success = false
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.termination-container {
  max-width: 500px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.submit-btn {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn:disabled {
  background-color: #ccc;
}

.message {
  margin-top: 15px;
  padding: 10px;
  border-radius: 4px;
}

.success {
  background-color: #d4edda;
  color: #155724;
}

.error {
  background-color: #f8d7da;
  color: #721c24;
}
</style>
