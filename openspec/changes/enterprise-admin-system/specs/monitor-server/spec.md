## ADDED Requirements

### Requirement: 服务器监控信息
系统 SHALL 展示服务器实时运行信息。

#### Scenario: 查看服务器信息
- **WHEN** 管理员访问服务器监控页面
- **THEN** 系统展示 CPU 使用率、内存使用率、JVM 堆内存、磁盘使用率、操作系统信息、JVM 信息

#### Scenario: 实时刷新
- **WHEN** 管理员点击刷新按钮
- **THEN** 系统重新采集并展示最新服务器信息
