## ADDED Requirements

### Requirement: Dio 客户端单例
系统 SHALL 提供 Dio 客户端单例，配置 baseURL、连接超时、接收超时，并挂载认证拦截器和错误拦截器。

#### Scenario: Dio 客户端初始化
- **WHEN** 应用启动并初始化 Dio 客户端
- **THEN** baseURL MUST 从环境配置读取，连接超时为 15 秒，接收超时为 15 秒

#### Scenario: 拦截器挂载
- **WHEN** Dio 客户端初始化完成
- **THEN** MUST 挂载 AuthInterceptor 和 ErrorInterceptor

### Requirement: 认证拦截器
AuthInterceptor SHALL 在每个请求中自动附加 `Authorization: Bearer <token>` 头。当 Token 不存在时，不附加该头。

#### Scenario: 请求携带 Token
- **WHEN** 发送 HTTP 请求且本地存储中存在有效 Token
- **THEN** 请求头 MUST 包含 `Authorization: Bearer <token>`

#### Scenario: 请求无 Token
- **WHEN** 发送 HTTP 请求且本地存储中无 Token
- **THEN** 请求头不得包含 `Authorization` 字段

### Requirement: 错误拦截器
ErrorInterceptor SHALL 统一处理后端响应格式 `{ code, message, data }`。当 `code != 200` 时抛出 ApiException；当响应状态码为 401 时，清除本地 Token 并跳转登录页。

#### Scenario: 业务错误处理
- **WHEN** 后端返回 `{ "code": 400, "message": "参数错误", "data": null }`
- **THEN** MUST 抛出 ApiException，包含 code=400 和 message="参数错误"

#### Scenario: 401 未授权处理
- **WHEN** 后端返回 HTTP 401 状态码
- **THEN** MUST 清除本地存储的 Token，并跳转到登录页

#### Scenario: 网络异常处理
- **WHEN** 请求因网络原因失败（超时、无连接等）
- **THEN** MUST 抛出 ApiException，code 为对应 DioExceptionType 的错误码

### Requirement: 统一响应包装 ApiResult
系统 SHALL 提供 `ApiResult<T>` 类型，封装请求成功（`Success<T>`）和失败（`Failure`）两种状态，Repository 方法 SHALL 返回 `ApiResult<T>`。

#### Scenario: 请求成功
- **WHEN** API 请求成功且后端返回 `code == 200`
- **THEN** MUST 返回 `ApiResult.success(data)`，data 为解析后的泛型数据

#### Scenario: 请求失败
- **WHEN** API 请求失败（业务错误或网络异常）
- **THEN** MUST 返回 `ApiResult.failure(ApiException)`

### Requirement: RESTful API Service 封装
每个业务模块的 Data 层 SHALL 提供 ApiService 类，封装对后端 RESTful API 的调用。方法命名遵循 REST 约定：`getXxx`、`listXxx`、`createXxx`、`updateXxx`、`deleteXxx`。

#### Scenario: API Service 方法命名
- **WHEN** 为用户模块创建 ApiService
- **THEN** MUST 包含 `getUser`、`listUsers`、`createUser`、`updateUser`、`deleteUser` 等方法
