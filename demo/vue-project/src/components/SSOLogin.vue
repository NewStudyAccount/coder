<template>
  <div class="sso-container">
    <div class="login-card">
      <div class="login-header">
        <img src="../assets/logo.svg" alt="Logo" class="logo" />
        <h1>单点登录 (SSO)</h1>
        <p class="subtitle">统一身份认证平台</p>
      </div>

      <div class="login-methods">
        <div class="method-item" @click="handleOIDCLogin">
          <div class="icon-wrapper oidc">
            <span class="icon">O</span>
          </div>
          <div class="method-info">
            <h3>OIDC 登录</h3>
            <p>使用 OpenID Connect 标准登录</p>
          </div>
          <span class="arrow">→</span>
        </div>

        <div class="method-item" @click="handleExternalLogin">
          <div class="icon-wrapper external">
            <span class="icon">E</span>
          </div>
          <div class="method-info">
            <h3>第三方登录</h3>
            <p>通过外部信任的身份提供商登录</p>
          </div>
          <span class="arrow">→</span>
        </div>
      </div>

      <div class="footer">
        <p>&copy; 2026 统一身份认证系统. 保留所有权利。</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SSOLogin',
  methods: {
    handleOIDCLogin() {
      // 跳转到现有的 OIDC 登录演示页面
      this.$router.push('/oidc2/login');
    },
    handleExternalLogin() {
      // 跳转到外部登录演示页面
      this.$router.push('/external-login');
    }
  }
};
</script>

<style scoped>
.sso-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.login-card {
  background: white;
  width: 100%;
  max-width: 450px;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 60px;
  height: 60px;
  margin-bottom: 16px;
}

h1 {
  font-size: 24px;
  color: #2c3e50;
  margin: 0;
}

.subtitle {
  color: #7f8c8d;
  margin-top: 8px;
  font-size: 14px;
}

.login-methods {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-item:hover {
  background-color: #f8fafc;
  border-color: #42b883;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  font-weight: bold;
  font-size: 20px;
}

.oidc {
  background-color: #42b883;
}

.external {
  background-color: #3498db;
}

.method-info {
  flex: 1;
}

.method-info h3 {
  margin: 0;
  font-size: 16px;
  color: #2c3e50;
}

.method-info p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #95a5a6;
}

.arrow {
  color: #bdc3c7;
  font-size: 20px;
}

.footer {
  margin-top: 40px;
  text-align: center;
  font-size: 12px;
  color: #bdc3c7;
}
</style>
