## ADDED Requirements

### Requirement: 消息通知 CRUD
系统 SHALL 提供站内信的增删改查功能。

#### Scenario: 发布通知
- **WHEN** 管理员填写通知标题、内容、类型（通知/公告）并发布
- **THEN** 系统创建通知记录，所有用户可在消息中心看到

#### Scenario: 查询通知列表
- **WHEN** 用户访问消息中心
- **THEN** 系统返回通知列表，区分已读和未读

### Requirement: 通知已读状态
系统 SHALL 支持标记通知为已读。

#### Scenario: 标记已读
- **WHEN** 用户点击某条通知
- **THEN** 系统将该通知标记为已读

#### Scenario: 全部已读
- **WHEN** 用户点击全部已读
- **THEN** 系统将所有未读通知标记为已读
