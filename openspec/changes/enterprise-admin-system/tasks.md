## 1. 数据库设计与初始化

- [x] 1.1 创建 `admin-system/database/init.sql`，定义所有系统表（sys_user, sys_role, sys_menu, sys_dept, sys_post, sys_user_role, sys_role_menu, sys_user_post, sys_role_dept, sys_dict_type, sys_dict_data, sys_config, sys_oper_log, sys_login_log, sys_notice, sys_job, sys_job_log）
- [x] 1.2 创建代码生成相关表（gen_table, gen_table_column）
- [x] 1.3 插入初始化数据（admin 用户、超级管理员角色、基础菜单树、基础部门树、基础岗位、系统字典、系统参数）

## 2. 后端项目初始化

- [x] 2.1 清空现有 `admin-system/admin-backend/` 目录，使用 Spring Boot 3.2 重建 pom.xml（引入 spring-boot-starter-security, spring-boot-starter-data-redis, mybatis-plus-spring-boot3-starter, knife4j, hutool, lombok, velocity）
- [x] 2.2 创建 `application.yml` 和 `application-dev.yml`（端口 8080、context-path /api、MySQL 数据源、Redis 连接、JWT 配置、MyBatis Plus 配置）
- [x] 2.3 创建 `AdminApplication.java` 启动类，@MapperScan 扫描 Mapper 包
- [x] 2.4 创建通用模块：Result.java（统一响应）、PageResult.java（分页响应）、PageParam.java（分页请求）、BusinessException.java（业务异常）、GlobalExceptionHandler.java（全局异常处理）
- [x] 2.5 创建工具类：SecurityUtils.java（获取当前登录用户）、ServletUtils.java（请求工具）

## 3. 后端安全模块

- [x] 3.1 创建 `security/JwtTokenProvider.java`，实现 JWT Token 生成、验证、解析
- [x] 3.2 创建 `security/JwtAuthFilter.java`，实现 OncePerRequestFilter，从请求头提取 Token 验证并设置 SecurityContext
- [x] 3.3 创建 `security/LoginUser.java`，实现 UserDetails 接口，包含用户ID、用户名、权限列表
- [x] 3.4 创建 `security/UserDetailsServiceImpl.java`，实现 UserDetailsService，从数据库加载用户和权限
- [x] 3.5 创建 `config/SecurityConfig.java`，配置 SecurityFilterChain（JWT Filter、登录接口放行、CORS、异常处理）
- [x] 3.6 创建 `config/RedisConfig.java`，配置 RedisTemplate 序列化方式
- [x] 3.7 创建 `config/CorsConfig.java`，配置跨域允许
- [x] 3.8 创建 `config/MybatisPlusConfig.java`，配置分页插件和数据权限拦截器
- [x] 3.9 创建 `config/Knife4jConfig.java`，配置 API 文档

## 4. 后端认证接口

- [x] 4.1 创建 AuthController（登录、登出、获取用户信息、刷新 Token、获取验证码）
- [x] 4.2 创建 AuthService（登录验证、Token 生成与 Redis 存储、登出清除 Redis、Token 刷新）
- [x] 4.3 创建登录请求/响应 DTO（LoginBody、TokenVO、UserInfoVO）

## 5. 后端系统基础模块 - 实体与 Mapper

- [x] 5.1 创建所有实体类：User, Role, Menu, Dept, Post, UserRole, RoleMenu, UserPost, RoleDept, DictType, DictData, Config, OperLog, LoginLog, Notice, Job, JobLog
- [x] 5.2 创建所有 Mapper 接口（继承 BaseMapper）
- [x] 5.3 创建复杂查询的 xml 映射文件（UserMapper.xml 含关联查询、MenuMapper.xml 含树形查询、DeptMapper.xml 含树形查询）

## 6. 后端系统基础模块 - Service 与 Controller

