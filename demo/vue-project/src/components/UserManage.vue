<template>
  <div style="padding: 30px;">
    <h2>用户管理</h2>
    <el-table :data="users" style="width: 100%; margin-bottom: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-button type="success" @click="showAddDialog = true">新增用户</el-button>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑用户" width="400px">
      <el-form :model="editForm">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updateUser">保存</el-button>
      </template>
    </el-dialog>

    <!-- 新增弹窗 -->
    <el-dialog v-model="showAddDialog" title="新增用户" width="400px">
      <el-form :model="addForm">
        <el-form-item label="用户名">
          <el-input v-model="addForm.username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="addForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addUser">新增</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const users = ref([
  { id: 1, username: '张三', email: 'zhangsan@example.com' },
  { id: 2, username: '李四', email: 'lisi@example.com' },
  { id: 3, username: '王五', email: 'wangwu@example.com' }
])

const showEditDialog = ref(false)
const showAddDialog = ref(false)
const editForm = ref({ id: null, username: '', email: '' })
const addForm = ref({ username: '', email: '' })

function editUser(user) {
  editForm.value = { ...user }
  showEditDialog.value = true
}

function updateUser() {
  const idx = users.value.findIndex(u => u.id === editForm.value.id)
  if (idx !== -1) {
    users.value[idx] = { ...editForm.value }
  }
  showEditDialog.value = false
}

function addUser() {
  const newId = users.value.length ? Math.max(...users.value.map(u => u.id)) + 1 : 1
  users.value.push({ id: newId, ...addForm.value })
  addForm.value = { username: '', email: '' }
  showAddDialog.value = false
}

function deleteUser(id) {
  users.value = users.value.filter(u => u.id !== id)
}
</script>