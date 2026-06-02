## ADDED Requirements

### Requirement: JWT 双 Token 认证
系统 SHALL 使用 JWT 双 Token 机制进行用户认证，accessToken 有效期 2 小时，refreshToken 有效期 7 天。

#### Scenario: 登录成功获取双 Token
- **WHEN** 用户使用正确的用户名和密码登录
- **THEN** 系统返回 accessToken 和 refreshToken，并将 Token 信息存入 Redis

#### Scenario: accessToken 过期自动刷新
- **WHEN** 请求返回 401 且 refreshToken 有效
- **THEN** 系统自动使用 refreshToken 获取新的 accessToken，并重试原请求

#### Scenario: refreshToken 过期
- **WHEN** refreshToken 也已过期
- **THEN** 系统清除本地 Token，跳转到登录页

### Requirement: 登录登出
系统 SHALL 提供登录和登出功能。

#### Scenario: 登录成功
- **WHEN** 用户输入正确的用户名密码并提交
- **THEN** 系统验证通过，存储 Token，跳转到首页

#### Scenario: 登录失败
- **WHEN** 用户输入错误的用户名或密码
- **THEN** 系统返回错误提示，不跳转

#### Scenario: 登出
- **WHEN** 用户点击登出按钮
- **THEN** 系统清除本地 Token 和 Redis 会话，跳转到登录页

### Requirement: 在线用户管理
系统 SHALL 展示当前在线用户列表，支持强制下线。

#### Scenario: 查看在线用户
- **WHEN** 管理员访问在线用户页面
- **THEN** 系统从 Redis 获取所有活跃 Token 对应的用户列表

#### Scenario: 强制下线
- **WHEN** 管理员点击强制下线按钮
- **THEN** 系统删除该用户的 Redis Token 记录，该用户后续请求返回 401
