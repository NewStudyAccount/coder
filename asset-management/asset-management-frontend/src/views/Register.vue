<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">用户注册</h2>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="80px"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
          />
        </el-form-item>
        
        <el-form-item label="部门" prop="department">
          <el-input
            v-model="registerForm.department"
            placeholder="请输入部门"
            prefix-icon="OfficeBuilding"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            style="width: 100%"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  email: '',
  phone: '',
  department: ''
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 验证邮箱
const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (value === '') {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入有效的邮箱地址'))
  } else {
    callback()
  }
}

// 验证手机号
const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (value === '') {
    callback(new Error('请输入手机号'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入有效的手机号'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2到20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入部门', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 构造注册数据（不包含confirmPassword）
        const registerData = {
          username: registerForm.username,
          password: registerForm.password,
          realName: registerForm.realName,
          email: registerForm.email,
          phone: registerForm.phone,
          department: registerForm.department
        }
        
        await register(registerData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  width: 500px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  color: #333;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.login-link {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}
</style>
