<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
            <el-icon class="stat-icon" style="color: #409EFF"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">角色总数</div>
              <div class="stat-value">{{ stats.roleCount }}</div>
            </div>
            <el-icon class="stat-icon" style="color: #67C23A"><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">菜单总数</div>
              <div class="stat-value">{{ stats.menuCount }}</div>
            </div>
            <el-icon class="stat-icon" style="color: #E6A23C"><Menu /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">部门总数</div>
              <div class="stat-value">{{ stats.deptCount }}</div>
            </div>
            <el-icon class="stat-icon" style="color: #F56C6C"><OfficeBuilding /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="系统名称">后台管理系统</el-descriptions-item>
            <el-descriptions-item label="系统版本">1.0.0</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + TypeScript</el-descriptions-item>
            <el-descriptions-item label="UI 框架">Element Plus</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 2.7</el-descriptions-item>
            <el-descriptions-item label="持久层">MyBatis</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
            <el-descriptions-item label="认证方式">JWT</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-links">
            <el-button type="primary" @click="$router.push('/system/user')">
              <el-icon><User /></el-icon>用户管理
            </el-button>
            <el-button type="success" @click="$router.push('/system/role')">
              <el-icon><UserFilled /></el-icon>角色管理
            </el-button>
            <el-button type="warning" @click="$router.push('/system/menu')">
              <el-icon><Menu /></el-icon>菜单管理
            </el-button>
            <el-button type="danger" @click="$router.push('/system/dept')">
              <el-icon><OfficeBuilding /></el-icon>部门管理
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const stats = reactive({
  userCount: 0,
  roleCount: 0,
  menuCount: 0,
  deptCount: 0
})
</script>

<style scoped>
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.8;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-links .el-button {
  width: 100%;
  justify-content: flex-start;
}
</style>
