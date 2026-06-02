## ADDED Requirements

### Requirement: 定时任务 CRUD
系统 SHALL 提供定时任务的增删改查功能。

#### Scenario: 新增任务
- **WHEN** 管理员填写任务信息（任务名称、任务组、调用目标、Cron 表达式、执行策略）并提交
- **THEN** 系统创建任务记录并注册到调度器

#### Scenario: 修改任务
- **WHEN** 管理员修改任务信息并提交
- **THEN** 系统更新任务记录并重新注册调度

#### Scenario: 删除任务
- **WHEN** 管理员删除任务
- **THEN** 系统删除任务记录并从调度器移除

### Requirement: 任务执行控制
系统 SHALL 支持任务的启动、暂停、立即执行。

#### Scenario: 启动任务
- **WHEN** 管理员点击启动
- **THEN** 系统将任务状态改为启用，注册到调度器

#### Scenario: 暂停任务
- **WHEN** 管理员点击暂停
- **THEN** 系统将任务状态改为暂停，从调度器移除

#### Scenario: 立即执行
- **WHEN** 管理员点击执行一次
- **THEN** 系统立即触发任务执行

### Requirement: 任务执行日志
系统 SHALL 记录每次任务执行的结果。

#### Scenario: 查看执行日志
- **WHEN** 管理员查看任务执行日志
- **THEN** 系统展示执行时间、执行结果、异常信息
