# OAuth2 逻辑补充 — 增加更多第三方接入

## 问题分析

当前 `Oauth2Service` 存在严重的可扩展性问题：
1. **硬编码 if-else**：每增加一个提供方，需在 `getAuthorizeUrl()` 和 `handleCallback()` 中增加分支
2. **大量 @Value 字段**：每个提供方 6 个配置项，5 个提供方就需要 30 个字段
3. **重复代码**：`exchangeGithubToken()`、`exchangeGoogleToken()` 逻辑几乎相同
4. **用户信息字段映射不统一**：每个提供方的 JSON 字段名不同（如 GitHub 用 `login`，Google 用 `name`）

## 解决方案：策略模式 + 配置驱动

将每个 OAuth2 提供方抽象为 **Oauth2Provider** 策略接口，通过配置文件注册提供方，运行时按 provider 名称查找对应策略。

### 新增提供方列表

| 提供方 | provider 标识 | 说明 |
|--------|--------------|------|
| GitHub | github | 已有，保留 |
| Google | google | 已有，保留 |
| 微信 | wechat | 新增，国内最常用 |
| 钉钉 | dingtalk | 新增，企业场景 |
| 飞书 | feishu | 新增，企业场景 |
| Gitee | gitee | 新增，国内代码托管 |
| 微软 | microsoft | 新增，国际企业场景 |

---

## 后端改动

### 1. 新增 `Oauth2ProviderConfig.java` — 提供方配置类

从 `application.yml` 的 `oauth2.providers` Map 中自动加载所有提供方配置，每个提供方包含：
- `clientId`、`clientSecret`、`redirectUri`
- `authorizeUrl`、`tokenUrl`、`userInfoUrl`
- `scope`（授权范围）
- `userInfoMapping`（用户信息字段映射：idField, usernameField, avatarField, emailField）

### 2. 新增 `Oauth2Provider.java` — 策略接口

```java
public interface Oauth2Provider {
    String getName();                          // 提供方标识
    String buildAuthorizeUrl(String state);    // 构建授权URL
    String exchangeToken(String code);         // code 换 access_token
    Oauth2UserInfo fetchUserInfo(String accessToken); // 获取用户信息
}
```

### 3. 新增 `Oauth2UserInfo.java` — 统一用户信息

```java
public class Oauth2UserInfo {
    private String id;
    private String username;
    private String avatarUrl;
    private String email;
}
```

### 4. 新增 `AbstractOauth2Provider.java` — 通用基类

实现 `Oauth2Provider` 接口的通用逻辑：
- `buildAuthorizeUrl()`：拼接 `authorizeUrl + client_id + redirect_uri + scope + state`
- `exchangeToken()`：POST `tokenUrl`，表单提交 code + client_id + client_secret + redirect_uri + grant_type
- `fetchUserInfo()`：GET `userInfoUrl`，带 Bearer token，根据 `userInfoMapping` 提取字段
- 子类只需覆盖有差异的部分（如微信的 token 请求需加 appid 参数）

### 5. 新增各提供方实现类

| 类名 | 覆盖逻辑 |
|------|----------|
| `GithubProvider.java` | 继承 Abstract，默认即可 |
| `GoogleProvider.java` | 继承 Abstract，默认即可 |
| `WechatProvider.java` | 覆盖 `exchangeToken()`（需加 appid/appsecret 参数，返回 openid） |
| `DingtalkProvider.java` | 覆盖 `exchangeToken()` + `fetchUserInfo()`（钉钉需先获取临时授权码再换用户信息） |
| `FeishuProvider.java` | 覆盖 `exchangeToken()`（需加 app_id/app_secret） |
| `GiteeProvider.java` | 继承 Abstract，默认即可 |
| `MicrosoftProvider.java` | 继承 Abstract，scope 默认 `User.Read` |

### 6. 新增 `Oauth2ProviderRegistry.java` — 提供方注册中心

- `@PostConstruct` 初始化：根据 yml 配置自动创建所有 Provider 实例
- `getProvider(String name)` 按 name 查找
- `getAvailableProviders()` 返回所有可用提供方列表

### 7. 重写 `Oauth2Service.java`

移除所有 `@Value` 字段和 if-else 分支，改为：
- 注入 `Oauth2ProviderRegistry`
- `getAuthorizeUrl()` → `registry.getProvider(provider).buildAuthorizeUrl(state)`
- `handleCallback()` → `registry.getProvider(provider).exchangeToken(code)` + `fetchUserInfo()`
- 统一的用户查找/创建/绑定逻辑不变

### 8. 新增 `Oauth2Controller` 接口

- `GET /oauth2/providers` — 返回所有可用提供方列表（前端动态渲染按钮）

### 9. 更新 `application.yml`

将 `oauth2.github` / `oauth2.google` 改为统一的 `oauth2.providers` Map 结构：

