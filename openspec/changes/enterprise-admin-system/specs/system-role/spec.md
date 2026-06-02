## ADDED Requirements

### Requirement: 角色 CRUD
系统 SHALL 提供角色的增删改查功能。

#### Scenario: 新增角色
- **WHEN** 管理员填写角色信息（角色名称、角色标识、排序、状态）并提交
- **THEN** 系统创建角色记录

#### Scenario: 修改角色
- **WHEN** 管理员修改角色信息并提交
- **THEN** 系统更新角色记录

#### Scenario: 删除角色
- **WHEN** 管理员删除角色
- **THEN** 系统检查该角色是否已分配给用户，已分配则拒绝删除

### Requirement: 分配菜单权限
系统 SHALL 支持为角色分配菜单权限。

#### Scenario: 分配菜单
- **WHEN** 管理员选择角色并勾选菜单权限
- **THEN** 系统更新 sys_role_menu 关联表

### Requirement: 分配数据权限
系统 SHALL 支持为角色配置数据权限范围。

#### Scenario: 配置数据权限
- **WHEN** 管理员选择角色并配置数据范围（全部数据/自定义/本部门/本部门及以下/仅本人）
- **THEN** 系统更新角色的 data_scope 字段；自定义范围时更新 sys_role_dept 关联表
