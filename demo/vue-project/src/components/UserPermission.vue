<template>
  <div style="padding: 30px;">
    <h2>用户权限管理</h2>
    <el-table :data="users" style="width: 100%; margin-bottom: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="permissions" label="权限">
        <template #default="scope">
          <el-tag
            v-for="perm in scope.row.permissions"
            :key="perm"
            type="info"
            style="margin-right: 4px;"
          >{{ perm }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editPermission(scope.row)">编辑权限</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑权限弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑用户权限" width="400px">
      <el-form>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="权限">
          <el-checkbox-group v-model="editForm.permissions">
            <el-checkbox label="ADMIN">ADMIN</el-checkbox>
            <el-checkbox label="USER">USER</el-checkbox>
            <el-checkbox label="GUEST">GUEST</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updatePermission">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const users = ref([
  { id: 1, username: '张三', permissions: ['ADMIN', 'USER'] },
  { id: 2, username: '李四', permissions: ['USER'] },
  { id: 3, username: '王五', permissions: ['GUEST'] }
])

const showEditDialog = ref(false)
const editForm = ref({ id: null, username: '', permissions: [] })

function editPermission(user) {
  editForm.value = { ...user, permissions: [...user.permissions] }
  showEditDialog.value = true
}

function updatePermission() {
  const idx = users.value.findIndex(u => u.id === editForm.value.id)
  if (idx !== -1) {
    users.value[idx].permissions = [...editForm.value.permissions]
  }
  showEditDialog.value = false
}
</script>