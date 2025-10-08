<template>
  <div style="padding: 30px;">
    <h2>角色管理</h2>
    <el-button type="primary" @click="showAddDialog = true" style="margin-bottom: 16px;">新增角色</el-button>
    <el-table :data="roles" style="width: 100%; margin-bottom: 20px;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名" />
      <el-table-column prop="description" label="描述" />
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
      <el-table-column label="操作" width="260">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editRole(scope.row)">编辑</el-button>
          <el-button type="warning" size="small" @click="editPermissions(scope.row)">权限绑定</el-button>
          <el-button type="danger" size="small" @click="deleteRole(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增角色弹窗 -->
    <el-dialog v-model="showAddDialog" title="新增角色" width="400px">
      <el-form>
        <el-form-item label="角色名">
          <el-input v-model="addForm.roleName" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="addForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑角色弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑角色" width="400px">
      <el-form>
        <el-form-item label="角色名">
          <el-input v-model="editForm.roleName" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updateRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 权限绑定弹窗 -->
    <el-dialog v-model="showPermDialog" title="绑定权限" width="400px">
      <el-form>
        <el-form-item label="角色名">
          <el-input v-model="permForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="权限">
          <el-checkbox-group v-model="permForm.permissions">
            <el-checkbox label="ADMIN">ADMIN</el-checkbox>
            <el-checkbox label="USER">USER</el-checkbox>
            <el-checkbox label="GUEST">GUEST</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPermDialog = false">取消</el-button>
        <el-button type="primary" @click="savePermissions">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const roles = ref([])
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const showPermDialog = ref(false)

const addForm = ref({ roleName: '', description: '' })
const editForm = ref({ id: null, roleName: '', description: '' })
const permForm = ref({ id: null, roleName: '', permissions: [] })

// 加载角色列表
async function fetchRoles() {
  try {
    const res = await axios.get('/role/list')
    roles.value = res.data.data
  } catch (e) {
    console.error('获取角色列表失败', e)
  }
}

// 新增角色
async function addRole() {
  try {
    await axios.post('/role/add', {
      roleName: addForm.value.roleName,
      description: addForm.value.description,
      permissions: []
    })
    showAddDialog.value = false
    addForm.value = { roleName: '', description: '' }
    await fetchRoles()
  } catch (e) {
    console.error('新增角色失败', e)
  }
}

// 编辑角色
function editRole(role) {
  editForm.value = { id: role.id, roleName: role.roleName, description: role.description }
  showEditDialog.value = true
}

async function updateRole() {
  try {
    await axios.post('/role/update?id=' + editForm.value.id, {
      roleName: editForm.value.roleName,
      description: editForm.value.description
    })
    showEditDialog.value = false
    await fetchRoles()
  } catch (e) {
    console.error('编辑角色失败', e)
  }
}

// 删除角色
async function deleteRole(role) {
  try {
    await axios.post('/role/delete?id=' + role.id)
    await fetchRoles()
  } catch (e) {
    console.error('删除角色失败', e)
  }
}

// 权限绑定
async function editPermissions(role) {
  try {
    const res = await axios.get('/role/permissions?id=' + role.id)
    permForm.value = {
      id: role.id,
      roleName: role.roleName,
      permissions: res.data.data
    }
    showPermDialog.value = true
  } catch (e) {
    console.error('获取角色权限失败', e)
  }
}

async function savePermissions() {
  try {
    await axios.post('/role/permissions?id=' + permForm.value.id, permForm.value.permissions)
    showPermDialog.value = false
    await fetchRoles()
  } catch (e) {
    console.error('保存角色权限失败', e)
  }
}

// 页面加载时获取数据
fetchRoles()
</script>