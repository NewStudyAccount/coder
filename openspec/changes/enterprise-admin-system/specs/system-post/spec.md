## ADDED Requirements

### Requirement: 岗位 CRUD
系统 SHALL 提供岗位的增删改查功能。

#### Scenario: 新增岗位
- **WHEN** 管理员填写岗位信息（岗位编码、岗位名称、排序、状态）并提交
- **THEN** 系统创建岗位记录

#### Scenario: 修改岗位
- **WHEN** 管理员修改岗位信息并提交
- **THEN** 系统更新岗位记录

#### Scenario: 删除岗位
- **WHEN** 管理员删除岗位
- **THEN** 系统检查该岗位是否已分配给用户，已分配则拒绝删除

#### Scenario: 查询岗位列表
- **WHEN** 管理员访问岗位管理页面
- **THEN** 系统返回岗位列表，支持按岗位编码、名称、状态筛选
