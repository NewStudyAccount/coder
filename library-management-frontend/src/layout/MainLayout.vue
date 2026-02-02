<template>
  <div class="main-layout">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-content">
        <div class="navbar-logo" @click="goHome">
          <el-icon><Reading /></el-icon>
          <span>图书馆管理系统</span>
        </div>
        
        <div class="navbar-menu">
          <router-link 
            to="/books" 
            class="navbar-item"
            :class="{ active: $route.path === '/books' }"
          >
            <el-icon><Reading /></el-icon>
            图书查询
          </router-link>
          
          <router-link 
            to="/borrow" 
            class="navbar-item"
            :class="{ active: $route.path === '/borrow' }"
          >
            <el-icon><DocumentAdd /></el-icon>
            借阅管理
          </router-link>
          
          <router-link 
            to="/return" 
            class="navbar-item"
            :class="{ active: $route.path === '/return' }"
          >
            <el-icon><DocumentChecked /></el-icon>
            归还管理
          </router-link>
          
          <router-link 
            to="/my-borrows" 
            class="navbar-item"
            :class="{ active: $route.path === '/my-borrows' }"
          >
            <el-icon><Tickets /></el-icon>
            我的借阅
          </router-link>
        </div>
        
        <div class="navbar-user">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><User /></el-icon>
              <span>{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <p>&copy; 2026 图书馆管理系统. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  Reading, 
  DocumentAdd, 
  DocumentChecked, 
  Tickets, 
  User, 
  ArrowDown, 
  SwitchButton 
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const username = computed(() => userStore.username)

const goHome = () => {
  router.push('/books')
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
  }
}
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

.navbar {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  user-select: none;
}

.navbar-logo:hover {
  opacity: 0.8;
}

.navbar-menu {
  display: flex;
  gap: 24px;
  align-items: center;
  flex: 1;
  justify-content: center;
}

.navbar-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
}

.navbar-item:hover {
  color: #409eff;
  background-color: #ecf5ff;
}

.navbar-item.active {
  color: #409eff;
  background-color: #ecf5ff;
  font-weight: 500;
}

.navbar-user {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px 20px;
}

.footer {
  background: white;
  padding: 20px;
  text-align: center;
  color: #909399;
  border-top: 1px solid #e4e7ed;
}

.footer p {
  margin: 0;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-content {
    flex-wrap: wrap;
    height: auto;
    padding: 12px;
  }

  .navbar-logo {
    font-size: 16px;
  }

  .navbar-menu {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 12px;
  }

  .navbar-item {
    font-size: 14px;
    padding: 6px 12px;
  }

  .main-content {
    padding: 16px 12px;
  }
}
</style>