- [x] 6.1 创建 UserService/UserServiceImpl（用户 CRUD、分配角色、重置密码、导入导出）
- [x] 6.2 创建 UserController（用户列表、新增、修改、删除、分配角色、重置密码、导出）
- [x] 6.3 创建 RoleService/RoleServiceImpl（角色 CRUD、分配菜单、分配数据权限）
- [x] 6.4 创建 RoleController（角色列表、新增、修改、删除、分配菜单、分配数据权限）
- [x] 6.5 创建 MenuService/MenuServiceImpl（菜单 CRUD、菜单树、根据角色查询菜单、根据用户查询菜单）
- [x] 6.6 创建 MenuController（菜单列表、新增、修改、删除、树形查询）
- [x] 6.7 创建 DeptService/DeptServiceImpl（部门 CRUD、部门树、部门列表）
- [x] 6.8 创建 DeptController（部门列表、新增、修改、删除、树形查询）
- [x] 6.9 创建 PostService/PostServiceImpl（岗位 CRUD）
- [x] 6.10 创建 PostController（岗位列表、新增、修改、删除）
- [x] 6.11 创建 DictTypeService/DictTypeServiceImpl（字典类型 CRUD、缓存刷新）
- [x] 6.12 创建 DictDataServiceImpl（字典数据 CRUD、缓存刷新、按类型查询）
- [x] 6.13 创建 DictTypeController、DictDataController
- [x] 6.14 创建 ConfigService/ConfigServiceImpl（参数 CRUD、缓存刷新、按 Key 查询）
- [x] 6.15 创建 ConfigController（参数列表、新增、修改、删除、按 Key 查询）

## 7. 后端框架层 - AOP 与数据权限

- [x] 7.1 创建 `framework/datapermission/DataScope.java` 注解
- [x] 7.2 创建 `framework/datapermission/DataScopeAspect.java` 切面，解析数据权限并拼接到 SQL
- [x] 7.3 创建 `framework/datapermission/DataScopeInterceptor.java` MyBatis Plus 拦截器，拼接部门过滤条件
- [x] 7.4 创建 `framework/operlog/OperLog.java` 注解（title, businessType）
- [x] 7.5 创建 `framework/operlog/OperLogAspect.java` 切面，异步记录操作日志
- [x] 7.6 创建 `framework/operlog/OperLogService.java`，异步保存操作日志到数据库

## 8. 后端监控模块

- [x] 8.1 创建 OperLogController（操作日志分页查询、详情查询）
- [x] 8.2 创建 LoginLogService/LoginLogServiceImpl（登录日志记录、查询）
- [x] 8.3 创建 LoginLogController（登录日志分页查询）
- [x] 8.4 创建在线用户管理：SysUserOnlineService（从 Redis 获取在线用户、强制下线）
- [x] 8.5 创建 SysUserOnlineController（在线用户列表、强制下线）
- [x] 8.6 创建 ServerController（服务器监控信息：CPU、内存、JVM、磁盘）

## 9. 后端开发工具模块

- [x] 9.1 创建代码生成：GenTableService/GenTableServiceImpl（表导入、配置编辑、代码预览、批量生成下载）
- [x] 9.2 创建 GenTableController（表导入、列表、编辑配置、预览、下载）
- [x] 9.3 创建 Velocity 模板文件（Entity.java.vm, Mapper.java.vm, Service.java.vm, ServiceImpl.java.vm, Controller.java.vm, Mapper.xml.vm）
- [x] 9.4 创建 NoticeService/NoticeServiceImpl（通知 CRUD、已读状态）
- [x] 9.5 创建 NoticeController（通知列表、新增、修改、删除、标记已读）
- [x] 9.6 创建 JobService/JobServiceImpl（任务 CRUD、启动/暂停/执行、Cron 校验）
- [x] 9.7 创建 JobController（任务列表、新增、修改、删除、启动、暂停、执行一次）
- [x] 9.8 创建 JobLogService/JobLogServiceImpl（执行日志记录和查询）
- [x] 9.9 创建 JobLogController（执行日志列表）

