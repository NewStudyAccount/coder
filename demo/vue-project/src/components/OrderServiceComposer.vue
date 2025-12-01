<!--<template>-->
<!--  <div class="order-service-composer">-->
<!--    <h2>订单构件服务编排</h2>-->
<!--    <form @submit.prevent="handleQuery">-->
<!--      <label>-->
<!--        订单ID:-->
<!--        <input v-model="orderId" required />-->
<!--      </label>-->
<!--      <label>-->
<!--        trade_type_code:-->
<!--        <input v-model="tradeTypeCode" required />-->
<!--      </label>-->
<!--      <label>-->
<!--        scene_type:-->
<!--        <input v-model="sceneType" required />-->
<!--      </label>-->
<!--      <label>-->
<!--        product_type_code:-->
<!--        <input v-model="productTypeCode" />-->
<!--      </label>-->
<!--      <label>-->
<!--        net_type_code:-->
<!--        <input v-model="netTypeCode" />-->
<!--      </label>-->
<!--      <button type="submit">查询并编排服务</button>-->
<!--    </form>-->
<!--    <div v-if="orderLine">-->
<!--      <h3>订单行信息</h3>-->
<!--      <pre>{{ orderLine }}</pre>-->
<!--    </div>-->
<!--    <div v-if="services.length">-->
<!--      <h3>编排结果</h3>-->
<!--      <ul>-->
<!--        <li v-for="(srv, idx) in services" :key="idx">{{ srv }}</li>-->
<!--      </ul>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--export default {-->
<!--  name: 'OrderServiceComposer',-->
<!--  data() {-->
<!--    return {-->
<!--      orderId: '',-->
<!--      tradeTypeCode: '',-->
<!--      sceneType: '',-->
<!--      productTypeCode: '',-->
<!--      netTypeCode: '',-->
<!--      orderLine: null,-->
<!--      services: []-->
<!--    }-->
<!--  },-->
<!--  methods: {-->
<!--    handleQuery() {-->
<!--      // 模拟查询订单行-->
<!--      this.orderLine = {-->
<!--        order_id: this.orderId,-->
<!--        line_level: '0',-->
<!--        cancel_tag: '0',-->
<!--        trade_type_code: this.tradeTypeCode,-->
<!--        scene_type: this.sceneType,-->
<!--        product_type_code: this.productTypeCode,-->
<!--        net_type_code: this.netTypeCode-->
<!--      }-->
<!--      this.services = this.composeServices()-->
<!--    },-->
<!--    composeServices() {-->
<!--      const t = this.tradeTypeCode-->
<!--      const s = this.sceneType-->
<!--      const p = this.productTypeCode-->
<!--      const n = this.netTypeCode-->
<!--      let result = []-->

<!--      // 业务逻辑分支-->
<!--      if (t == '127' && s == '12700') {-->
<!--        result.push('公共构建服务一')-->
<!--        result.push('公共构建服务十七')-->
<!--        result.push('公共构建服务二')-->
<!--        result.push('公共构建服务三十')-->
<!--        if (p === 'DDI') result.push('公共构建服务二十三')-->
<!--        if (n === '31' || n === '32') result.push('功能服务26')-->
<!--      }-->
<!--      if (t == '3410' && (s == '34100' || s == '34101')) {-->
<!--        result.push('公共构建服务三')-->
<!--      }-->
<!--      if (t == '279' && s == '27900') {-->
<!--        result.push('公共构建服务八')-->
<!--      }-->
<!--      if (t == '101' && s == '10100') {-->
<!--        result.push('公共构建服务七')-->
<!--        result.push('公共构建服务二')-->
<!--        result.push('公共构建服务十四')-->
<!--      }-->
<!--      if (t == '160' && s == '16000') {-->
<!--        result.push('公共构建服务二')-->
<!--        result.push('公共构建服务十四')-->
<!--      }-->
<!--      if (t == '192' && s == '19200') {-->
<!--        result.push('公共构建服务九')-->
<!--        result.push('公共构建服务十')-->
<!--        result.push('公共构建服务十一')-->
<!--        result.push('公共构建服务四')-->
<!--        result.push('公共构建服务二十七')-->
<!--        result.push('公共构建服务三十二')-->
<!--        result.push('公共构建服务三十一')-->
<!--      }-->
<!--      if (n === '31' || n === '32') {-->
<!--        result.push('功能服务三十三')-->
<!--      }-->
<!--      if (t == '10' && s == '10000') {-->
<!--        result.push('构建服务二十三')-->
<!--        result.push('构建服务三十')-->
<!--        if (n === '31' || n === '32') result.push('功能服务6')-->
<!--      }-->
<!--      if (t == '70' && s == '70000') {-->
<!--        result.push('公共构建服务十四')-->
<!--        result.push('公共构建服务九')-->
<!--        result.push('公共构建服务十')-->
<!--        result.push('公共构建服务十一')-->
<!--        result.push('公共构建服务四')-->
<!--        result.push('公共构建服务二十七')-->
<!--        result.push('公共构建服务三十二')-->
<!--      }-->
<!--      if (t == '112' && s == '11200') {-->
<!--        result.push('构件服务一')-->
<!--        result.push('构件服务十七')-->
<!--        result.push('功能服务六')-->
<!--      }-->
<!--      if (t == '127' && s == '12705') {-->
<!--        result.push('公共构建服务三十三')-->
<!--        result.push('公共构建服务十七')-->
<!--      }-->
<!--      if (t == '127' && s == '12701') {-->
<!--        result.push('构建服务1标签页序列45')-->
<!--        if (p === 'DDI') result.push('公共构建服务二十三')-->
<!--      }-->
<!--      if (t == '128' && s == '12800') {-->
<!--        if (p === 'DDI') result.push('公共构建服务二十三')-->
<!--      }-->
<!--      return result-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style scoped>-->
<!--.order-service-composer {-->
<!--  border: 1px solid #eee;-->
<!--  padding: 24px;-->
<!--  width: 480px;-->
<!--  margin: 30px auto;-->
<!--  background: #f9f9f9;-->
<!--  border-radius: 8px;-->
<!--}-->
<!--form label {-->
<!--  display: block;-->
<!--  margin-bottom: 10px;-->
<!--}-->
<!--input {-->
<!--  margin-left: 8px;-->
<!--  padding: 4px 8px;-->
<!--}-->
<!--button {-->
<!--  margin-top: 12px;-->
<!--  padding: 8px 20px;-->
<!--  font-size: 16px;-->
<!--  cursor: pointer;-->
<!--}-->
<!--h3 {-->
<!--  margin-top: 20px;-->
<!--}-->
<!--</style>-->