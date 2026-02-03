<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff">
              <el-icon :size="30"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAssets }}</div>
              <div class="stat-label">资产总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon :size="30"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.availableAssets }}</div>
              <div class="stat-label">可用资产</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon :size="30"><Edit /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.inUseAssets }}</div>
              <div class="stat-label">使用中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon :size="30"><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.maintenanceAssets }}</div>
              <div class="stat-label">维修中</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <span>资产分类统计</span>
          </template>
          <div ref="categoryChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <span>资产状态分布</span>
          </template>
          <div ref="statusChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 近期借用记录 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>近期借用记录</span>
          </template>
          <el-table :data="recentBorrows" stripe>
            <el-table-column prop="assetName" label="资产名称" />
            <el-table-column prop="borrower" label="借用人" />
            <el-table-column prop="borrowDate" label="借用时间" />
            <el-table-column prop="expectedReturnDate" label="预计归还" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'PENDING'" type="warning">待审批</el-tag>
                <el-tag v-else-if="row.status === 'APPROVED'" type="success">已批准</el-tag>
                <el-tag v-else-if="row.status === 'RETURNED'" type="info">已归还</el-tag>
                <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
                <el-tag v-else-if="row.status === 'OVERDUE'" type="danger">已逾期</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getAssetStatistics } from '@/api/asset'
import { getBorrowList } from '@/api/borrow'
import dayjs from 'dayjs'

const statistics = ref({
  totalAssets: 0,
  availableAssets: 0,
  inUseAssets: 0,
  maintenanceAssets: 0,
  categoryStats: [],
  statusStats: []
})

const recentBorrows = ref([])
const categoryChartRef = ref(null)
const statusChartRef = ref(null)
let categoryChart = null
let statusChart = null

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await getAssetStatistics()
    statistics.value = res.data
    
    // 等待DOM更新后渲染图表
    await nextTick()
    renderCharts()
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取近期借用记录
const fetchRecentBorrows = async () => {
  try {
    const res = await getBorrowList({ page: 1, size: 10 })
    recentBorrows.value = res.data.records.map(item => ({
      ...item,
      borrowDate: dayjs(item.borrowDate).format('YYYY-MM-DD HH:mm'),
      expectedReturnDate: dayjs(item.expectedReturnDate).format('YYYY-MM-DD')
    }))
  } catch (error) {
    ElMessage.error('获取借用记录失败')
  }
}

// 渲染图表
const renderCharts = () => {
  renderCategoryChart()
  renderStatusChart()
}

// 渲染分类统计图表
const renderCategoryChart = () => {
  if (!categoryChartRef.value) return
  
  if (categoryChart) {
    categoryChart.dispose()
  }
  
  categoryChart = echarts.init(categoryChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '资产分类',
        type: 'pie',
        radius: '60%',
        data: statistics.value.categoryStats || [],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  categoryChart.setOption(option)
}

// 渲染状态分布图表
const renderStatusChart = () => {
  if (!statusChartRef.value) return
  
  if (statusChart) {
    statusChart.dispose()
  }
  
  statusChart = echarts.init(statusChartRef.value)
  
  const statusMap = {
    'AVAILABLE': '可用',
    'IN_USE': '使用中',
    'MAINTENANCE': '维修中',
    'SCRAPPED': '已报废'
  }
  
  const statusData = statistics.value.statusStats || []
  const xData = statusData.map(item => statusMap[item.name] || item.name)
  const yData = statusData.map(item => item.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: xData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '数量',
        type: 'bar',
        data: yData,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  statusChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  categoryChart?.resize()
  statusChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  fetchRecentBorrows()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  categoryChart?.dispose()
  statusChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>
