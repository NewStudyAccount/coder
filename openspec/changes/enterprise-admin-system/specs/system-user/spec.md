## ADDED Requirements

### Requirement: 用户 CRUD
系统 SHALL 提供用户的增删改查功能，支持分页查询、条件搜索。

#### Scenario: 新增用户
- **WHEN** 管理员填写用户信息（用户名、昵称、部门、岗位、角色）并提交
- **THEN** 系统创建用户记录，密码使用 BCrypt 加密存储

#### Scenario: 修改用户
- **WHEN** 管理员修改用户信息并提交
- **THEN** 系统更新用户记录

#### Scenario: 删除用户
- **WHEN** 管理员删除一个或多个用户
- **THEN** 系统逻辑删除用户记录（status 置为禁用），同时删除用户角色关联

#### Scenario: 分页查询用户
- **WHEN** 管理员访问用户列表页面
- **THEN** 系统返回分页用户数据，支持按用户名、手机号、状态、部门筛选

### Requirement: 分配角色
系统 SHALL 支持为用户分配一个或多个角色。

#### Scenario: 分配角色
- **WHEN** 管理员选择用户并分配角色
- **THEN** 系统更新 sys_user_role 关联表

### Requirement: 重置密码
系统 SHALL 支持管理员重置用户密码。

#### Scenario: 重置密码
- **WHEN** 管理员点击重置密码
- **THEN** 系统将用户密码重置为默认密码（123456），使用 BCrypt 加密

### Requirement: 用户数据权限
系统 SHALL 根据当前登录用户的数据权限范围过滤用户列表数据。

#### Scenario: 全部数据权限
- **WHEN** 用户角色配置为"全部数据权限"
- **THEN** 用户可查看所有部门的用户数据

#### Scenario: 本部门数据权限
- **WHEN** 用户角色配置为"本部门数据权限"
- **THEN** 用户只能查看本部门的用户数据
