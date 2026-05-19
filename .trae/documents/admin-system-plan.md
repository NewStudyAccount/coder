# 后台管理系统创建计划（SpringBoot + Vue3 + TypeScript）

## 项目概述

在 `d:\yudao\myproject\coder` 目录下创建一个名为 `admin-system` 的前后端分离后台管理系统，参考现有 `asset-management` 项目的架构风格，但升级为 **Vue3 + TypeScript** 技术栈。

## 技术栈

### 后端
- Spring Boot 2.7.x
- MyBatis + MySQL
- JWT 认证
- Lombok + Hutool
- BCrypt 密码加密

### 前端
- Vue 3 + TypeScript
- Vite 5
- Element Plus
- Vue Router 4
- Pinia 状态管理
- Axios 请求封装

## 项目结构

```
admin-system/
├── admin-backend/                    # 后端项目
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/admin/
│       │   ├── AdminApplication.java
│       │   ├── common/
│       │   │   ├── Result.java        # 统一响应封装
│       │   │   └── PageResult.java    # 分页响应封装
│       │   ├── config/
│       │   │   ├── CorsConfig.java    # 跨域配置
│       │   │   └── WebMvcConfig.java  # Web配置
│       │   ├── controller/
│       │   │   ├── AuthController.java
│       │   │   ├── UserController.java
│       │   │   ├── RoleController.java
│       │   │   ├── MenuController.java
│       │   │   └── DeptController.java
│       │   ├── entity/
│       │   │   ├── User.java
│       │   │   ├── Role.java
│       │   │   ├── Menu.java
│       │   │   └── Dept.java
│       │   ├── mapper/
│       │   │   ├── UserMapper.java
│       │   │   ├── RoleMapper.java
│       │   │   ├── MenuMapper.java
│       │   │   └── DeptMapper.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   ├── UserService.java
│       │   │   ├── RoleService.java
│       │   │   ├── MenuService.java
│       │   │   └── DeptService.java
│       │   ├── interceptor/
│       │   │   └── JwtAuthInterceptor.java
│       │   └── util/
│       │       └── JwtUtil.java
│       └── resources/
│           ├── application.yml
│           └── mapper/
│               ├── UserMapper.xml
│               ├── RoleMapper.xml
│               ├── MenuMapper.xml
│               └── DeptMapper.xml
├── admin-frontend/                   # 前端项目
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   ├── tsconfig.node.json
│   ├── vite.config.ts
│   ├── env.d.ts
│   └── src/
│       ├── main.ts
│       ├── App.vue
│       ├── api/
│       │   ├── auth.ts
│       │   ├── user.ts
│       │   ├── role.ts
│       │   ├── menu.ts
│       │   └── dept.ts
│       ├── assets/
│       │   └── main.css
│       ├── layout/
│       │   └── MainLayout.vue
│       ├── router/
│       │   └── index.ts
│       ├── stores/
│       │   ├── user.ts
│       │   └── permission.ts
│       ├── types/
│       │   ├── api.d.ts
│       │   ├── user.d.ts
│       │   ├── role.d.ts
│       │   ├── menu.d.ts
│       │   └── dept.d.ts
│       ├── utils/
│       │   └── request.ts
│       └── views/
│           ├── login/
│           │   └── Login.vue
│           ├── dashboard/
│           │   └── Dashboard.vue
│           ├── system/
│           │   ├── user/
│           │   │   └── UserList.vue
│           │   ├── role/
│           │   │   └── RoleList.vue
│           │   ├── menu/
│           │   │   └── MenuList.vue
│           │   └── dept/
│           │       └── DeptList.vue
│           └── profile/
│               └── Profile.vue
└── database/
    └── init.sql
```

## 实施步骤

### 第一阶段：数据库设计

1. **创建 `database/init.sql`** — 包含以下表结构：
   - `sys_user` — 用户表（id, username, password, nickname, email, phone, avatar, dept_id, status, create_time, update_time）
   - `sys_role` — 角色表（id, role_name, role_key, sort, status, remark, create_time, update_time）
   - `sys_menu` — 菜单表（id, menu_name, parent_id, path, component, icon, sort, menu_type, status, create_time, update_time）
   - `sys_user_role` — 用户角色关联表（id, user_id, role_id）
   - `sys_role_menu` — 角色菜单关联表（id, role_id, menu_id）
   - `sys_dept` — 部门表（id, dept_name, parent_id, sort, leader, status, create_time, update_time）
   - 初始化数据（admin 用户、基础角色、菜单等）

