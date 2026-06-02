## ADDED Requirements

### Requirement: 字典类型 CRUD
系统 SHALL 提供字典类型的增删改查功能。

#### Scenario: 新增字典类型
- **WHEN** 管理员填写字典类型信息（字典名称、字典类型、状态）并提交
- **THEN** 系统创建字典类型记录

#### Scenario: 修改字典类型
- **WHEN** 管理员修改字典类型信息并提交
- **THEN** 系统更新字典类型记录，同时刷新 Redis 缓存

#### Scenario: 删除字典类型
- **WHEN** 管理员删除字典类型
- **THEN** 系统检查该类型下是否有字典数据，有则拒绝删除

### Requirement: 字典数据 CRUD
系统 SHALL 提供字典数据的增删改查功能。

#### Scenario: 新增字典数据
- **WHEN** 管理员填写字典数据（字典标签、字典值、排序、状态）并提交
- **THEN** 系统创建字典数据记录，同时刷新 Redis 缓存

#### Scenario: 修改字典数据
- **WHEN** 管理员修改字典数据并提交
- **THEN** 系统更新字典数据记录，同时刷新 Redis 缓存

### Requirement: 字典缓存
系统 SHALL 将字典数据缓存到 Redis，提供快速查询接口。

#### Scenario: 缓存查询
- **WHEN** 前端请求字典数据
- **THEN** 系统优先从 Redis 缓存获取，缓存不存在时从数据库加载并写入缓存
