## Context

当前 `coder` 项目包含多个 Vue3 + Spring Boot 全栈子系统（admin-system、asset-management 等），后端 API 统一使用 JWT Bearer Token 认证，响应格式为 `{ code, message, data }`。现有 `flutter-learn/` 是一个教学 Demo 项目，采用简单的命名路由 + Provider + Dio，无 Repository 层、无统一请求封装、无 Token 管理，无法支撑正式业务开发。

需要新建一个生产级 Flutter 项目，采用 Feature-first + Clean Architecture，先搭框架再逐步对接后端。

## Goals / Non-Goals

**Goals:**

- 建立可扩展的 Flutter 项目架构，支持后续对接多个后端服务
- 实现完整的认证流程（登录/注册/Token 管理/路由守卫）
- 封装统一网络请求层，与后端 `{ code, message, data }` 响应格式对齐
- 搭建主题、国际化、通用组件等基础设施
- 确保代码可测试、可维护

**Non-Goals:**

- 不对接具体业务 API（用户管理、商品管理等），仅预留接口
- 不实现 OAuth2 第三方登录，后续按需添加
- 不做桌面端（Windows/macOS）适配，聚焦移动端
- 不做 CI/CD 流水线配置

## Decisions

### D1: Feature-first + Clean Architecture 三层架构

```
lib/
├── core/                          # 核心基础设施（跨功能共享）
│   ├── network/                   # Dio 封装、拦截器、错误处理
│   ├── router/                    # GoRouter 配置、路由守卫
│   ├── storage/                   # Hive 存储封装
│   ├── theme/                     # Material 3 主题定义
│   ├── constants/                 # 常量
│   ├── extensions/                # Dart 扩展方法
│   └── utils/                     # 工具类
├── shared/                        # 共享 UI 组件
│   ├── widgets/                   # 通用组件（Loading、Error、Empty 等）
│   └── layouts/                   # 布局组件（MainLayout 等）
├── features/                      # 功能模块（Feature-first）
│   ├── auth/                      # 认证模块
│   │   ├── data/                  # Data 层
│   │   │   ├── datasources/       # 远程/本地数据源
│   │   │   ├── models/            # DTO（JSON 序列化模型）
│   │   │   └── repositories/      # Repository 实现
│   │   ├── domain/                # Domain 层
│   │   │   ├── entities/          # 业务实体
│   │   │   └── repositories/      # Repository 抽象接口
│   │   └── presentation/          # Presentation 层
│   │       ├── pages/             # 页面
│   │       ├── widgets/           # 模块专用组件
│   │       └── providers/         # Riverpod Providers
│   └── home/                      # 首页模块
│       └── presentation/
└── main.dart                      # 入口
```

**选择理由**: Feature-first 让功能模块高内聚，团队可并行开发不同模块。Clean Architecture 三层分离确保 Domain 层不依赖具体实现，便于替换数据源和测试。

**替代方案**: Layer-first（按 data/domain/presentation 分层）— 功能多了文件散乱，删除/替换功能困难。

### D2: GoRouter 路由

- 声明式路由表，集中管理所有路由
- `redirect` 函数实现认证守卫：未登录重定向到 `/login`
- `ShellRoute` 实现主布局嵌套（底部导航 + 顶部 AppBar）
- 路由路径命名规范：`/auth/login`, `/auth/register`, `/home`, `/profile`

**替代方案**: auto_route — 代码生成增加构建复杂度，当前路由规模不需要。

### D3: Riverpod 状态管理

- 使用 `flutter_riverpod` + `riverpod_annotation`（代码生成）
- Provider 组织：每个 feature 的 `providers/` 目录下定义
- 异步状态：`AsyncNotifierProvider` + `AsyncValue` 处理加载/错误/数据
- 全局状态：AuthState 放在 `core/` 下，跨 feature 共享

**替代方案**: Bloc — 样板代码多（Event/State 类），当前团队规模不需要强约束。

### D4: Dio 网络层封装

```
core/network/
├── dio_client.dart          # Dio 单例，配置 baseURL、超时、拦截器
├── interceptors/
│   ├── auth_interceptor.dart    # 自动附加 Bearer Token
│   └── error_interceptor.dart   # 统一错误处理（401 跳登录）
├── api_result.dart          # 统一响应包装（Success/Failure）
└── api_exception.dart       # 自定义异常类
```

- `baseURL` 从环境配置读取，默认 `http://localhost:8080/api`
- 请求拦截器：从 Hive 读取 Token，附加 `Authorization: Bearer <token>`
- 响应拦截器：解析 `{ code, message, data }`，`code != 200` 抛出 `ApiException`
- 401 响应：清除本地 Token，跳转登录页

**与 admin-frontend request.ts 对齐**: 保持相同的拦截逻辑和错误处理策略。

### D5: Hive 本地存储

- 存储 Token 对象（accessToken、refreshToken、过期时间）
- 存储用户偏好（主题模式、语言）
- 使用 `hive_flutter` + Adapter 代码生成
- Storage Key 常量集中管理

**替代方案**: Isar — 更强大但体积大，当前只需简单缓存。

### D6: 主题系统

- Material 3（`useMaterial3: true`）
- Seed Color 方式定义主题，支持亮/暗模式
- 主题状态通过 Riverpod Provider 管理
- 自定义 ColorScheme 扩展品牌色

### D7: 数据模型策略

- Domain Entity：纯 Dart 类，不含 JSON 注解
- Data Model（DTO）：使用 `freezed` + `json_serializable` 生成不可变类和序列化
- Repository 实现中完成 Model → Entity 转换

## Risks / Trade-offs

- **[代码生成构建时间]** freezed + json_serializable + riverpod_annotation + hive_adapter 均依赖 build_runner，首次生成和增量构建耗时 → 仅在模型变更时运行 `build_runner`，CI 中缓存生成文件
- **[Hive Web 兼容性]** Hive 在 Web 端使用 IndexedDB，性能和容量有限 → 当前聚焦移动端，Web 支持后续考虑
- **[Riverpod 学习曲线]** Riverpod 写法灵活，团队风格可能不统一 → 制定 Provider 编写规范，统一使用代码生成方式
- **[GoRouter 版本稳定性]** GoRouter 迭代较快，API 可能有 breaking change → 锁定主版本，升级前评估