```yaml
oauth2:
  frontend-redirect-url: http://localhost:5173/oauth2/callback
  providers:
    github:
      client-id: your-github-client-id
      client-secret: your-github-client-secret
      redirect-uri: http://localhost:8080/api/oauth2/callback/github
      authorize-url: https://github.com/login/oauth/authorize
      token-url: https://github.com/login/oauth/access_token
      user-info-url: https://api.github.com/user
      scope: user:email
      user-info-mapping:
        id-field: id
        username-field: login
        avatar-field: avatar_url
        email-field: email
    google:
      # ...
    wechat:
      client-id: your-wechat-app-id
      client-secret: your-wechat-app-secret
      redirect-uri: http://localhost:8080/api/oauth2/callback/wechat
      authorize-url: https://open.weixin.qq.com/connect/qrconnect
      token-url: https://api.weixin.qq.com/sns/oauth2/access_token
      user-info-url: https://api.weixin.qq.com/sns/userinfo
      scope: snsapi_login
      user-info-mapping:
        id-field: openid
        username-field: nickname
        avatar-field: headimgurl
        email-field: email
    dingtalk:
      client-id: your-dingtalk-app-id
      client-secret: your-dingtalk-app-secret
      redirect-uri: http://localhost:8080/api/oauth2/callback/dingtalk
      authorize-url: https://login.dingtalk.com/login/qrcode.htm
      token-url: https://oapi.dingtalk.com/sns/gettoken
      user-info-url: https://oapi.dingtalk.com/sns/getuserinfo
      scope: snsapi_login
      user-info-mapping:
        id-field: openid
        username-field: nick
        avatar-field: avatar_url
        email-field: email
    feishu:
      client-id: your-feishu-app-id
      client-secret: your-feishu-app-secret
      redirect-uri: http://localhost:8080/api/oauth2/callback/feishu
      authorize-url: https://open.feishu.cn/open-apis/authen/v1/authorize
      token-url: https://open.feishu.cn/open-apis/authen/v1/oidc/access_token
      user-info-url: https://open.feishu.cn/open-apis/authen/v1/user_info
      scope: contact:user.base:readonly
      user-info-mapping:
        id-field: open_id
        username-field: name
        avatar-field: avatar_url
        email-field: email
    gitee:
      client-id: your-gitee-client-id
      client-secret: your-gitee-client-secret
      redirect-uri: http://localhost:8080/api/oauth2/callback/gitee
      authorize-url: https://gitee.com/oauth/authorize
      token-url: https://gitee.com/oauth/token
      user-info-url: https://gitee.com/api/v5/user
      scope: user_info
      user-info-mapping:
        id-field: id
        username-field: login
        avatar-field: avatar_url
        email-field: email
    microsoft:
      client-id: your-microsoft-client-id
      client-secret: your-microsoft-client-secret
      redirect-uri: http://localhost:8080/api/oauth2/callback/microsoft
      authorize-url: https://login.microsoftonline.com/common/oauth2/v2.0/authorize
      token-url: https://login.microsoftonline.com/common/oauth2/v2.0/token
      user-info-url: https://graph.microsoft.com/v1.0/me
      scope: User.Read
      user-info-mapping:
        id-field: id
        username-field: displayName
        avatar-field: ""
        email-field: mail
```

---

## 前端改动

### 1. 更新 `api/oauth2.ts`

新增 `getAvailableProviders()` API。

### 2. 更新 `types/user.d.ts`

新增 `Oauth2ProviderInfo` 类型。

### 3. 重写 `Login.vue` 第三方登录区域

- 从后端 `/oauth2/providers` 动态获取可用提供方列表
- 根据返回数据动态渲染按钮（不再硬编码 GitHub/Google）
- 每个提供方显示名称 + 图标（通过 provider 名称映射图标）
- 提供方图标映射：github → GitHub SVG, google → Google SVG, wechat → 微信绿色图标, dingtalk → 钉钉蓝色图标, feishu → 飞书蓝色图标, gitee → Gitee 红色图标, microsoft → 微软四色图标

---

## 实施步骤

### 步骤 1：后端 — 新增 Oauth2UserInfo.java
统一第三方用户信息数据结构。

### 步骤 2：后端 — 新增 Oauth2Provider.java 接口
定义策略接口（getName, buildAuthorizeUrl, exchangeToken, fetchUserInfo）。

### 步骤 3：后端 — 新增 Oauth2ProviderConfig.java
从 yml 加载提供方配置的配置类。

### 步骤 4：后端 — 新增 AbstractOauth2Provider.java
通用基类，实现标准 OAuth2 授权码流程。

### 步骤 5：后端 — 新增 7 个提供方实现类
GithubProvider, GoogleProvider, WechatProvider, DingtalkProvider, FeishuProvider, GiteeProvider, MicrosoftProvider。

### 步骤 6：后端 — 新增 Oauth2ProviderRegistry.java
提供方注册中心，自动注册 + 查找。

### 步骤 7：后端 — 重写 Oauth2Service.java
移除 @Value 和 if-else，改为注入 Registry。

### 步骤 8：后端 — 更新 Oauth2Controller.java
新增 `/oauth2/providers` 接口。

### 步骤 9：后端 — 更新 application.yml
改为 `oauth2.providers` Map 结构，增加 5 个新提供方配置。

### 步骤 10：后端编译验证
`mvn compile`。

### 步骤 11：前端 — 更新类型和 API
更新 `types/user.d.ts`、`api/oauth2.ts`。

### 步骤 12：前端 — 重写 Login.vue
动态渲染第三方登录按钮。

### 步骤 13：前端编译验证
`vue-tsc --noEmit && vite build`。

---

## 关键设计决策

- **策略模式**：每个 OAuth2 提供方是一个独立策略，新增提供方只需添加一个实现类 + yml 配置，无需修改现有代码
- **配置驱动**：提供方的 URL、scope、字段映射全部在 yml 中配置，无需改代码即可调整
- **字段映射**：`userInfoMapping` 解决不同提供方 JSON 字段名不一致的问题（如 GitHub 用 `login`，微信用 `nickname`）
- **特殊提供方覆盖**：微信/钉钉/飞书的 token 交换流程与标准 OAuth2 有差异，通过子类覆盖 `exchangeToken()` 实现
- **前端动态渲染**：登录页从后端获取可用提供方列表，动态渲染按钮，后端未配置的提供方不会显示