## 10. Flutter 前端项目初始化

- [x] 10.1 清空现有 `admin-system/admin-frontend/` 目录，使用 `flutter create admin-frontend` 创建 Flutter Web 项目
- [x] 10.2 配置 pubspec.yaml，添加核心依赖：flutter_riverpod, riverpod_annotation, go_router, dio, hive, hive_flutter, freezed, freezed_annotation, json_annotation
- [x] 10.3 配置 pubspec.yaml，添加开发依赖：build_runner, json_serializable, riverpod_generator, flutter_lints
- [x] 10.4 执行 `flutter pub get` 验证依赖无冲突
- [x] 10.5 创建 `lib/` 目录结构：core/、shared/、features/ 及其子目录

## 11. Flutter 核心基础设施

- [x] 11.1 创建 `core/constants/` 目录：api_constants.dart（API 路径和 baseURL）、storage_keys.dart（Hive Key 常量）
- [x] 11.2 创建 `core/network/api_exception.dart`，定义 ApiException 类
- [x] 11.3 创建 `core/network/api_result.dart`，定义 ApiResult<T> 密封类
- [x] 11.4 创建 `core/network/interceptors/auth_interceptor.dart`，请求自动附加 Bearer Token
- [x] 11.5 创建 `core/network/interceptors/error_interceptor.dart`，统一错误处理和 401 跳转
- [x] 11.6 创建 `core/network/dio_client.dart`，Dio 单例配置
- [x] 11.7 创建 `core/storage/storage_service.dart`，Hive 类型安全封装
- [x] 11.8 创建 `core/theme/app_theme.dart`，Material 3 亮色/暗色 ThemeData
- [x] 11.9 创建 `core/theme/theme_provider.dart`，Riverpod 管理主题模式
- [ ] 11.10 运行 `build_runner` 生成序列化代码

## 12. Flutter 后台管理布局

- [x] 12.1 创建 `shared/layouts/admin_layout.dart`，主布局组件（侧边菜单 + 顶部导航 + 内容区）
- [x] 12.2 实现侧边菜单组件：根据菜单数据动态渲染树形菜单，支持折叠/展开
- [x] 12.3 实现顶部导航栏：Logo、面包屑、消息通知图标、用户信息下拉菜单
- [x] 12.4 实现标签页导航：多页签切换、关闭当前/关闭其他/关闭全部
- [x] 12.5 实现面包屑导航：根据当前路由层级生成面包屑
- [x] 12.6 实现响应式布局：窗口宽度 < 768px 侧边栏折叠为图标模式

## 13. Flutter 权限与路由

- [x] 13.1 创建 `core/router/app_router.dart`，GoRouter 基础配置（登录页、404 页、403 页）
- [x] 13.2 实现动态路由生成：登录后获取菜单数据，动态注册 GoRouter 路由
- [x] 13.3 实现路由守卫：未登录重定向登录页，无权限跳转 403 页
- [x] 13.4 创建 `shared/widgets/permission_widget.dart`，按钮级权限控制组件
- [x] 13.5 实现菜单权限过滤：侧边菜单仅显示当前用户有权限的菜单项

## 14. Flutter 通用组件

- [x] 14.1 创建 `shared/widgets/admin_data_table.dart`，企业级数据表格组件（分页、排序、行选择、工具栏、加载/空/错误状态）
- [x] 14.2 创建 `shared/widgets/admin_tree.dart`，树形组件（展开/折叠、选择、搜索）
- [x] 14.3 创建 `shared/widgets/admin_dialog.dart`，通用弹窗组件（表单弹窗、确认弹窗）
- [x] 14.4 创建 `shared/widgets/admin_form.dart`，通用表单组件（文本、下拉、日期、数字等表单项）
- [x] 14.5 创建 `shared/widgets/loading_widget.dart`、`error_widget.dart`、`empty_widget.dart`

