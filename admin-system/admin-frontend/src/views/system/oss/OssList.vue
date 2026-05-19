<template>
  <div class="oss-list">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="文件名">
          <el-input v-model="queryForm.originalName" placeholder="请输入文件名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="flex-between mb-10">
        <span>文件列表</span>
        <el-upload
          :show-file-list="false"
          :before-upload="handleBeforeUpload"
          :http-request="handleUpload"
          :multiple="true"
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>上传文件
          </el-button>
        </el-upload>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="originalName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="类型" width="150" show-overflow-tooltip />
        <el-table-column prop="uploadUserName" label="上传人" width="120" />
        <el-table-column prop="createTime" label="上传时间" width="170" />
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDownload(row)">下载</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { uploadFile, getFileList, deleteFile } from '@/api/oss'

const loading = ref(false)
const tableData = ref<any[]>([])

const queryForm = reactive({
  originalName: ''
})

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFileList(queryForm)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => {
  queryForm.originalName = ''
  loadData()
}

const handleBeforeUpload = (file: File) => {
  const maxSize = 50 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }
  return true
}

const handleUpload = async (options: UploadRequestOptions) => {
  try {
    await uploadFile(options.file)
    ElMessage.success('上传成功')
    loadData()
  } catch (e) {
    console.error(e)
    ElMessage.error('上传失败')
  }
}

const handleDownload = (row: any) => {
  window.open(row.url, '_blank')
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除文件"${row.originalName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteFile(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.search-form {
  margin-bottom: 10px;
}
</style>
