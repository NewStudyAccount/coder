<template>
  <div class="contract-alignment-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">合约对齐管理</span>
          <span class="subtitle">对齐非主资费的合约与主资费的UAT</span>
        </div>
      </template>

      <!-- 单个对齐表单 -->
      <el-card class="section-card" shadow="never">
        <template #header>
          <span class="section-title">单个订单对齐</span>
        </template>
        

          <el-form-item label="订单行ID" prop="orderLineId">
            <el-input 
              v-model="singleForm.orderLineId" 
              placeholder="请输入订单行ID"
              clearable
            />
          </el-form-item>

          <el-form-item label="服务请求日期" prop="srd">
            <el-date-picker
              v-model="singleForm.srd"
              type="datetime"
              placeholder="选择服务请求日期"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSingleAlign" :loading="singleLoading">
              <el-icon><Check /></el-icon>
              执行对齐
            </el-button>
            <el-button @click="resetSingleForm">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 批量对齐表单 -->
      <el-card class="section-card" shadow="never">
        <template #header>
          <div class="batch-header">
            <span class="section-title">批量订单对齐</span>
            <el-button size="small" type="success" @click="addBatchItem">
              <el-icon><Plus /></el-icon>
              添加项目
            </el-button>
          </div>
        </template>

        <el-table :data="batchForm.items" border style="width: 100%" max-height="400">
          <el-table-column type="index" label="序号" width="60" align="center" />
          
          <el-table-column label="订单ID" width="200">
            <template #default="{ row }">
              <el-input v-model="row.orderId" placeholder="请输入订单ID" size="small" />
            </template>
          </el-table-column>

          <el-table-column label="订单行ID" width="200">
            <template #default="{ row }">
              <el-input v-model="row.orderLineId" placeholder="请输入订单行ID" size="small" />
            </template>
          </el-table-column>

          <el-table-column label="服务请求日期" width="220">
            <template #default="{ row }">
              <el-date-picker
                v-model="row.srd"
                type="datetime"
                placeholder="选择日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                size="small"
                style="width: 100%"
              />
            </template>
          </el-table-column>

          <el-table-column label="状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'success'" type="success">成功</el-tag>
              <el-tag v-else-if="row.status === 'error'" type="danger">失败</el-tag>
              <el-tag v-else type="info">待执行</el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center" fixed="right">
            <template #default="{ $index }">
              <el-button 
                type="danger" 
                size="small" 
                link
                @click="removeBatchItem($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 20px">
          <el-button type="primary" @click="handleBatchAlign" :loading="batchLoading" :disabled="batchForm.items.length === 0">
            <el-icon><Checked /></el-icon>
            批量执行对齐
          </el-button>
          <el-button @click="clearBatchForm">
            <el-icon><Delete /></el-icon>
            清空列表
          </el-button>
        </div>
      </el-card>

      <!-- 执行结果 -->
      <el-card class="section-card" shadow="never" v-if="resultVisible">
        <template #header>
          <div class="result-header">
            <span class="section-title">执行结果</span>
            <el-button size="small" link @click="resultVisible = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </template>

        <el-alert 
          :title="resultData.message" 
          :type="resultData.success ? 'success' : 'error'"
          :closable="false"
          show-icon
          style="margin-bottom: 15px"
        />

        <div v-if="resultData.details && resultData.details.length > 0">
          <el-divider content-position="left">详细结果</el-divider>
          <el-table :data="resultData.details" border size="small">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="orderId" label="订单ID" width="180" />
            <el-table-column prop="orderLineId" label="订单行ID" width="180" />
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.success ? 'success' : 'danger'" size="small">
                  {{ row.success ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="消息" />
          </el-table>
        </div>

        <div v-if="resultData.data" style="margin-top: 15px">
          <el-descriptions border :column="2" size="small">
            <el-descriptions-item label="订单ID" v-if="resultData.data.orderId">
              {{ resultData.data.orderId }}
            </el-descriptions-item>
            <el-descriptions-item label="订单行ID" v-if="resultData.data.orderLineId">
              {{ resultData.data.orderLineId }}
            </el-descriptions-item>
            <el-descriptions-item label="服务请求日期" v-if="resultData.data.srd">
              {{ resultData.data.srd }}
            </el-descriptions-item>
            <el-descriptions-item label="总数" v-if="resultData.data.total">
              {{ resultData.data.total }}
            </el-descriptions-item>
            <el-descriptions-item label="成功数" v-if="resultData.data.successCount !== undefined">
              <el-tag type="success" size="small">{{ resultData.data.successCount }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="失败数" v-if="resultData.data.failureCount !== undefined">
              <el-tag type="danger" size="small">{{ resultData.data.failureCount }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Refresh, Plus, Checked, Delete, Close } from '@element-plus/icons-vue'
import axios from 'axios'

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api/contract-alignment'

// 单个对齐表单
const singleFormRef = ref(null)
const singleForm = reactive({
  orderId: '',
  orderLineId: '',
  srd: ''
})

const singleRules = {
  orderId: [
    { required: true, message: '请输入订单ID', trigger: 'blur' }
  ],
  orderLineId: [
    { required: true, message: '请输入订单行ID', trigger: 'blur' }
  ],
  srd: [
    { required: true, message: '请选择服务请求日期', trigger: 'change' }
  ]
}

const singleLoading = ref(false)

// 批量对齐表单
const batchForm = reactive({
  items: []
})

const batchLoading = ref(false)

// 结果显示
const resultVisible = ref(false)
const resultData = reactive({
  success: false,
  message: '',
  data: null,
  details: []
})

// 单个对齐
const handleSingleAlign = async () => {
  if (!singleFormRef.value) return
  
  await singleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        singleLoading.value = true
        
        const response = await axios.post(`${API_BASE_URL}/align`, {
          orderId: singleForm.orderId,
          orderLineId: singleForm.orderLineId,
          srd: singleForm.srd
        })
        
        if (response.data.success) {
          ElMessage.success(response.data.message)
          
          // 显示结果
          resultData.success = true
          resultData.message = response.data.message
          resultData.data = response.data.data
          resultData.details = []
          resultVisible.value = true
          
          // 重置表单
          resetSingleForm()
        } else {
          ElMessage.error(response.data.message)
        }
      } catch (error) {
        console.error('执行对齐失败:', error)
        ElMessage.error(error.response?.data?.message || '执行对齐失败，请检查网络连接')
      } finally {
        singleLoading.value = false
      }
    }
  })
}

