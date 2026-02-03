<template>
  <div class="asset-list-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="资产名称">
          <el-input v-model="searchForm.name" placeholder="请输入资产名称" clearable />
        </el-form-item>
        <el-form-item label="资产编号">
          <el-input v-model="searchForm.assetNumber" placeholder="请输入资产编号" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维修中" value="MAINTENANCE" />
            <el-option label="已报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd" :icon="Plus">新增资产</el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete" :icon="Delete">
          批量删除
        </el-button>
        <el-button type="success" @click="handleExport" :icon="Download">导出</el-button>
      </div>
      
      <!-- 表格 -->
      <el-table
        :data="tableData"
        stripe
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="name" label="资产名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="specifications" label="规格型号" />
        <el-table-column prop="location" label="存放位置" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'AVAILABLE'" type="success">可用</el-tag>
            <el-tag v-else-if="row.status === 'IN_USE'" type="warning">使用中</el-tag>
            <el-tag v-else-if="row.status === 'MAINTENANCE'" type="danger">维修中</el-tag>
            <el-tag v-else-if="row.status === 'SCRAPPED'" type="info">已报废</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购入日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)" :icon="View">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)" :icon="Edit">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <!-- 资产表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="assetFormRef"
        :model="assetForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="资产编号" prop="assetNumber">
          <el-input v-model="assetForm.assetNumber" placeholder="请输入资产编号" />
        </el-form-item>
        <el-form-item label="资产名称" prop="name">
          <el-input v-model="assetForm.name" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="assetForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格型号" prop="specifications">
          <el-input v-model="assetForm.specifications" placeholder="请输入规格型号" />
        </el-form-item>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="assetForm.location" placeholder="请输入存放位置" />
        </el-form-item>
        <el-form-item label="购入价格" prop="purchasePrice">
          <el-input-number v-model="assetForm.purchasePrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="购入日期" prop="purchaseDate">
          <el-date-picker
            v-model="assetForm.purchaseDate"
            type="date"
            placeholder="请选择购入日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="assetForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维修中" value="MAINTENANCE" />
            <el-option label="已报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="assetForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 资产详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="资产详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编号">{{ currentAsset.assetNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentAsset.name }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentAsset.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ currentAsset.specifications }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ currentAsset.location }}</el-descriptions-item>
        <el-descriptions-item label="购入价格">¥{{ currentAsset.purchasePrice }}</el-descriptions-item>
        <el-descriptions-item label="购入日期">{{ currentAsset.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentAsset.status === 'AVAILABLE'" type="success">可用</el-tag>
          <el-tag v-else-if="currentAsset.status === 'IN_USE'" type="warning">使用中</el-tag>
          <el-tag v-else-if="currentAsset.status === 'MAINTENANCE'" type="danger">维修中</el-tag>
          <el-tag v-else-if="currentAsset.status === 'SCRAPPED'" type="info">已报废</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentAsset.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentAsset.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentAsset.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, View, Edit } from '@element-plus/icons-vue'
import { getAssetList, createAsset, updateAsset, deleteAsset, batchDeleteAssets, exportAssets } from '@/api/asset'
import { getCategoryTree } from '@/api/asset'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const categories = ref([])
const selectedIds = ref([])

const searchForm = reactive({
  name: '',
  assetNumber: '',
  categoryId: null,
  status: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const assetFormRef = ref(null)

const assetForm = reactive({
  id: null,
  assetNumber: '',
  name: '',
  categoryId: null,
  specifications: '',
  location: '',
  purchasePrice: 0,
  purchaseDate: '',
  status: 'AVAILABLE',
  remark: ''
})

const currentAsset = ref({})

const rules = {
  assetNumber: [{ required: true, message: '请输入资产编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  specifications: [{ required: true, message: '请输入规格型号', trigger: 'blur' }],
  location: [{ required: true, message: '请输入存放位置', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入购入价格', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择购入日期', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await getAssetList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取资产列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类树
const fetchCategories = async () => {
  try {
    const res = await getCategoryTree()
    categories.value = flattenTree(res.data)
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 扁平化树结构
const flattenTree = (tree, result = []) => {
  tree.forEach(node => {
    result.push({ id: node.id, name: node.name })
    if (node.children && node.children.length > 0) {
      flattenTree(node.children, result)
    }
  })
  return result
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    assetNumber: '',
    categoryId: null,
    status: null
  })
  handleSearch()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增资产'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑资产'
  Object.assign(assetForm, row)
  dialogVisible.value = true
}

// 查看
const handleView = (row) => {
  currentAsset.value = row
  detailDialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该资产吗？', '提示', {
      type: 'warning'
    })
    await deleteAsset(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的${selectedIds.value.length}条资产吗？`, '提示', {
      type: 'warning'
    })
    await batchDeleteAssets(selectedIds.value)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 导出
const handleExport = async () => {
  try {
    const res = await exportAssets(searchForm)
    const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `资产列表_${new Date().getTime()}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 提交表单
const handleSubmit = async () => {
  if (!assetFormRef.value) return
  
  await assetFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateAsset(assetForm.id, assetForm)
          ElMessage.success('修改成功')
        } else {
          await createAsset(assetForm)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '修改失败' : '新增失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(assetForm, {
    id: null,
    assetNumber: '',
    name: '',
    categoryId: null,
    specifications: '',
    location: '',
    purchasePrice: 0,
    purchaseDate: '',
    status: 'AVAILABLE',
    remark: ''
  })
  assetFormRef.value?.clearValidate()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  fetchData()
  fetchCategories()
})
</script>

<style scoped>
.asset-list-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
