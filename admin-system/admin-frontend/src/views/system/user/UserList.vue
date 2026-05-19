<template>
  <div class="user-list">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="flex-between mb-10">
        <span>用户列表</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="240">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleAssignRole(row)">分配角色</el-button>
            <el-button type="info" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="form.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px" destroy-on-close>
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
          {{ role.roleName }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser, getUserRoleIds, assignUserRoles, resetPassword } from '@/api/user'
import { getRoleList } from '@/api/role'
import { getDeptTree } from '@/api/dept'

const loading = ref(false)
const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleDialogVisible = ref(false)
const selectedRoleIds = ref<number[]>([])
const currentUserId = ref<number>(0)
const roleList = ref<any[]>([])
const deptTree = ref<any[]>([])

const queryForm = reactive({
  username: '',
  status: undefined as number | undefined
})

const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  deptId: undefined as number | undefined,
  status: 1
})

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryForm)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const loadDeptTree = async () => {
  try {
    const res = await getDeptTree()
    deptTree.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => loadData()
const handleReset = () => {
  queryForm.username = ''
  queryForm.status = undefined
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(form, { id: undefined, username: '', password: '', nickname: '', phone: '', email: '', deptId: undefined, status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateUser(form)
        } else {
          await addUser(form)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        loadData()
      } catch (e) {
        console.error(e)
      }
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleAssignRole = async (row: any) => {
  currentUserId.value = row.id
  const roleRes = await getRoleList()
  roleList.value = roleRes.data || []
  const userRoleRes = await getUserRoleIds(row.id)
  selectedRoleIds.value = userRoleRes.data || []
  roleDialogVisible.value = true
}

const handleRoleSubmit = async () => {
  await assignUserRoles(currentUserId.value, selectedRoleIds.value)
  ElMessage.success('分配成功')
  roleDialogVisible.value = false
  loadData()
}

const handleResetPwd = (row: any) => {
  ElMessageBox.prompt('请输入新密码', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^.{6,}$/,
    inputErrorMessage: '密码长度不能少于6位'
  }).then(async ({ value }) => {
    await resetPassword(row.id, value)
    ElMessage.success('密码重置成功')
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadDeptTree()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 10px;
}
</style>
