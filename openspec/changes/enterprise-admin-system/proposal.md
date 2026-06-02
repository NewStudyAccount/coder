## Why

现有 `admin-system` 项目基于 Vue3 + Element Plus，需要重新构建为 Flutter Web + Spring Boot 3.x 架构的后台管理系统。Flutter 方案可同时覆盖 Web/Android/iOS 三端，且需要实现完整企业级功能（数据权限、代码生成、操作日志、定时任务等），而非仅基础 CRUD。

## What Changes

- 覆盖现有 `admin-system/` 目录，使用 Spring Boot 3.x + MyBatis Plus + Spring Security 6 + Redis 重建后端
- 使用 Flutter Web 重建前端，采用 Feature-first + Clean Architecture 架构
- 实现企业级 RBAC 权限体系：用户-角色-菜单三级关联 + 数据权限（部门隔离）+ 按钮级权限
- 实现登录登出、导航栏、侧边菜单栏、标签页导航、面包屑
- 实现系统基础模块：用户管理、角色管理、菜单管理、部门管理、岗位管理、字典管理、参数配置
- 实现监控日志模块：操作日志、登录日志、在线用户管理、服务器监控
- 实现开发工具模块：代码生成器、消息通知、定时任务
- 数据库从 MySQL 8.0 重建，表结构对齐企业级需求
- 前端实现动态菜单渲染（根据用户角色权限生成路由和菜单）
- 前端实现按钮级权限控制指令/组件

## Capabilities

### New Capabilities

- `auth-system`: 认证授权体系，包含登录/登出、JWT双Token机制、Token刷新、验证码、在线用户管理、强制下线
- `system-user`: 用户管理，包含用户CRUD、分配角色、重置密码、导入导出、数据权限
- `system-role`: 角色管理，包含角色CRUD、分配菜单权限、分配数据权限（部门范围）
- `system-menu`: 菜单管理，包含树形CRUD、菜单类型（目录/菜单/按钮）、权限标识、动态路由生成
- `system-dept`: 部门管理，包含树形CRUD、部门负责人、数据权限隔离
- `system-post`: 岗位管理，包含岗位CRUD、用户岗位关联
- `system-dict`: 字典管理，包含字典类型CRUD、字典数据CRUD、Redis缓存同步
- `system-config`: 参数配置，包含系统参数CRUD、Redis缓存、参数Key常量
- `monitor-operlog`: 操作日志，包含AOP自动记录、日志查询、日志详情
- `monitor-loginlog`: 登录日志，包含登录成功/失败记录、日志查询
- `monitor-online`: 在线用户，包含在线用户列表、强制下线
- `monitor-server`: 服务器监控，包含CPU/内存/JVM/磁盘实时信息
- `dev-gen`: 代码生成器，包含选表导入、代码预览、批量生成、自定义模板
- `dev-notice`: 消息通知，包含站内信CRUD、已读/未读、广播通知
- `dev-job`: 定时任务，包含任务CRUD、执行/暂停、Cron表达式、执行日志
- `flutter-admin-layout`: Flutter Web 后台布局，包含侧边菜单、顶部导航栏、标签页、面包屑、响应式适配
- `flutter-permission`: Flutter 前端权限控制，包含动态路由生成、按钮级权限组件/指令、菜单权限过滤
- `flutter-data-table`: Flutter 企业级数据表格组件，包含分页、排序、筛选、行选择、工具栏

### Modified Capabilities

（无已有能力需要修改，全部为新建）

## Impact

- **覆盖重建**: `admin-system/` 目录将完全重建，现有代码被替换
- **后端技术栈**: Spring Boot 3.2.x + JDK 17 + MyBatis Plus 3.5.x + Spring Security 6 + Redis
- **前端技术栈**: Flutter 3.x + Riverpod + GoRouter + Dio + Hive
- **数据库**: MySQL 8.0，新增 sys_post、sys_dict_type、sys_dict_data、sys_config、sys_oper_log、sys_login_log、sys_notice、sys_job、sys_job_log、gen_table、gen_table_column 等表
- **依赖引入**: 后端新增 spring-boot-starter-security、spring-boot-starter-data-redis、mybatis-plus-spring-boot3-starter、knife4j 等；前端新增 flutter_riverpod、go_router、dio、hive 等
- **开发工具**: 需要 JDK 17+、Flutter SDK 3.x、Redis 服务
