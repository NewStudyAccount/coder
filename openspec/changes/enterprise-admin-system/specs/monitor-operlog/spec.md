## ADDED Requirements

### Requirement: 操作日志记录
系统 SHALL 通过 AOP 注解自动记录用户操作日志。

#### Scenario: 自动记录操作
- **WHEN** 用户执行标注了 @OperLog 注解的接口
- **THEN** 系统异步记录操作人、操作模块、操作类型、请求参数、返回结果、IP 地址、耗时

#### Scenario: 登录日志自动记录
- **WHEN** 用户登录成功或失败
- **THEN** 系统自动记录登录日志（用户名、IP、登录地点、浏览器、操作系统、状态、消息）

### Requirement: 操作日志查询
系统 SHALL 提供操作日志的分页查询功能。

#### Scenario: 查询操作日志
- **WHEN** 管理员访问操作日志页面
- **THEN** 系统返回分页日志数据，支持按操作模块、操作人、操作类型、时间范围筛选

### Requirement: 日志详情
系统 SHALL 支持查看操作日志详情。

#### Scenario: 查看日志详情
- **WHEN** 管理员点击某条日志
- **THEN** 系统展示完整的请求参数和返回结果
