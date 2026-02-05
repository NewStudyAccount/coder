<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2>欢迎回来，{{ userInfo.realName || userInfo.username }}！</h2>
      <p>{{ currentTime }}</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-1" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStudents }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-2" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCourses }}</div>
              <div class="stat-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-3" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalGrades }}</div>
              <div class="stat-label">成绩记录</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card stat-card-4" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.averageScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>成绩分布</span>
              <el-icon><PieChart /></el-icon>
            </div>
          </template>
          <div class="chart-content">
            <div class="grade-distribution">
              <div v-for="item in gradeDistribution" :key="item.grade" class="grade-item">
                <div class="grade-label">{{ item.grade }}</div>
                <div class="grade-bar">
                  <div 
                    class="grade-progress" 
                    :style="{ width: item.percentage + '%', backgroundColor: item.color }"
                  ></div>
                </div>
                <div class="grade-count">{{ item.count }}人 ({{ item.percentage }}%)</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>热门课程</span>
              <el-icon><Histogram /></el-icon>
            </div>
          </template>
          <div class="chart-content">
            <div class="popular-courses">
              <div v-for="(course, index) in popularCourses" :key="course.id" class="course-item">
                <div class="course-rank">{{ index + 1 }}</div>
                <div class="course-info">
                  <div class="course-name">{{ course.name }}</div>
                  <div class="course-teacher">{{ course.teacher }}</div>
                </div>
                <div class="course-count">{{ course.studentCount }}人</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="quick-actions-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
          <el-icon><Operation /></el-icon>
        </div>
      </template>
      <div class="quick-actions">
        <el-button 
          type="primary" 
          :icon="Plus" 
          @click="navigateTo('/students')"
        >
          添加学生
        </el-button>
        <el-button 
          type="success" 
          :icon="DocumentAdd" 
          @click="navigateTo('/courses')"
        >
          添加课程
        </el-button>
        <el-button 
          type="warning" 
          :icon="EditPen" 
          @click="navigateTo('/grades')"
        >
          录入成绩
        </el-button>
        <el-button 
          type="info" 
          :icon="Download" 
          @click="handleExport"
        >
          导出数据
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  UserFilled, 
  Reading, 
  Document, 
  TrendCharts,
  PieChart,
  Histogram,
  Operation,
  Plus,
  DocumentAdd,
  EditPen,
  Download
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getStudentList } from '@/api/student'
import { getCourseList } from '@/api/course'
import { getGradeList, getGradeStatistics } from '@/api/grade'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const currentTime = ref('')
const stats = reactive({
  totalStudents: 0,
  totalCourses: 0,
  totalGrades: 0,
  averageScore: 0
})

const gradeDistribution = ref([
  { grade: '优秀 (90-100)', count: 0, percentage: 0, color: '#67c23a' },
  { grade: '良好 (80-89)', count: 0, percentage: 0, color: '#409eff' },
  { grade: '中等 (70-79)', count: 0, percentage: 0, color: '#e6a23c' },
  { grade: '及格 (60-69)', count: 0, percentage: 0, color: '#f56c6c' },
  { grade: '不及格 (<60)', count: 0, percentage: 0, color: '#909399' }
])

const popularCourses = ref([])

// 更新当前时间
const updateTime = () => {
  const now = new Date()
  const options = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    weekday: 'long'
  }
  currentTime.value = now.toLocaleDateString('zh-CN', options)
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 获取学生总数
    const studentsRes = await getStudentList({ pageNum: 1, pageSize: 1 })
    stats.totalStudents = studentsRes.total || 0

    // 获取课程总数
    const coursesRes = await getCourseList({ pageNum: 1, pageSize: 1 })
    stats.totalCourses = coursesRes.total || 0

    // 获取成绩总数和统计信息
    const gradesRes = await getGradeList({ pageNum: 1, pageSize: 1 })
    stats.totalGrades = gradesRes.total || 0

    // 获取成绩统计
    const statisticsRes = await getGradeStatistics()
    stats.averageScore = statisticsRes.averageScore?.toFixed(1) || '0.0'
    
    // 更新成绩分布
    if (statisticsRes.distribution) {
      updateGradeDistribution(statisticsRes.distribution)
    }

    // 获取热门课程
    const popularRes = await getCourseList({ 
      pageNum: 1, 
      pageSize: 5,
      sortBy: 'studentCount',
      sortOrder: 'desc'
    })
    popularCourses.value = popularRes.list || []

  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 更新成绩分布
const updateGradeDistribution = (distribution) => {
  const total = distribution.excellent + distribution.good + distribution.medium + 
                distribution.pass + distribution.fail

  if (total === 0) return

  gradeDistribution.value[0].count = distribution.excellent
  gradeDistribution.value[0].percentage = ((distribution.excellent / total) * 100).toFixed(1)
  
  gradeDistribution.value[1].count = distribution.good
  gradeDistribution.value[1].percentage = ((distribution.good / total) * 100).toFixed(1)
  
  gradeDistribution.value[2].count = distribution.medium
  gradeDistribution.value[2].percentage = ((distribution.medium / total) * 100).toFixed(1)
  
  gradeDistribution.value[3].count = distribution.pass
  gradeDistribution.value[3].percentage = ((distribution.pass / total) * 100).toFixed(1)
  
  gradeDistribution.value[4].count = distribution.fail
  gradeDistribution.value[4].percentage = ((distribution.fail / total) * 100).toFixed(1)
}

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

// 导出数据
const handleExport = () => {
  ElMessage.info('数据导出功能开发中...')
}

onMounted(() => {
  updateTime()
  setInterval(updateTime, 60000) // 每分钟更新一次时间
  loadStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-section {
  margin-bottom: 24px;
}

.welcome-section h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.welcome-section p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-card-1 {
  border-left: 4px solid #409eff;
}

.stat-card-2 {
  border-left: 4px solid #67c23a;
}

.stat-card-3 {
  border-left: 4px solid #e6a23c;
}

.stat-card-4 {
  border-left: 4px solid #f56c6c;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card-1 .stat-icon {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.stat-card-2 .stat-icon {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.stat-card-3 .stat-icon {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-card-4 .stat-icon {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-content {
  min-height: 300px;
  padding: 10px 0;
}

.grade-distribution {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.grade-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.grade-label {
  width: 120px;
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}

.grade-bar {
  flex: 1;
  height: 24px;
  background: #f5f7fa;
  border-radius: 12px;
  overflow: hidden;
}

.grade-progress {
  height: 100%;
  border-radius: 12px;
  transition: width 0.3s;
}

.grade-count {
  width: 100px;
  font-size: 13px;
  color: #909399;
  text-align: right;
  flex-shrink: 0;
}

.popular-courses {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.course-item:hover {
  background: #e4e7ed;
  transform: translateX(4px);
}

.course-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.course-item:first-child .course-rank {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.course-item:nth-child(2) .course-rank {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.course-item:nth-child(3) .course-rank {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.course-info {
  flex: 1;
}

.course-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.course-teacher {
  font-size: 12px;
  color: #909399;
}

.course-count {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
  flex-shrink: 0;
}

.quick-actions-card {
  border-radius: 8px;
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 140px;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 12px;
  }

  .welcome-section h2 {
    font-size: 24px;
  }

  .stat-value {
    font-size: 24px;
  }

  .grade-label {
    width: 100px;
    font-size: 12px;
  }

  .grade-count {
    width: 80px;
    font-size: 12px;
  }

  .quick-actions .el-button {
    flex: 1 1 calc(50% - 6px);
    min-width: auto;
  }
}
</style>
