<template>
  <div class="menu-list">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="菜单名称">
          <el-input v-model="queryForm.menuName" placeholder="请输入菜单名称" clearable />
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
        <span>菜单列表</span>
        <el-button type="primary" @click="handleAdd(0)">
          <el-icon><Plus /></el-icon>新增根菜单
        </el-button>
      </div>

      <el-table
        :data="menuTree"
        border
        row-key="id"
        :tree-props="{ children: 'children' }"
        v-loading="loading"
        default-expand-all
      >
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="path" label="路由路径" width="180" />
        <el-table-column prop="component" label="组件路径" width="220" />
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 1" type="primary">目录</el-tag>
            <el-tag v-else-if="row.menuType === 2" type="success">菜单</el-tag>
            <el-tag v-else type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button v-if="row.menuType !== 3" type="primary" link @click="handleAdd(row.id)">
              新增
            </el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeForSelect"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="请选择上级菜单"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.menuType !== 3">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.menuType !== 3">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.menuType === 2">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" v-if="form.menuType === 3">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
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
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/api/menu'

const loading = ref(false)
const menuTree = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryForm = reactive({
  menuName: '',
  status: undefined as number | undefined
})

const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  menuName: '',
  parentId: 0,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  menuType: 1,
  perms: '',
  status: 1
})

const formRules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

const menuTreeForSelect = computed(() => {
  return [{ id: 0, menuName: '根目录', children: menuTree.value }]
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMenuTree(queryForm)
    menuTree.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => {
  queryForm.menuName = ''
  queryForm.status = undefined
  loadData()
}

const handleAdd = (parentId: number) => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, { id: undefined, menuName: '', parentId, path: '', component: '', icon: '', sort: 0, menuType: parentId === 0 ? 1 : 2, perms: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateMenu(form)
        } else {
          await addMenu(form)
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
  ElMessageBox.confirm(`确定要删除菜单"${row.menuName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.search-form {
  margin-bottom: 10px;
}
</style>
