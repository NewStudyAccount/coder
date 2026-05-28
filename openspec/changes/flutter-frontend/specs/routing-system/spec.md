## ADDED Requirements

### Requirement: 声明式路由表
系统 SHALL 使用 GoRouter 声明式路由表，所有路由集中定义在 `core/router/app_router.dart` 中。路由路径 SHALL 使用 kebab-case 命名。

#### Scenario: 路由定义格式
- **WHEN** 定义新路由
- **THEN** MUST 使用 `GoRoute` 组件，path 使用 kebab-case（如 `/auth/login`），name 使用 PascalCase（如 `AuthLogin`）

#### Scenario: 路由导航
- **WHEN** 调用 `context.go('/auth/login')`
- **THEN** MUST 渲染对应的 LoginPage 组件

### Requirement: 认证路由守卫
GoRouter SHALL 通过 `redirect` 函数实现认证守卫。未登录用户访问受保护路由时，重定向到登录页；已登录用户访问登录页时，重定向到首页。

#### Scenario: 未登录访问受保护路由
- **WHEN** 未登录用户访问 `/home` 或其他受保护路由
- **THEN** MUST 重定向到 `/auth/login`

#### Scenario: 已登录访问登录页
- **WHEN** 已登录用户访问 `/auth/login`
- **THEN** MUST 重定向到 `/home`

#### Scenario: 未登录访问公开路由
- **WHEN** 未登录用户访问 `/auth/login` 或 `/auth/register`
- **THEN** 正常渲染对应页面，不发生重定向

### Requirement: ShellRoute 主布局嵌套
系统 SHALL 使用 `ShellRoute` 实现主布局嵌套，受保护路由共享底部导航栏和顶部 AppBar。

#### Scenario: 主布局渲染
- **WHEN** 用户访问 `/home`、`/profile` 等受保护路由
- **THEN** 页面 MUST 嵌套在 ShellRoute 内，显示底部导航栏和顶部 AppBar

#### Scenario: 登录页无主布局
- **WHEN** 用户访问 `/auth/login` 或 `/auth/register`
- **THEN** 页面不得显示底部导航栏和顶部 AppBar

### Requirement: 底部导航
主布局 SHALL 包含底部导航栏，至少包含"首页"和"我的"两个 Tab。

#### Scenario: Tab 切换
- **WHEN** 用户点击底部导航栏的"首页"Tab
- **THEN** MUST 导航到 `/home` 路由

#### Scenario: Tab 切换到我的
- **WHEN** 用户点击底部导航栏的"我的"Tab
- **THEN** MUST 导航到 `/profile` 路由

### Requirement: 路由路径定义
系统 SHALL 定义以下核心路由路径：
- `/auth/login` — 登录页
- `/auth/register` — 注册页
- `/home` — 首页
- `/profile` — 个人中心

#### Scenario: 所有核心路由可访问
- **WHEN** 通过 GoRouter 导航到上述任一路径
- **THEN** MUST 正确渲染对应的页面组件
