## ADDED Requirements

### Requirement: 在线用户列表
系统 SHALL 展示当前在线用户列表。

#### Scenario: 查看在线用户
- **WHEN** 管理员访问在线用户页面
- **THEN** 系统从 Redis 获取所有活跃会话，展示用户名、登录 IP、登录时间、浏览器

### Requirement: 强制下线
系统 SHALL 支持强制指定用户下线。

#### Scenario: 强制下线用户
- **WHEN** 管理员点击强制下线
- **THEN** 系统删除该用户的 Redis Token，该用户后续请求返回 401 并跳转登录页
