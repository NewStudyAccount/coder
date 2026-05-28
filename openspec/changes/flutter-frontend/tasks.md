## 1. 项目初始化

- [x] 1.1 使用 `flutter create flutter-app` 创建新 Flutter 项目
- [x] 1.2 配置 pubspec.yaml，添加核心依赖：go_router、flutter_riverpod、riverpod_annotation、dio、hive、hive_flutter、freezed、freezed_annotation、json_annotation、flutter_hooks、hooks_riverpod
- [x] 1.3 配置 pubspec.yaml，添加开发依赖：build_runner、freezed、json_serializable、riverpod_generator、flutter_lints
- [x] 1.4 执行 `flutter pub get` 验证依赖无冲突
- [x] 1.5 创建 `lib/` 目录结构：core/、shared/、features/ 及其子目录

## 2. 核心基础设施 - 本地存储

- [x] 2.1 创建 `core/constants/storage_keys.dart`，定义所有 Hive Key 常量（authToken、userPreferences）
- [x] 2.2 创建 Token 数据模型（`features/auth/data/models/token_model.dart`），使用 freezed + json_serializable
- [x] 2.3 创建 UserPreferences 数据模型，使用 freezed + json_serializable
- [x] 2.4 创建 `core/storage/storage_service.dart`，封装 Hive 的类型安全 get/set/delete 方法
- [x] 2.5 运行 `build_runner` 生成 Token 和 UserPreferences 的序列化代码

## 3. 核心基础设施 - 网络层

- [x] 3.1 创建 `core/network/api_exception.dart`，定义 ApiException 类（code、message、data）
- [x] 3.2 创建 `core/network/api_result.dart`，定义 ApiResult<T> 密封类（Success/Failure）
- [x] 3.3 创建 `core/network/interceptors/auth_interceptor.dart`，实现请求自动附加 Bearer Token
- [x] 3.4 创建 `core/network/interceptors/error_interceptor.dart`，实现统一错误处理和 401 跳转
- [x] 3.5 创建 `core/network/dio_client.dart`，配置 Dio 单例（baseURL、超时、拦截器）
- [x] 3.6 创建 `core/constants/api_constants.dart`，定义 API 路径常量和 baseURL

## 4. 核心基础设施 - 主题系统

- [x] 4.1 创建 `core/theme/app_theme.dart`，定义 Material 3 亮色和暗色 ThemeData（Seed Color 方式）
- [x] 4.2 创建 `core/theme/theme_provider.dart`，使用 Riverpod 管理主题模式（light/dark/system）
- [x] 4.3 实现主题偏好持久化：切换主题时保存到 Hive，启动时从 Hive 恢复

## 5. 核心基础设施 - 路由系统

- [x] 5.1 创建 `core/router/app_router.dart`，定义 GoRouter 路由表（/auth/login、/auth/register、/home、/profile）
- [x] 5.2 实现 ShellRoute 主布局嵌套（底部导航 + 顶部 AppBar）
- [x] 5.3 实现 redirect 认证守卫（未登录重定向登录页，已登录重定向首页）
- [x] 5.4 创建底部导航组件（首页、我的 两个 Tab）

## 6. 共享 UI 组件

- [x] 6.1 创建 `shared/widgets/loading_widget.dart`，支持全屏遮罩和内嵌两种模式
- [x] 6.2 创建 `shared/widgets/error_widget.dart`，显示错误信息和重试按钮
- [x] 6.3 创建 `shared/widgets/empty_widget.dart`，显示空状态图标和提示文字
- [x] 6.4 创建 `shared/layouts/main_layout.dart`，主布局组件（ShellRoute 的子组件）

## 7. 认证模块 - Domain 层

- [x] 7.1 创建 `features/auth/domain/entities/user.dart`，定义 User 实体
- [x] 7.2 创建 `features/auth/domain/repositories/auth_repository.dart`，定义 AuthRepository 抽象接口（login、register、logout、getUserInfo、refreshToken）

## 8. 认证模块 - Data 层

- [x] 8.1 创建 `features/auth/data/models/login_request.dart`，使用 freezed 定义登录请求 DTO
- [x] 8.2 创建 `features/auth/data/models/register_request.dart`，使用 freezed 定义注册请求 DTO
- [x] 8.3 创建 `features/auth/data/models/user_model.dart`，使用 freezed 定义用户 DTO（含 JSON 序列化）
- [x] 8.4 创建 `features/auth/data/datasources/auth_remote_data_source.dart`，封装认证相关 API 调用
- [x] 8.5 创建 `features/auth/data/repositories/auth_repository_impl.dart`，实现 AuthRepository 接口，完成 Model → Entity 转换
- [x] 8.6 运行 `build_runner` 生成所有认证模块的序列化代码

## 9. 认证模块 - Presentation 层

- [x] 9.1 创建 `features/auth/presentation/providers/auth_provider.dart`，使用 Riverpod 管理认证状态（AsyncValue<AuthState>）
- [x] 9.2 创建 `features/auth/presentation/pages/login_page.dart`，实现登录页面 UI 和表单验证
- [x] 9.3 创建 `features/auth/presentation/pages/register_page.dart`，实现注册页面 UI 和表单验证
- [x] 9.4 对接登录逻辑：输入验证 → 调用 AuthRepository.login → 保存 Token → 跳转首页
- [x] 9.5 对接注册逻辑：输入验证 → 调用 AuthRepository.register → 自动登录 → 跳转首页
- [x] 9.6 实现 Token 自动刷新：401 时使用 refreshToken 重试，失败则跳转登录页

## 10. 首页与个人中心

- [x] 10.1 创建 `features/home/presentation/pages/home_page.dart`，实现首页占位页面
- [x] 10.2 创建 `features/profile/presentation/pages/profile_page.dart`，实现个人中心页面（显示用户信息、登出按钮）
- [x] 10.3 对接登出逻辑：清除 Token → 跳转登录页

## 11. 应用入口整合

- [x] 11.1 重写 `main.dart`，按顺序初始化 Hive → StorageService → DioClient → GoRouter
- [x] 11.2 使用 ProviderScope 包裹根组件，注入全局 Provider
- [x] 11.3 使用 MaterialApp.router 配置 GoRouter
- [x] 11.4 验证完整启动流程：冷启动 → 检查 Token → 自动登录或跳转登录页

## 12. 验证与收尾

- [ ] 12.1 验证登录流程：输入凭证 → 登录成功 → 跳转首页 → 显示用户信息
- [ ] 12.2 验证注册流程：填写信息 → 注册成功 → 自动登录 → 跳转首页
- [ ] 12.3 验证登出流程：点击登出 → 清除数据 → 跳转登录页
- [ ] 12.4 验证主题切换：切换亮/暗/跟随系统 → 立即生效 → 重启后保持
- [ ] 12.5 验证 Token 持久化：登录后杀进程重启 → 自动进入首页
- [ ] 12.6 验证 401 处理：Token 过期 → 自动刷新或跳转登录页
