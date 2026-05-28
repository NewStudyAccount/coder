## ADDED Requirements

### Requirement: Riverpod Provider 体系
系统 SHALL 使用 Riverpod 作为状态管理方案，通过 `ProviderScope` 在应用根部注入。所有 Provider MUST 使用 `riverpod_generator` 代码生成方式定义。

#### Scenario: ProviderScope 初始化
- **WHEN** 应用启动
- **THEN** 根组件 MUST 被 `ProviderScope` 包裹

#### Scenario: Provider 定义方式
- **WHEN** 定义新的 Provider
- **THEN** MUST 使用 `@riverpod` 注解和代码生成，不得手写 Provider

### Requirement: 异步状态处理
系统 SHALL 使用 `AsyncValue<T>` 处理异步操作的三种状态：加载中（`AsyncLoading`）、成功（`AsyncData`）、失败（`AsyncError`）。

#### Scenario: 异步加载状态
- **WHEN** Provider 正在执行异步操作
- **THEN** `AsyncValue` MUST 处于 `AsyncLoading` 状态

#### Scenario: 异步成功状态
- **WHEN** 异步操作成功完成
- **THEN** `AsyncValue` MUST 处于 `AsyncData` 状态，包含返回数据

#### Scenario: 异步失败状态
- **WHEN** 异步操作抛出异常
- **THEN** `AsyncValue` MUST 处于 `AsyncError` 状态，包含异常信息

### Requirement: 全局认证状态
系统 SHALL 提供全局 AuthState Provider，管理当前用户的登录状态和用户信息。该 Provider MUST 可在任意功能模块中访问。

#### Scenario: 未登录状态
- **WHEN** 本地无有效 Token
- **THEN** AuthState MUST 表示未登录状态

#### Scenario: 已登录状态
- **WHEN** 本地存在有效 Token 且用户信息已加载
- **THEN** AuthState MUST 包含用户信息

### Requirement: Provider 组织规范
每个功能模块的 Provider SHALL 定义在该模块的 `presentation/providers/` 目录下。跨模块共享的 Provider 定义在 `core/` 对应目录下。

#### Scenario: 模块内 Provider 定位
- **WHEN** 需要查找认证相关的 Provider
- **THEN** MUST 在 `features/auth/presentation/providers/` 目录下找到

#### Scenario: 全局 Provider 定位
- **WHEN** 需要查找全局认证状态 Provider
- **THEN** MUST 在 `core/` 相关目录下找到
