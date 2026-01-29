# 资产管理系统

## 项目简介
一套完整的资产管理系统，支持资产的全生命周期管理。

## 技术栈
- 前端：Vue3 + Element Plus + Vite
- 后端：Spring Boot 2.7 + MyBatis + MySQL
- 认证：JWT

## 功能模块
1. 资产管理：资产的增删改查、状态管理
2. 分类管理：资产分类维护
3. 借用管理：资产借用/归还记录
4. 用户管理：用户信息和权限管理
5. 统计报表：资产统计和报表展示

## 快速开始

### 后端启动
```bash
cd asset-management-backend
mvn clean install
mvn spring-boot:run
```

### 前端启动
```bash
cd asset-management-frontend
npm install
npm run dev
```

### 数据库初始化
执行 `database/init.sql` 文件初始化数据库

## 默认账号
- 管理员：admin / admin123
- 普通用户：user / user123
