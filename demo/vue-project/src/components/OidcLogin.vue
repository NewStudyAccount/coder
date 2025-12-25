<template>
  <div class="oidc-login">
    <h2>OIDC 登录（直连自建 IdP /oidc2/**）</h2>
    <form @submit.prevent="doAuthorize">
      <div class="row">
        <label>用户名</label>
        <input v-model="form.username" placeholder="alice 或 bob" />
      </div>
      <div class="row">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="alice123 或 bob123" />
      </div>
      <div class="row">
        <label>Client ID</label>
        <input v-model="form.client_id" />
      </div>
      <div class="row">
        <label>Client Secret</label>
        <input v-model="form.client_secret" />
      </div>
      <div class="row">
        <label>Redirect URI</label>
        <input v-model="form.redirect_uri" />
      </div>
      <div class="row">
        <label>Scope</label>
        <input v-model="form.scope" />
      </div>
      <div class="row">
        <label>State</label>
        <input v-model="form.state" />
      </div>
      <div class="row">
        <button type="submit">Authorize（获取 code 并跳转）</button>
      </div>
    </form>

    <p class="tip">
      提示：本页面直接调用 /oidc2/authorize（GET），将用户名口令作为查询参数（仅用于演示）。
      生产中应使用登录表单+Session 或者外部 IdP 登录页面。
    </p>
    <div v-if="error" class="error">错误：{{ error }}</div>
  </div>
</template>

<script>
export default {
  name: 'OidcLogin',
  data() {
    return {
      error: '',
      form: {
        username: 'alice',
        password: 'alice123',
        client_id: 'demo-client',
        client_secret: 'demo-secret',
        redirect_uri: window.location.origin + '/callback',
        scope: 'openid profile email',
        state: Math.random().toString(36).slice(2, 10)
      }
    };
  },
  methods: {
    async doAuthorize() {
      try {
        this.error = '';
        const q = new URLSearchParams({
          response_type: 'code',
          client_id: this.form.client_id,
          redirect_uri: this.form.redirect_uri,
          scope: this.form.scope,
          state: this.form.state,
          username: this.form.username,
          password: this.form.password
        });
        // 直接跳转到授权端点，由后端重定向回 redirect_uri 携带 code/state
        window.location.href = `/oidc2/authorize?${q.toString()}`;
      } catch (e) {
        this.error = e?.message || String(e);
      }
    }
  }
};
</script>

<style scoped>
.oidc-login {
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
button {
  padding: 8px 14px;
  background: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover { background: #389d70; }
.error { color: #c0392b; margin-top: 8px; }
.tip { color: #666; margin-top: 10px; font-size: 13px; }
</style>