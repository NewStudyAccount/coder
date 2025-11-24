<template>
  <div class="simple-demo">
    <h2>前端计数器示例</h2>
    <p>当前计数：{{ count }}</p>
    <button @click="count++">点击增加</button>
  </div>
</template>

<script>
export default {
  name: 'SimpleDemo',
  data() {
    return {
      count: 0
    }
  }
}
</script>

<style scoped>
.simple-demo {
  border: 1px solid #eee;
  padding: 20px;
  width: 300px;
  margin: 20px auto;
  text-align: center;
  background: #fafafa;
}
button {
  margin-top: 10px;
  padding: 6px 16px;
  font-size: 16px;
  cursor: pointer;
}
</style>