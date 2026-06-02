## Context

现有 `admin-system/` 基于 Spring Boot 2.7 + Vue3 + Element Plus，功能覆盖基础 CRUD 但缺少企业级能力（数据权限、代码生成、操作日志等）。需要完全重建为 Spring Boot 3.x + Flutter Web 架构，实现完整企业级后台管理系统。

后端 API 统一使用 JWT Bearer Token 认证，响应格式为 `{ code, message, data }`。数据库为 MySQL 8.0，缓存使用 Redis。

## Goals / Non-Goals

**Goals:**

- 重建后端为 Spring Boot 3.2 + Spring Security 6 + MyBatis Plus + Redis 架构
- 重建前端为 Flutter Web，采用 Feature-first + Clean Architecture
- 实现完整 RBAC 权限：用户-角色-菜单 + 数据权限（部门隔离）+ 按钮级权限
- 实现动态菜单：根据用户角色权限动态生成路由和侧边菜单
- 实现企业级模块：字典管理、参数配置、操作日志、登录日志、代码生成、定时任务、消息通知
- 前端实现后台管理布局：侧边菜单 + 顶部导航 + 标签页 + 面包屑
- 封装 Flutter 企业级数据表格组件（分页、排序、筛选、工具栏）

**Non-Goals:**

- 不做移动端适配（聚焦 Web 端后台管理）
- 不做 OAuth2 第三方登录，后续按需添加
- 不做 CI/CD 流水线
- 不做微服务架构（单体应用足够）
- 不做国际化（仅中文）

## Decisions

### D1: 后端架构 - Spring Boot 3.2 + 分层架构

```
admin-backend/
├── src/main/java/com/admin/
│   ├── AdminApplication.java
│   ├── common/                     # 通用模块
│   │   ├── result/                 # Result, PageResult, PageParam
│   │   ├── exception/              # GlobalExceptionHandler, BusinessException
│   │   ├── constant/               # 常量
│   │   └── util/                   # SecurityUtils, ServletUtils
│   ├── config/                     # 配置类
│   │   ├── SecurityConfig.java     # Spring Security 配置
│   │   ├── MybatisPlusConfig.java  # MyBatis Plus 配置
│   │   ├── RedisConfig.java        # Redis 配置
│   │   ├── CorsConfig.java         # 跨域配置
│   │   └── Knife4jConfig.java      # API 文档配置
│   ├── security/                   # 安全模块
│   │   ├── JwtTokenProvider.java   # JWT 生成/验证
│   │   ├── JwtAuthFilter.java      # JWT 认证过滤器
│   │   ├── UserDetailsServiceImpl.java
│   │   └── LoginUser.java          # 认证用户主体
│   ├── framework/                  # 框架层
│   │   ├── datapermission/         # 数据权限 AOP
│   │   ├── operlog/                # 操作日志 AOP
│   │   └── codegen/                # 代码生成引擎
│   ├── controller/                 # 控制器层
│   ├── service/                    # 服务层
│   │   └── impl/                   # 服务实现
│   ├── mapper/                     # MyBatis Plus Mapper
│   └── entity/                     # 实体类
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    └── mapper/                     # XML 映射（复杂查询）
```

**选择理由**: Spring Security 6 替代手写 JWT 拦截器，提供完整安全链路。MyBatis Plus 减少样板代码。AOP 实现操作日志和数据权限无侵入。

**替代方案**: Sa-Token — 更轻量但生态不如 Spring Security 成熟。

### D2: 认证方案 - JWT 双 Token + Redis 会话

- 登录成功颁发 accessToken（短有效期 2h）+ refreshToken（长有效期 7d）
- Token 信息存 Redis（key: `login:token:{uuid}`），支持主动失效和在线用户管理
- accessToken 过期后用 refreshToken 换取新 Token
- 登出时删除 Redis 中的 Token 记录
- 强制下线：删除指定用户的 Redis Token

**选择理由**: Redis 存储 Token 实现服务端会话管理，支持强制下线和在线用户查询。双 Token 机制平衡安全性和用户体验。