// 重置单个表单
const resetSingleForm = () => {
  if (singleFormRef.value) {
    singleFormRef.value.resetFields()
  }
}

// 添加批量项目
const addBatchItem = () => {
  batchForm.items.push({
    orderId: '',
    orderLineId: '',
    srd: '',
    status: 'pending'
  })
}

// 删除批量项目
const removeBatchItem = (index) => {
  batchForm.items.splice(index, 1)
}

// 清空批量列表
const clearBatchForm = () => {
  ElMessageBox.confirm(
    '确定要清空所有批量项目吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    batchForm.items = []
    ElMessage.success('已清空')
  }).catch(() => {})
}

// 批量对齐
const handleBatchAlign = async () => {
  // 验证所有项目是否填写完整
  const invalidItems = batchForm.items.filter(
    item => !item.orderId || !item.orderLineId || !item.srd
  )
  
  if (invalidItems.length > 0) {
    ElMessage.warning('请填写所有项目的必填字段')
    return
  }
  
  try {
    batchLoading.value = true
    
    const response = await axios.post(`${API_BASE_URL}/align-batch`, {
      items: batchForm.items.map(item => ({
        orderId: item.orderId,
        orderLineId: item.orderLineId,
        srd: item.srd
      }))
    })
    
    if (response.data.success) {
      // 更新每个项目的状态
      const details = response.data.data.details
      batchForm.items.forEach((item, index) => {
        if (details[index]) {
          item.status = details[index].success ? 'success' : 'error'
        }
      })
      
      ElMessage.success(response.data.message)
      
      // 显示结果
      resultData.success = true
      resultData.message = response.data.message
      resultData.data = {
        total: response.data.data.total,
        successCount: response.data.data.successCount,
        failureCount: response.data.data.failureCount
      }
      resultData.details = details
      resultVisible.value = true
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('批量执行失败:', error)
    ElMessage.error(error.response?.data?.message || '批量执行失败，请检查网络连接')
  } finally {
    batchLoading.value = false
  }
}
</script>

<style scoped>
.contract-alignment-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.box-card {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.subtitle {
  font-size: 13px;
  color: #909399;
}

.section-card {
  margin-bottom: 20px;
}

.section-card:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.batch-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-card__header) {
  background-color: #f5f7fa;
  padding: 15px 20px;
}

:deep(.el-divider__text) {
  font-weight: 600;
  color: #606266;
}
</style>
