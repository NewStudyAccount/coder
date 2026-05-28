## Why

当前项目已有多个 Vue3 前端 + Spring Boot 后端的全栈子系统，但缺少统一的移动端方案。现有的 `flutter-learn` 仅是学习 Demo，架构简单（命名路由、无 Repository 层、无统一请求封装），无法支撑正式业务开发。需要搭建一个生产级的 Flutter 前端项目框架，为后续对接 admin-backend、asset-management 等后端服务奠定基础。

## What Changes

- 创建全新的 Flutter 项目，采用 Feature-first + Clean Architecture 架构
- 集成 GoRouter 声明式路由，支持路由守卫、嵌套路由、深度链接
- 集成 Riverpod 状态管理，建立统一的 Provider 体系
- 集成 Dio 网络层，封装统一请求/响应拦截、JWT Token 管理、错误处理
- 集成 Hive 本地存储，用于 Token 持久化和配置缓存
- 搭建核心基础设施：主题系统、国际化、通用 UI 组件
- 实现认证模块（登录/注册/Token 刷新）作为首个业务功能
- 建立项目规范：目录结构、命名约定、代码分层规则

## Capabilities

### New Capabilities

- `project-scaffold`: 项目脚手架与目录结构规范，包括 Feature-first 分层、Clean Architecture 三层划分、核心基础设施模块
- `network-layer`: 基于 Dio 的统一网络请求层，包含请求/响应拦截器、JWT Token 自动附加与刷新、统一错误处理、RESTful API 封装
- `routing-system`: 基于 GoRouter 的路由系统，包含声明式路由表、路由守卫（认证拦截）、嵌套路由（ShellRoute）、深度链接支持
- `state-management`: 基于 Riverpod 的状态管理体系，包含 Provider 组织规范、异步状态处理、依赖注入模式
- `local-storage`: 基于 Hive 的本地存储方案，包含 Token 持久化、用户配置缓存、存储迁移策略
- `auth-module`: 认证模块，包含登录/注册页面、JWT Token 管理、自动刷新、登出、路由守卫集成
- `ui-framework`: UI 基础框架，包含 Material 3 主题系统、亮/暗模式切换、通用组件库、响应式布局适配

### Modified Capabilities

（无已有能力需要修改）

## Impact

- **新增项目**: `flutter-app/` 目录，与现有 `flutter-learn/` 并行独立
- **依赖引入**: go_router, flutter_riverpod, dio, hive, hive_flutter, flutter_hooks, hooks_riverpod, freezed, json_serializable, build_runner
- **后端对接**: 后续需配置 CORS 和 API 地址，当前 admin-backend 已开放 CORS（`addAllowedOriginPattern("*")`）
- **开发工具**: 需要配置 build_runner 代码生成（freezed/json_serializable/hive adapter）
