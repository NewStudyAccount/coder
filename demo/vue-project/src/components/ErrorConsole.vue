<template>
  <div v-if="errors && errors.length" class="error-console">
    <div class="error-title">报错控制台</div>
    <ul>
      <li v-for="(err, idx) in errors" :key="idx">
        <span class="error-msg">{{ err }}</span>
        <button class="close-btn" @click="$emit('close', idx)">关闭</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({
  errors: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.error-console {
  background: #fff0f0;
  border: 1px solid #ffb3b3;
  color: #c00;
  margin: 16px 0 0 0;
  padding: 12px 20px 8px 20px;
  border-radius: 6px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  box-shadow: 0 2px 8px rgba(255,0,0,0.04);
}
.error-title {
  font-weight: bold;
  margin-bottom: 6px;
}
ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
li {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}
.error-msg {
  flex: 1;
  word-break: break-all;
}
.close-btn {
  background: #ffb3b3;
  border: none;
  color: #c00;
  border-radius: 3px;
  padding: 2px 10px;
  margin-left: 10px;
  cursor: pointer;
  font-size: 13px;
  transition: background 0.2s;
}
.close-btn:hover {
  background: #ff6666;
  color: #fff;
}
</style>