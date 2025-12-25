<template>
  <div class="oidc-callback">
    <h2>OIDC 回调</h2>
    <div v-if="loading">正在处理授权码(code)...</div>
    <div v-else>
      <div v-if="error" class="error">错误：{{ error }}</div>
      <div v-else>
        <p>授权成功，已获取令牌：</p>
        <ul class="token-list">
          <li><b>access_token</b>: {{ tokens.access_token }}</li>
          <li><b>id_token</b>: {{ tokens.id_token }}</li>
          <li><b>refresh_token</b>: {{ tokens.refresh_token }}</li>
          <li><b>expires_in</b>: {{ tokens.expires_in }}</li>
        </ul>
        <div class="actions">
          <button @click="fetchUserinfo">获取用户信息</button>
          <button @click="gotoUserinfo">跳转到用户信息页</button>
        </div>
        <div v-if="userinfo">
          <h3>用户信息</h3>
          <pre>{{ userinfo }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OidcCallback',
  data() {
    return {
      loading: true,
      error: '',
      tokens: {
        access_token: '',
        id_token: '',
        refresh_token: '',
        expires_in: 0
      },
      userinfo: null
    };
  },
  async mounted() {
    // 解析 URL 中的 ?code=...&state=...
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const state = params.get('state') || '';
    if (!code) {
      this.error = '缺少 code';
      this.loading = false;
      return;
    }
    try {
      // 使用 Basic 认证（client_id:client_secret），或直接表单携带 client_id/client_secret
      const form = new URLSearchParams({
        grant_type: 'authorization_code',
        code,
        redirect_uri: window.location.origin + '/callback',
        client_id: 'demo-client',
        client_secret: 'demo-secret'
      });
      const resp = await fetch('/oidc2/token', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: form.toString()
      });
      if (!resp.ok) {
        const e = await resp.json().catch(() => ({}));
        throw new Error(e.error || (`HTTP ${resp.status}`));
      }
      const data = await resp.json();
      this.tokens = {
        access_token: data.access_token,
        id_token: data.id_token,
        refresh_token: data.refresh_token,
        expires_in: data.expires_in
      };
      // 存储到 localStorage
      localStorage.setItem('oidc.access_token', this.tokens.access_token);
      localStorage.setItem('oidc.id_token', this.tokens.id_token);
      localStorage.setItem('oidc.refresh_token', this.tokens.refresh_token || '');
      this.loading = false;
    } catch (e) {
      this.error = e?.message || String(e);
      this.loading = false;
    }
  },
  methods: {
    async fetchUserinfo() {
      this.userinfo = null;
      this.error = '';
      const at = localStorage.getItem('oidc.access_token');
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
    gotoUserinfo() {
      this.$router.push({ path: '/userinfo' });
    }
  }
};
</script>

<style scoped>
.oidc-callback {
  max-width: 720px;
  margin: 20px auto;
  padding: 16px;
}
.token-list {
  list-style: none;
  padding: 0;
  margin: 12px 0;
}
.token-list li { margin: 6px 0; word-break: break-all; }
.actions { display: flex; gap: 10px; margin: 12px 0; }
button {
  padding: 8px 14px;
  background: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover { background: #389d70; }
.error { color: #c0392b; }
pre {
  background: #f7f7f7;
  padding: 12px;
  border-radius: 6px;
  overflow: auto;
}
</style>