**替代方案**: 纯 JWT 无状态 — 无法主动失效 Token，不支持强制下线。

### D3: 数据权限 - AOP + MyBatis Plus 拦截器

```
@DataScope(deptAlias = "d")  // 注解在 Mapper 方法上
List<User> selectUserList(PageParam page);
```

- 自定义 `@DataScope` 注解标注需要数据权限的查询方法
- MyBatis Plus 拦截器在 SQL 执行前拼接部门过滤条件
- 角色配置数据范围：全部数据、本部门数据、本部门及以下、仅本人
- SQL 拼接：根据角色数据范围动态追加 `WHERE dept_id IN (...)`

**选择理由**: AOP + 拦截器方式对业务代码零侵入，只需加注解即可。

### D4: 操作日志 - AOP 注解

```
@OperLog(title = "用户管理", businessType = BusinessType.INSERT)
@PostMapping
public Result<Void> addUser(@RequestBody User user) { ... }
```

- 自定义 `@OperLog` 注解标注需要记录的操作
- AOP 切面自动记录：操作人、操作模块、操作类型、请求参数、返回结果、IP、耗时
- 异步写入数据库（不阻塞业务请求）
- 登录日志通过 Spring Security 事件监听自动记录

### D5: 代码生成 - 模板引擎

- 读取数据库表结构（information_schema.COLUMNS）
- 使用 Velocity 模板引擎生成：Entity、Mapper、Service、Controller、前端页面
- 支持自定义模板和生成策略
- 前端提供表导入、代码预览、批量生成功能

### D6: Flutter Web 前端架构

```
admin-frontend/
├── lib/
│   ├── core/                          # 核心基础设施
│   │   ├── network/                   # Dio 封装、拦截器
│   │   ├── router/                    # GoRouter 配置
│   │   ├── storage/                   # Hive 存储
│   │   ├── theme/                     # Material 3 主题
│   │   ├── constants/                 # 常量
│   │   ├── extensions/                # Dart 扩展
│   │   └── utils/                     # 工具类
│   ├── shared/                        # 共享组件
│   │   ├── widgets/                   # 通用组件
│   │   │   ├── admin_data_table.dart  # 企业级数据表格
│   │   │   ├── admin_tree.dart        # 树形组件
│   │   │   ├── permission_widget.dart # 权限控制组件
│   │   │   └── ...
│   │   └── layouts/                   # 布局组件
│   │       └── admin_layout.dart      # 后台管理布局
│   ├── features/                      # 功能模块
│   │   ├── auth/                      # 认证
│   │   ├── dashboard/                 # 首页
│   │   ├── system/                    # 系统管理
│   │   │   ├── user/
│   │   │   ├── role/
│   │   │   ├── menu/
│   │   │   ├── dept/
│   │   │   ├── post/
│   │   │   ├── dict/
│   │   │   └── config/
│   │   ├── monitor/                   # 监控
│   │   │   ├── operlog/
│   │   │   ├── loginlog/
│   │   │   ├── online/
│   │   │   └── server/
│   │   └── devtools/                  # 开发工具
│   │       ├── gen/
│   │       ├── notice/
│   │       └── job/
│   └── main.dart
```

**选择理由**: Feature-first 让功能模块高内聚。后台管理系统功能模块多，按功能组织比按层组织更清晰。

### D7: Flutter 后台管理布局

```
┌──────────────────────────────────────────────────────────────┐
│  Logo    面包屑 > 当前页              🔔  👤 Admin ▾ 登出    │  ← 顶部导航栏
├──────────┬───────────────────────────────────────────────────┤
│          │  [首页] [用户管理] [角色管理] ×                     │  ← 标签页
│  系统管理 │───────────────────────────────────────────────────│
│   ├ 用户 │                                                   │
│   ├ 角色 │                                                   │
│   ├ 菜单 │              内容区域                              │
│   ├ 部门 │                                                   │
│   ├ 岗位 │                                                   │
│   ├ 字典 │                                                   │
│   └ 参数 │                                                   │
│  系统监控 │                                                   │
│   ├ 操作 │                                                   │
│   ├ 登录 │                                                   │
│   ├ 在线 │                                                   │
│   └ 服务器│                                                   │
│  开发工具 │                                                   │
│   ├ 代码 │                                                   │
│   ├ 通知 │                                                   │
│   └ 任务 │                                                   │
├──────────┴───────────────────────────────────────────────────┤
│                                                              │  ← 底部（可选）
└──────────────────────────────────────────────────────────────┘
```