### 第二阶段：后端开发

2. **创建 `admin-backend/pom.xml`** — Maven 配置，参考 asset-management 的 pom 风格，引入 Spring Boot 2.7、MyBatis、MySQL、JWT、Lombok、Hutool、Validation、BCrypt

3. **创建 `application.yml`** — 服务端口 8080、context-path /api、数据源配置、MyBatis 配置、JWT 配置

4. **创建 `AdminApplication.java`** — Spring Boot 启动类，@MapperScan

5. **创建通用模块**
   - `Result.java` — 统一响应封装（code, message, data）
   - `PageResult.java` — 分页响应封装（list, total）
   - `JwtUtil.java` — JWT 工具类（生成/验证/解析 Token）

6. **创建配置类**
   - `CorsConfig.java` — 跨域配置
   - `WebMvcConfig.java` — 注册 JWT 拦截器，排除登录/注册路径

7. **创建 JWT 拦截器** — `JwtAuthInterceptor.java`，验证 Token，将用户信息放入请求属性

8. **创建实体类** — User, Role, Menu, Dept（使用 Lombok @Data）

9. **创建 Mapper 层** — UserMapper, RoleMapper, MenuMapper, DeptMapper（接口 + XML）

10. **创建 Service 层** — AuthService, UserService, RoleService, MenuService, DeptService

11. **创建 Controller 层**
    - `AuthController` — 登录、注册、获取用户信息、退出
    - `UserController` — 用户 CRUD、分配角色
    - `RoleController` — 角色 CRUD、分配菜单
    - `MenuController` — 菜单树形列表、菜单 CRUD
    - `DeptController` — 部门树形列表、部门 CRUD

### 第三阶段：前端开发

12. **创建前端项目基础文件**
    - `package.json` — 依赖：vue3, vue-router, pinia, axios, element-plus, typescript
    - `vite.config.ts` — 配置别名 @、代理 /api
    - `tsconfig.json` / `tsconfig.node.json` — TypeScript 配置
    - `env.d.ts` — Vue 组件类型声明
    - `index.html` — 入口 HTML

13. **创建类型定义** — `types/` 目录下定义 User, Role, Menu, Dept, Api 类型

14. **创建工具模块**
    - `utils/request.ts` — Axios 封装，请求/响应拦截器，Token 注入，401 跳转

15. **创建状态管理**
    - `stores/user.ts` — 用户 Token、用户信息、登录/登出
    - `stores/permission.ts` — 动态菜单/路由权限

16. **创建路由** — `router/index.ts`，登录/注册页 + 嵌套布局路由 + 路由守卫

17. **创建布局** — `layout/MainLayout.vue`，左侧菜单 + 顶部导航 + 内容区

18. **创建 API 模块** — auth.ts, user.ts, role.ts, menu.ts, dept.ts

19. **创建页面视图**
    - `views/login/Login.vue` — 登录页
    - `views/dashboard/Dashboard.vue` — 仪表盘首页
    - `views/system/user/UserList.vue` — 用户管理（表格 + 搜索 + 新增/编辑弹窗）
    - `views/system/role/RoleList.vue` — 角色管理
    - `views/system/menu/MenuList.vue` — 菜单管理（树形表格）
    - `views/system/dept/DeptList.vue` — 部门管理（树形表格）
    - `views/profile/Profile.vue` — 个人中心

20. **创建入口文件** — `main.ts`（注册 ElementPlus、Pinia、Router）、`App.vue`

21. **创建全局样式** — `assets/main.css`

### 第四阶段：验证

22. **后端编译验证** — 在 admin-backend 目录执行 `mvn compile` 确保无编译错误

23. **前端安装依赖 & 编译验证** — 在 admin-frontend 目录执行 `npm install && npm run build` 确保无 TypeScript 和构建错误

## 关键设计决策

- **前端使用 TypeScript**：所有 .vue 文件使用 `<script setup lang="ts">`，API 和 Store 均有类型定义
- **参考 asset-management 的架构风格**：统一 Result 响应、JWT 认证、MyBatis + XML、Element Plus UI
- **RBAC 权限模型**：用户-角色-菜单三级关联，支持动态菜单渲染
- **前后端分离**：后端 8080 端口，前端 5173 端口，通过 Vite proxy 转发 /api
