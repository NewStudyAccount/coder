## ADDED Requirements

### Requirement: 动态路由生成
系统 SHALL 根据用户菜单权限动态生成前端路由。

#### Scenario: 登录后生成路由
- **WHEN** 用户登录成功后
- **THEN** 前端调用获取菜单接口，根据菜单数据动态注册 GoRouter 路由

#### Scenario: 菜单类型路由规则
- **WHEN** 处理菜单数据生成路由
- **THEN** 目录类型生成父路由 ShellRoute，菜单类型生成子路由，按钮类型不生成路由

### Requirement: 按钮级权限控制
系统 SHALL 支持按钮级别的权限控制。

#### Scenario: 权限组件控制可见性
- **WHEN** 页面中使用 PermissionWidget 包裹按钮，传入权限标识
- **THEN** 当前用户拥有该权限标识时显示按钮，否则隐藏

#### Scenario: 无权限路由拦截
- **WHEN** 用户访问没有权限的页面路由
- **THEN** 系统跳转到 403 无权限页面

### Requirement: 菜单权限过滤
系统 SHALL 根据用户权限过滤侧边菜单。

#### Scenario: 仅显示有权限菜单
- **WHEN** 渲染侧边菜单
- **THEN** 仅显示当前用户角色拥有权限的菜单项
