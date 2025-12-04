<template>
  <div class="site-group-relation-page">
    <h2>site成员修改群组关系台账生成</h2>
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
        <label>成员号码(serial_number)：</label>
        <input v-model="form.serialNumber" type="text" required />
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
        <h4>生成的台账信息：</h4>
        <table border="1">
          <thead>
            <tr>
              <th>orderId</th>
              <th>orderLineId</th>
              <th>relationTypeCode</th>
              <th>userIdA</th>
              <th>userIdB</th>
              <th>serialNumberA</th>
              <th>serialNumberB</th>
              <th>callSequence</th>
              <th>isPrimaryNumber</th>
              <th>roleCodeA</th>
              <th>roleCodeB</th>
              <th>modifyTag</th>
              <th>startDate</th>
              <th>endDate</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(ledger, idx) in result.uuLedgers" :key="idx">
              <td>{{ ledger.orderId }}</td>
              <td>{{ ledger.orderLineId }}</td>
              <td>{{ ledger.relationTypeCode }}</td>
              <td>{{ ledger.userIdA }}</td>
              <td>{{ ledger.userIdB }}</td>
              <td>{{ ledger.serialNumberA }}</td>
              <td>{{ ledger.serialNumberB }}</td>
              <td>{{ ledger.callSequence }}</td>
              <td>{{ ledger.isPrimaryNumber }}</td>
              <td>{{ ledger.roleCodeA }}</td>
              <td>{{ ledger.roleCodeB }}</td>
              <td>{{ ledger.modifyTag }}</td>
              <td>{{ ledger.startDate }}</td>
              <td>{{ ledger.endDate }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SiteGroupRelationPage',
  data() {
    return {
      form: {
        orderId: '',
        orderLineId: '',
        snUserId: '',
        serialNumber: '',
        srd: ''
      },
      result: null
    };
  },
  methods: {
    async handleSubmit() {
      this.result = null;
      try {
        const resp = await axios.post('/api/site-group-relation/modify', {
          orderId: this.form.orderId,
          orderLineId: this.form.orderLineId,
          snUserId: this.form.snUserId,
          serialNumber: this.form.serialNumber,
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
.site-group-relation-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
.site-group-relation-page form > div {
  margin-bottom: 12px;
}
.site-group-relation-page table {
  margin-top: 16px;
  width: 100%;
  border-collapse: collapse;
}
.site-group-relation-page th, .site-group-relation-page td {
  padding: 4px 8px;
  text-align: center;
}
</style>