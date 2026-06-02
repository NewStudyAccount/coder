## ADDED Requirements

### Requirement: 登录日志记录
系统 SHALL 自动记录所有登录尝试。

#### Scenario: 登录成功记录
- **WHEN** 用户登录成功
- **THEN** 系统记录用户名、IP、登录地点、浏览器、操作系统、状态为成功

#### Scenario: 登录失败记录
- **WHEN** 用户登录失败
- **THEN** 系统记录用户名、IP、失败原因，状态为失败

### Requirement: 登录日志查询
系统 SHALL 提供登录日志的分页查询功能。

#### Scenario: 查询登录日志
- **WHEN** 管理员访问登录日志页面
- **THEN** 系统返回分页日志数据，支持按用户名、IP、状态、时间范围筛选

### Requirement: 登录日志清理
系统 SHALL 支持清理过期的登录日志。

#### Scenario: 清理日志
- **WHEN** 管理员执行清理操作
- **THEN** 系统删除指定时间范围之前的登录日志
