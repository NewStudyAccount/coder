## ADDED Requirements

### Requirement: 企业级数据表格组件
系统 SHALL 提供 AdminDataTable 通用数据表格组件，支持分页、排序、筛选、行选择、工具栏。

#### Scenario: 服务端分页
- **WHEN** 表格数据量超过一页
- **THEN** 表格底部显示分页控件，切换页码时请求服务端数据

#### Scenario: 列排序
- **WHEN** 用户点击可排序列的表头
- **THEN** 表格按该列升序或降序重新请求数据

#### Scenario: 行选择
- **WHEN** 表格启用多选模式
- **THEN** 每行显示复选框，选中行数据可通过回调获取

#### Scenario: 工具栏
- **WHEN** 表格配置了工具栏
- **THEN** 表格上方显示搜索框、新增/修改/删除/导出等操作按钮，按钮根据权限显示/隐藏

### Requirement: 表格状态管理
系统 SHALL 支持表格的加载、空数据、错误三种状态展示。

#### Scenario: 加载状态
- **WHEN** 数据正在请求中
- **THEN** 表格显示加载动画

#### Scenario: 空数据状态
- **WHEN** 查询结果为空
- **THEN** 表格显示空数据提示

#### Scenario: 错误状态
- **WHEN** 请求失败
- **THEN** 表格显示错误信息和重试按钮