## 15. Flutter 认证模块

- [x] 15.1 创建 `features/auth/` 目录结构（data/domain/presentation）
- [x] 15.2 创建 Domain 层：User 实体、AuthRepository 抽象接口
- [x] 15.3 创建 Data 层：LoginBody、TokenVO、UserInfoVO 模型，AuthRemoteDataSource，AuthRepositoryImpl
- [x] 15.4 创建 Presentation 层：AuthProvider（认证状态管理）
- [x] 15.5 创建登录页面：用户名/密码输入、登录按钮、表单验证
- [x] 15.6 对接登录逻辑：输入验证 → API 调用 → 保存 Token → 获取菜单 → 动态生成路由 → 跳转首页
- [x] 15.7 实现登出逻辑：清除 Token → 跳转登录页
- [x] 15.8 实现 Token 自动刷新：401 时用 refreshToken 重试

## 16. Flutter 系统管理模块

- [x] 16.1 创建 `features/system/user/` 模块：用户列表页（AdminDataTable + 搜索 + 新增/编辑弹窗 + 分配角色弹窗 + 重置密码）
- [x] 16.2 创建 `features/system/role/` 模块：角色列表页 + 分配菜单弹窗（树形选择）+ 分配数据权限弹窗
- [x] 16.3 创建 `features/system/menu/` 模块：菜单树形列表页 + 新增/编辑弹窗（含上级菜单选择、图标选择）
- [x] 16.4 创建 `features/system/dept/` 模块：部门树形列表页 + 新增/编辑弹窗
- [x] 16.5 创建 `features/system/post/` 模块：岗位列表页 + 新增/编辑弹窗
- [x] 16.6 创建 `features/system/dict/` 模块：字典类型列表页 + 字典数据列表页
- [x] 16.7 创建 `features/system/config/` 模块：参数配置列表页 + 新增/编辑弹窗

## 17. Flutter 监控模块

- [x] 17.1 创建 `features/monitor/operlog/` 模块：操作日志列表页（分页查询、详情弹窗）
- [x] 17.2 创建 `features/monitor/loginlog/` 模块：登录日志列表页
- [x] 17.3 创建 `features/monitor/online/` 模块：在线用户列表页 + 强制下线
- [x] 17.4 创建 `features/monitor/server/` 模块：服务器监控页（CPU/内存/JVM/磁盘仪表盘）

## 18. Flutter 开发工具模块

- [x] 18.1 创建 `features/devtools/gen/` 模块：代码生成页（表导入、配置编辑、代码预览、批量下载）
- [x] 18.2 创建 `features/devtools/notice/` 模块：消息通知页（通知列表、新增/编辑、已读/未读）
- [x] 18.3 创建 `features/devtools/job/` 模块：定时任务页（任务列表、新增/编辑、启动/暂停/执行、执行日志）

## 19. Flutter 应用入口与整合

- [x] 19.1 重写 `main.dart`，按顺序初始化 Hive → StorageService → DioClient → GoRouter
- [x] 19.2 使用 ProviderScope 包裹根组件，注入全局 Provider
- [x] 19.3 使用 MaterialApp.router 配置 GoRouter
- [x] 19.4 创建首页 Dashboard 页面（统计卡片、快捷入口）

## 20. 验证与收尾

- [x] 20.1 后端编译验证：`mvn compile` 无错误
- [ ] 20.2 前端编译验证：`flutter build web` 无错误
- [ ] 20.3 验证登录登出流程
- [ ] 20.4 验证动态菜单和权限控制
- [ ] 20.5 验证用户管理 CRUD + 分配角色
- [ ] 20.6 验证角色管理 CRUD + 分配菜单 + 数据权限
- [ ] 20.7 验证菜单管理树形 CRUD
- [ ] 20.8 验证操作日志自动记录
