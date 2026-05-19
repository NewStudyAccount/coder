<template>
  <div class="role-list">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.roleName" placeholder="请输入角色名称" clearable />
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
        <span>角色列表</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleKey" label="角色标识" width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleAssignMenu(row)">分配权限</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" title="分配权限" width="500px" destroy-on-close>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="selectedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, getRoleMenuIds, assignRoleMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'

const loading = ref(false)
const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const menuDialogVisible = ref(false)
const currentRoleId = ref<number>(0)
const selectedMenuIds = ref<number[]>([])
const menuTree = ref<any[]>([])
const menuTreeRef = ref<any>()

const queryForm = reactive({
  roleName: '',
  status: undefined as number | undefined
})

const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  roleName: '',
  roleKey: '',
  sort: 0,
  status: 1,
  remark: ''
})

const formRules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryForm)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => {
  queryForm.roleName = ''
  queryForm.status = undefined
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, { id: undefined, roleName: '', roleKey: '', sort: 0, status: 1, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateRole(form)
        } else {
          await addRole(form)
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
  ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleAssignMenu = async (row: any) => {
  currentRoleId.value = row.id
  const menuRes = await getMenuTree()
  menuTree.value = menuRes.data || []
  const roleMenuRes = await getRoleMenuIds(row.id)
  selectedMenuIds.value = roleMenuRes.data || []
  menuDialogVisible.value = true
}

const handleMenuSubmit = async () => {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  const menuIds = [...checkedKeys, ...halfCheckedKeys]
  await assignRoleMenus(currentRoleId.value, menuIds)
  ElMessage.success('分配成功')
  menuDialogVisible.value = false
}

onMounted(() => loadData())
</script>

<style scoped>
.search-form {
  margin-bottom: 10px;
}
</style>
