<template>
  <div class="external-login">
    <h2>选择第三方登录</h2>

    <div class="row">
      <label>Client ID</label>
      <input v-model="clientId" />
    </div>
    <div class="row">
      <label>Redirect URI</label>
      <input v-model="redirectUri" />
    </div>
    <div class="row">
      <label>State</label>
      <input v-model="state" />
    </div>

    <div class="providers">
      <button class="btn github" @click="loginWith('github')">
        使用 GitHub 登录
      </button>
      <button class="btn google" @click="loginWith('google')">
        使用 Google 登录
      </button>
    </div>

    <p class="tip">
      本页会跳转到自建 OIDC 服务的外部登录入口：
      /oidc2/external/{provider}/login?client_id=...&redirect_uri=...&state=...
      回调后将重定向回 redirect_uri（默认 /callback），携带我方 code。
    </p>

    <div v-if="error" class="error">错误：{{ error }}</div>
  </div>
</template>

<script>
export default {
  name: 'ExternalLogin',
  data() {
    return {
      clientId: 'demo-client',
      redirectUri: window.location.origin + '/callback',
      state: Math.random().toString(36).slice(2, 10),
      error: ''
    };
  },
  methods: {
    loginWith(provider) {
      try {
        this.error = '';
        const q = new URLSearchParams({
          client_id: this.clientId,
          redirect_uri: this.redirectUri,
          state: this.state
        });
        window.location.href = `/oidc2/external/${provider}/login?${q.toString()}`;
      } catch (e) {
        this.error = e?.message || String(e);
      }
    }
  }
};
</script>

<style scoped>
.external-login {
  max-width: 520px;
  margin: 20px auto;
  padding: 16px;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
}
.row {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.row label {
  width: 120px;
  color: #333;
}
.row input {
  flex: 1;
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.providers {
  display: flex;
  gap: 12px;
  margin: 10px 0 16px;
}
.btn {
  padding: 8px 14px;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.github { background: #24292e; }
.github:hover { background: #111417; }
.google { background: #4285F4; }
.google:hover { background: #2a6fe3; }
.error { color: #c0392b; margin-top: 8px; }
.tip { color: #666; margin-top: 10px; font-size: 13px; }
</style>