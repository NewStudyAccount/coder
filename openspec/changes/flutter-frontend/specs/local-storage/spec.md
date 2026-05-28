## ADDED Requirements

### Requirement: Hive 存储初始化
应用启动时 SHALL 初始化 Hive，并注册所有必要的 TypeAdapter。

#### Scenario: Hive 初始化
- **WHEN** 应用启动
- **THEN** MUST 调用 `Hive.initFlutter()` 完成初始化，然后注册 TypeAdapter

### Requirement: Token 持久化
系统 SHALL 使用 Hive 存储 Token 对象，包含 accessToken、refreshToken、expiresIn 字段。存储 Key 为 `auth_token`。

#### Scenario: Token 保存
- **WHEN** 用户登录成功获取 Token
- **THEN** MUST 将 Token 对象保存到 Hive 的 `auth_token` Box 中

#### Scenario: Token 读取
- **WHEN** 网络请求需要 Token
- **THEN** MUST 从 Hive 的 `auth_token` Box 中读取 Token 对象

#### Scenario: Token 清除
- **WHEN** 用户登出或 Token 失效
- **THEN** MUST 从 Hive 中删除 `auth_token` 对应的记录

### Requirement: 用户偏好存储
系统 SHALL 使用 Hive 存储用户偏好设置，包含主题模式（light/dark/system）和语言设置。存储 Key 为 `user_preferences`。

#### Scenario: 主题偏好保存
- **WHEN** 用户切换主题模式
- **THEN** MUST 将新的主题模式保存到 Hive

#### Scenario: 主题偏好读取
- **WHEN** 应用启动初始化主题
- **THEN** MUST 从 Hive 读取用户上次选择的主题模式

### Requirement: Storage Key 常量管理
所有 Hive 存储 Key SHALL 集中定义在 `core/constants/storage_keys.dart` 中，使用 `static const String` 声明。

#### Scenario: Storage Key 引用
- **WHEN** 代码中需要访问 Hive 存储
- **THEN** MUST 通过 `StorageKeys` 类的常量引用 Key，不得硬编码字符串

### Requirement: 存储服务封装
系统 SHALL 提供 `StorageService` 封装 Hive 操作，提供类型安全的 get/set/delete 方法。

#### Scenario: 类型安全存储操作
- **WHEN** 调用 `StorageService.getToken()`
- **THEN** MUST 返回 `Token?` 类型，而非原始 dynamic 类型
