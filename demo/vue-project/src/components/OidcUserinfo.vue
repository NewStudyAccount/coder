<template>
  <div class="oidc-userinfo">
    <h2>OIDC 用户信息</h2>

    <div class="row">
      <label>access_token</label>
      <input v-model="accessToken" placeholder="从 localStorage 读取或手动粘贴" />
      <button @click="loadFromStorage">从本地读取</button>
    </div>

    <div class="row">
      <button @click="fetchUserinfo">获取用户信息（/oidc2/userinfo）</button>
      <button @click="refreshAccessToken">使用 refresh_token 刷新 access_token</button>
      <button @click="gotoLogin">去登录页</button>
    </div>

    <div v-if="error" class="error">错误：{{ error }}</div>

    <div v-if="userinfo" class="panel">
      <h3>用户信息</h3>
      <pre>{{ userinfo }}</pre>
    </div>

    <div class="panel">
      <h3>本地令牌</h3>
      <ul class="token-list">
        <li><b>access_token</b>: {{ local.access_token }}</li>
        <li><b>id_token</b>: {{ local.id_token }}</li>
        <li><b>refresh_token</b>: {{ local.refresh_token }}</li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OidcUserinfo',
  data() {
    return {
      accessToken: '',
      error: '',
      userinfo: null,
      local: {
        access_token: '',
        id_token: '',
        refresh_token: ''
      }
    };
  },
  mounted() {
    this.loadFromStorage();
  },
  methods: {
    loadFromStorage() {
      this.local.access_token = localStorage.getItem('oidc.access_token') || '';
      this.local.id_token = localStorage.getItem('oidc.id_token') || '';
      this.local.refresh_token = localStorage.getItem('oidc.refresh_token') || '';
      if (!this.accessToken && this.local.access_token) {
        this.accessToken = this.local.access_token;
      }
    },
    async fetchUserinfo() {
      this.error = '';
      this.userinfo = null;
      const at = this.accessToken || this.local.access_token;
      if (!at) {
        this.error = '缺少 access_token';
        return;
      }
      try {
        const resp = await fetch('/oidc2/userinfo', {
          headers: { Authorization: `Bearer ${at}` }
        });
        if (!resp.ok) {
          const e = await resp.json().catch(() => ({}));
          throw new Error(e.error || (`HTTP ${resp.status}`));
        }
        this.userinfo = await resp.json();
      } catch (e) {
        this.error = e?.message || String(e);
      }
    },
    async refreshAccessToken() {
      this.error = '';
      const rt = this.local.refresh_token;
      if (!rt) {
        this.error = '本地无 refresh_token';
        return;
      }
      try {
        const form = new URLSearchParams({
          grant_type: 'refresh_token',
          refresh_token: rt,
          client_id: 'demo-client',
          client_secret: 'demo-secret'
        });
        const resp = await fetch('/oidc2/token', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: form.toString()
        });
        if (!resp.ok) {
          const e = await resp.json().catch(() => ({}));
          throw new Error(e.error || (`HTTP ${resp.status}`));
        }
        const data = await resp.json();
        // 更新本地 access_token 与 id_token
        localStorage.setItem('oidc.access_token', data.access_token || '');
        localStorage.setItem('oidc.id_token', data.id_token || '');
        // 可选择是否轮换 refresh_token，这里后端未轮换，仍沿用原值
        this.loadFromStorage();
        this.accessToken = this.local.access_token;
      } catch (e) {
        this.error = e?.message || String(e);
      }
    },
    gotoLogin() {
      this.$router.push({ path: '/oidc2/login' });
    }
  }
};
</script>

<style scoped>
.oidc-userinfo {
  max-width: 760px;
  margin: 20px auto;
  padding: 16px;
}
.row {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.row label {
  width: 140px;
  color: #333;
}
.row input {
  flex: 1;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
button {
  padding: 8px 14px;
  background: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button + button { margin-left: 8px; }
button:hover { background: #389d70; }
.error { color: #c0392b; margin: 8px 0; }
.panel {
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  padding: 12px;
  margin-top: 12px;
}
.token-list { list-style: none; padding: 0; margin: 0; }
.token-list li { margin: 6px 0; word-break: break-all; }
pre {
  background: #f7f7f7;
  padding: 12px;
  border-radius: 6px;
  overflow: auto;
}
</style>