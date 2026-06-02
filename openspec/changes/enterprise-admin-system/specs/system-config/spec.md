## ADDED Requirements

### Requirement: 参数配置 CRUD
系统 SHALL 提供系统参数的增删改查功能。

#### Scenario: 新增参数
- **WHEN** 管理员填写参数信息（参数名称、参数键、参数值、系统内置/否）并提交
- **THEN** 系统创建参数记录

#### Scenario: 修改参数
- **WHEN** 管理员修改参数值并提交
- **THEN** 系统更新参数记录，同时刷新 Redis 缓存

#### Scenario: 删除参数
- **WHEN** 管理员删除参数
- **THEN** 系统检查该参数是否为系统内置，系统内置参数不允许删除

### Requirement: 参数缓存
系统 SHALL 将参数配置缓存到 Redis。

#### Scenario: 缓存查询
- **WHEN** 系统需要读取参数值
- **THEN** 优先从 Redis 获取，缓存不存在时从数据库加载

#### Scenario: 按参数键查询
- **WHEN** 通过参数键查询参数值
- **THEN** 系统返回对应的参数值字符串