- 侧边菜单：根据用户权限动态渲染，支持折叠/展开
- 顶部导航：面包屑、消息通知、用户信息下拉
- 标签页：多页签切换，支持关闭/关闭其他/关闭全部
- 响应式：窗口缩小时侧边栏自动折叠为图标模式

### D8: Flutter 动态路由与权限

- 登录后获取用户菜单列表，动态生成 GoRouter 路由
- 菜单类型为"目录"生成父路由，"菜单"生成子路由，"按钮"不生成路由
- 按钮级权限：`PermissionWidget` 组件，根据权限标识控制子组件可见性
- 路由守卫：未登录重定向登录页，无权限跳转 403 页

### D9: Flutter 企业级数据表格

- 封装 `AdminDataTable<T>` 组件，支持：
  - 服务端分页（PageParam → PageResult）
  - 列排序（点击表头排序）
  - 列筛选（下拉/输入筛选）
  - 行选择（多选/单选）
  - 工具栏（搜索、新增、修改、删除、导出按钮）
  - 按钮权限控制（工具栏按钮根据权限显示/隐藏）
  - 加载状态、空状态、错误状态

### D10: 数据库设计

核心表结构：

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| sys_user | 用户表 | id, username, password, nickname, dept_id, status |
| sys_role | 角色表 | id, role_name, role_key, data_scope, status |
| sys_menu | 菜单表 | id, menu_name, parent_id, path, component, menu_type, perms |
| sys_dept | 部门表 | id, dept_name, parent_id, leader, status |
| sys_post | 岗位表 | id, post_code, post_name, sort, status |
| sys_user_role | 用户角色关联 | user_id, role_id |
| sys_role_menu | 角色菜单关联 | role_id, menu_id |
| sys_user_post | 用户岗位关联 | user_id, post_id |
| sys_role_dept | 角色部门关联(数据权限) | role_id, dept_id |
| sys_dict_type | 字典类型 | id, dict_type, dict_name, status |
| sys_dict_data | 字典数据 | id, dict_type, dict_label, dict_value, sort |
| sys_config | 参数配置 | id, config_key, config_value, config_type |
| sys_oper_log | 操作日志 | id, title, business_type, oper_name, oper_url, oper_ip, status |
| sys_login_log | 登录日志 | id, username, login_ip, login_location, status, msg |
| sys_notice | 消息通知 | id, notice_title, notice_type, notice_content, status |
| sys_job | 定时任务 | id, job_name, job_group, invoke_target, cron_expression, status |
| sys_job_log | 任务执行日志 | id, job_id, job_name, invoke_target, status |
| gen_table | 代码生成-表 | id, table_name, table_comment, class_name, package_name |
| gen_table_column | 代码生成-列 | id, table_id, column_name, column_type, java_field, java_type |

## Risks / Trade-offs

- **[Flutter Web 表格性能]** 大数据量表格渲染性能不如原生 DOM → 分页加载控制单页数据量，虚拟滚动按需引入
- **[Flutter 后台管理生态]** 缺少成熟的后台管理模板和组件库 → 需要自建 AdminDataTable、AdminTree、AdminLayout 等核心组件，开发量较大
- **[代码生成模板]** Velocity 模板需要覆盖多种场景 → 先实现基础 CRUD 模板，后续逐步完善
- **[Spring Security 6 学习曲线]** 配置方式与 Security 5 差异较大 → 参考官方文档和若依 Spring Boot 3 版本
- **[Redis 依赖]** 引入 Redis 增加部署复杂度 → 提供单机模式（无 Redis）和集群模式两种配置
