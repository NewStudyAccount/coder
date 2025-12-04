<template>
  <div class="member-relation-supplement-page">
    <h2>增补成员的付费关系、装机地址台账生成</h2>
    <form @submit.prevent="handleSubmit">
      <div>
        <label>订单ID(order_id)：</label>
        <input v-model="form.orderId" type="number" required />
      </div>
      <div>
        <label>订单行ID(order_line_id)：</label>
        <input v-model="form.orderLineId" type="number" required />
      </div>
      <div>
        <label>成员用户ID(sn_user_id)：</label>
        <input v-model="form.snUserId" type="text" required />
      </div>
      <div>
        <label>群组父号码(parent_serial_number)：</label>
        <input v-model="form.parentSerialNumber" type="text" required />
      </div>
      <div>
        <label>生效时间(SRD)：</label>
        <input v-model="form.srd" type="text" placeholder="2025-01-01 00:00:00" required />
      </div>
      <button type="submit">生成台账</button>
    </form>
    <div v-if="result">
      <h3>处理结果</h3>
      <div :style="{color: result.success ? 'green' : 'red'}">{{ result.message }}</div>
      <div v-if="result.success">
        <h4>装机地址台账：</h4>
        <table v-if="result.installLedgers && result.installLedgers.length" border="1">
          <thead>
            <tr>
              <th>orderId</th>
              <th>orderLineId</th>
              <th>installId</th>
              <th>installItemId</th>
              <th>userId</th>
              <th>address</th>
              <th>modifyTag</th>
              <th>startDate</th>
              <th>endDate</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(ledger, idx) in result.installLedgers" :key="idx">
              <td>{{ ledger.orderId }}</td>
              <td>{{ ledger.orderLineId }}</td>
              <td>{{ ledger.installId }}</td>
              <td>{{ ledger.installItemId }}</td>
              <td>{{ ledger.userId }}</td>
              <td>{{ ledger.address }}</td>
              <td>{{ ledger.modifyTag }}</td>
              <td>{{ ledger.startDate }}</td>
              <td>{{ ledger.endDate }}</td>
            </tr>
          </tbody>
        </table>
        <div v-else>无装机地址台账</div>
        <h4>付费关系台账：</h4>
        <table v-if="result.payRelationLedgers && result.payRelationLedgers.length" border="1">
          <thead>
            <tr>
              <th>orderId</th>
              <th>orderLineId</th>
              <th>payRelationId</th>
              <th>userId</th>
              <th>accountId</th>
              <th>chargeCategory</th>
              <th>modifyTag</th>
              <th>startDate</th>
              <th>endDate</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(ledger, idx) in result.payRelationLedgers" :key="idx">
              <td>{{ ledger.orderId }}</td>
              <td>{{ ledger.orderLineId }}</td>
              <td>{{ ledger.payRelationId }}</td>
              <td>{{ ledger.userId }}</td>
              <td>{{ ledger.accountId }}</td>
              <td>{{ ledger.chargeCategory }}</td>
              <td>{{ ledger.modifyTag }}</td>
              <td>{{ ledger.startDate }}</td>
              <td>{{ ledger.endDate }}</td>
            </tr>
          </tbody>
        </table>
        <div v-else>无付费关系台账</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MemberRelationSupplementPage',
  data() {
    return {
      form: {
        orderId: '',
        orderLineId: '',
        snUserId: '',
        parentSerialNumber: '',
        srd: ''
      },
      result: null
    };
  },
  methods: {
    async handleSubmit() {
      this.result = null;
      try {
        const resp = await axios.post('/api/member-relation-supplement/supplement', {
          orderId: this.form.orderId,
          orderLineId: this.form.orderLineId,
          snUserId: this.form.snUserId,
          parentSerialNumber: this.form.parentSerialNumber,
          srd: this.form.srd
        });
        this.result = resp.data;
      } catch (e) {
        this.result = { success: false, message: '请求失败: ' + (e.response?.data?.message || e.message) };
      }
    }
  }
};
</script>

<style scoped>
.member-relation-supplement-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}
.member-relation-supplement-page form > div {
  margin-bottom: 12px;
}
.member-relation-supplement-page table {
  margin-top: 16px;
  width: 100%;
  border-collapse: collapse;
}
.member-relation-supplement-page th, .member-relation-supplement-page td {
  padding: 4px 8px;
  text-align: center;
}
</style>