<template>
  <div class="login-container">
    <div class="form-container">
      <!-- 头部区域 -->
      <div class="form-header">
        <el-icon :size="50" color="#409eff">
          <Reading />
        </el-icon>
        <h2>图书馆管理系统</h2>
        <p>欢迎登录</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="0"
        size="large"
        @keyup.enter="handleLogin"
      >
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            clearable
            maxlength="20"
            :disabled="loading"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            maxlength="20"
            :disabled="loading"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            :disabled="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>

        <!-- 底部注册链接 -->
        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">
            立即注册
          </router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Reading, User, Lock } from '@element-plus/icons-vue'

// ==================== 路由和状态管理 ====================
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// ==================== 响应式数据 ====================
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// ==================== 表单验证规则 ====================
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { 
      min: 3, 
      max: 20, 
      message: '用户名长度为3-20个字符', 
      trigger: 'blur' 
    },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { 
      min: 6, 
      max: 20, 
      message: '密码长度为6-20个字符', 
      trigger: 'blur' 
    }
  ]
}

// ==================== 方法 ====================

/**
 * 处理登录逻辑
 */
const handleLogin = async () => {
  // 检查表单引用是否存在
  if (!loginFormRef.value) {
    ElMessage.warning('表单初始化失败，请刷新页面重试')
    return
  }

  try {
    // 验证表单
    await loginFormRef.value.validate()
    
    // 开始登录
    loading.value = true

    // 调用登录接口
    const success = await userStore.login(loginForm)
    
    if (success) {
      ElMessage.success('登录成功')
      
      // 获取重定向路径，默认跳转到图书列表页
      const redirect = route.query.redirect || '/books'
      
      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        router.push(redirect)
      }, 500)
    }
  } catch (error) {
    // 表单验证失败时不显示错误信息，由表单组件自己显示
    if (error && typeof error === 'object' && !error.message) {
      return
    }
    
    // 其他错误
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ==================== 容器样式 ==================== */
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* ==================== 表单容器 ==================== */
.form-container {
  width: 100%;
  max-width: 420px;
  background: white;
  border-radius: 16px;
  padding: 48px 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  animation: slideIn 0.3s ease-out;
}

/* 表单容器动画 */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ==================== 头部样式 ==================== */
.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  margin: 16px 0 8px;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  letter-spacing: 0.5px;
}

.form-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

/* ==================== 登录按钮 ==================== */
.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

/* ==================== 底部样式 ==================== */
.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.register-link {
  color: #409eff;
  text-decoration: none;
  margin-left: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.register-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

/* ==================== 输入框样式优化 ==================== */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

/* ==================== 表单项间距 ==================== */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item:last-of-type) {
  margin-bottom: 0;
}

/* ==================== 响应式设计 ==================== */
@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }

  .form-container {
    padding: 32px 24px;
    border-radius: 12px;
  }

  .form-header h2 {
    font-size: 24px;
  }

  .form-header {
    margin-bottom: 32px;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
}

/* 小屏幕优化 */
@media (max-width: 360px) {
  .form-container {
    padding: 24px 20px;
  }

  .form-header h2 {
    font-size: 22px;
  }
}
</style>
