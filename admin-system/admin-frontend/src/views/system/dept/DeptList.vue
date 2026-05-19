<template>
  <div class="dept-list">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="queryForm.deptName" placeholder="请输入部门名称" clearable />
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
        <span>部门列表</span>
        <el-button type="primary" @click="handleAdd(0)">
          <el-icon><Plus /></el-icon>新增根部门
        </el-button>
      </div>

      <el-table
        :data="deptTree"
        border
        row-key="id"
        :tree-props="{ children: 'children' }"
        v-loading="loading"
        default-expand-all
      >
        <el-table-column prop="deptName" label="部门名称" width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAdd(row.id)">新增</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="deptTreeForSelect"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择上级部门"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
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
import { getDeptTree, addDept, updateDept, deleteDept } from '@/api/dept'

const loading = ref(false)
const deptTree = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryForm = reactive({
  deptName: '',
  status: undefined as number | undefined
})

const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  deptName: '',
  parentId: 0,
  sort: 0,
  leader: '',
  status: 1
})

const formRules: FormRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const deptTreeForSelect = computed(() => {
  return [{ id: 0, deptName: '根部门', children: deptTree.value }]
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDeptTree(queryForm)
    deptTree.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => {
  queryForm.deptName = ''
  queryForm.status = undefined
  loadData()
}

const handleAdd = (parentId: number) => {
  dialogTitle.value = '新增部门'
  Object.assign(form, { id: undefined, deptName: '', parentId, sort: 0, leader: '', status: 1 })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑部门'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateDept(form)
        } else {
          await addDept(form)
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
  ElMessageBox.confirm(`确定要删除部门"${row.deptName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDept(row.id)
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
