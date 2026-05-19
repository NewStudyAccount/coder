<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="user-card">
            <el-avatar :size="80" :src="userStore.userInfo.avatar" icon="User" />
            <h3 class="user-name">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</h3>
            <p class="user-role">{{ userStore.userInfo.roleName || '普通用户' }}</p>
            <p class="user-dept">{{ userStore.userInfo.deptName || '未分配部门' }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>基本信息</span>
          </template>
          <el-form :model="profileForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input :model-value="userStore.userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" style="margin-top: 20px">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUser, resetPassword } from '@/api/user'

const userStore = useUserStore()

const profileForm = reactive({
  nickname: userStore.userInfo.nickname || '',
  phone: userStore.userInfo.phone || '',
  email: userStore.userInfo.email || ''
})

const pwdFormRef = ref<FormInstance>()
const pwdForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPwd = (_rule: any, value: string, callback: Function) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const handleSave = async () => {
  try {
    await updateUser({
      id: userStore.userInfo.id,
      username: userStore.userInfo.username,
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email
    })
    const newInfo = { ...userStore.userInfo, ...profileForm }
    userStore.setUserInfo(newInfo)
    ElMessage.success('保存成功')
  } catch (e) {
    console.error(e)
  }
}

const handleChangePwd = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await resetPassword(userStore.userInfo.id, pwdForm.newPassword)
        ElMessage.success('密码修改成功')
        pwdForm.newPassword = ''
        pwdForm.confirmPassword = ''
      } catch (e) {
        console.error(e)
      }
    }
  })
}
</script>

<style scoped>
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.user-name {
  margin-top: 15px;
  font-size: 18px;
  color: #303133;
}

.user-role {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}

.user-dept {
  margin-top: 5px;
  font-size: 14px;
  color: #909399;
}
</style>
