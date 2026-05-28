## ADDED Requirements

### Requirement: 登录功能
系统 SHALL 提供登录页面，包含用户名/邮箱输入框、密码输入框、登录按钮。登录成功后保存 Token 到本地存储并跳转首页。

#### Scenario: 登录成功
- **WHEN** 用户输入正确的用户名和密码并点击登录
- **THEN** MUST 调用 `POST /api/auth/login`，成功后保存 Token 到 Hive，跳转到 `/home`

#### Scenario: 登录失败
- **WHEN** 用户输入错误的用户名或密码
- **THEN** MUST 显示错误提示信息（如"用户名或密码错误"），不跳转页面

#### Scenario: 表单验证
- **WHEN** 用户未填写用户名或密码就点击登录
- **THEN** MUST 在对应输入框下方显示验证错误提示

### Requirement: 注册功能
系统 SHALL 提供注册页面，包含用户名、邮箱、密码、确认密码输入框和注册按钮。注册成功后自动登录并跳转首页。

#### Scenario: 注册成功
- **WHEN** 用户填写完整且合法的注册信息并点击注册
- **THEN** MUST 调用 `POST /api/auth/register`，成功后自动执行登录流程

#### Scenario: 密码不一致
- **WHEN** 用户输入的密码和确认密码不一致
- **THEN** MUST 在确认密码输入框下方显示"密码不一致"提示

#### Scenario: 注册失败
- **WHEN** 后端返回注册失败（如用户名已存在）
- **THEN** MUST 显示后端返回的错误信息

### Requirement: 登出功能
系统 SHALL 提供登出功能，清除本地 Token 和用户信息，跳转到登录页。

#### Scenario: 登出操作
- **WHEN** 用户点击登出按钮
- **THEN** MUST 清除 Hive 中的 Token 和用户信息，跳转到 `/auth/login`

### Requirement: Token 自动刷新
系统 SHALL 在 Token 过期前自动刷新。当 API 返回 401 时，尝试使用 refreshToken 获取新 Token，成功后重试原请求；刷新失败则跳转登录页。

#### Scenario: Token 刷新成功
- **WHEN** API 请求返回 401 且本地存在 refreshToken
- **THEN** MUST 调用 Token 刷新接口获取新 Token，保存后自动重试原请求

#### Scenario: Token 刷新失败
- **WHEN** Token 刷新接口也返回失败
- **THEN** MUST 清除本地 Token，跳转到登录页

### Requirement: 认证状态持久化
应用重启后 SHALL 自动检查本地 Token，若 Token 有效则自动进入已登录状态。

#### Scenario: 应用重启自动登录
- **WHEN** 应用启动且本地存在未过期的 Token
- **THEN** MUST 自动设置为已登录状态，直接进入首页

#### Scenario: 应用重启 Token 过期
- **WHEN** 应用启动且本地 Token 已过期
- **THEN** MUST 清除本地 Token，进入登录页

### Requirement: 获取用户信息
登录成功后 SHALL 调用 `GET /api/auth/userinfo` 获取用户详细信息并缓存。

#### Scenario: 获取用户信息成功
- **WHEN** 登录成功后自动请求用户信息
- **THEN** MUST 将用户信息存储在 AuthState Provider 中
