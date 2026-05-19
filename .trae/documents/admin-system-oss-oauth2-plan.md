# admin-system 增加 OSS 对象存储 + OAuth2 认证计划

## 概述

在现有 `admin-system` 项目中增加两个功能模块：
1. **OSS 对象存储** — 支持文件上传/下载/删除/列表，本地文件系统实现 + 可扩展 MinIO/S3
2. **OAuth2 认证** — 支持 GitHub/Google 第三方登录，基于授权码流程

参考 demo 项目中已有的 `OssService`/`OssController` 和 `OidcServer` 实现模式，但适配到 admin-system 的架构风格（统一 Result 响应、JWT 认证、MyBatis + MySQL）。

---

## 一、OSS 对象存储

### 后端新增文件

| 文件 | 说明 |
|------|------|
| `entity/OssFile.java` | 文件记录实体（id, original_name, file_name, file_path, file_size, file_type, url, upload_user, create_time） |
| `mapper/OssFileMapper.java` | 文件记录 Mapper 接口 |
| `mapper/OssFileMapper.xml` | 文件记录 Mapper XML |
| `service/OssFileService.java` | OSS 服务（上传/下载/删除/列表，本地文件系统实现） |
| `controller/OssController.java` | OSS 接口（/oss/upload, /oss/list, /oss/download/{id}, /oss/delete/{id}） |

### 后端修改文件

| 文件 | 修改内容 |
|------|----------|
| `application.yml` | 增加 oss.storage-path 配置 |
| `WebMvcConfig.java` | 排除 /oss/download/** 路径（公开下载不需要认证） |
| `database/init.sql` | 增加 sys_oss_file 表 |

### 前端新增文件

| 文件 | 说明 |
|------|------|
| `types/oss.d.ts` | OSS 文件类型定义 |
| `api/oss.ts` | OSS API 接口 |
| `views/system/oss/OssList.vue` | 文件管理页面（上传 + 列表 + 删除 + 预览/下载） |

### 前端修改文件

| 文件 | 修改内容 |
|------|----------|
| `router/index.ts` | 增加 /system/oss 路由 |
| `layout/MainLayout.vue` | 侧边栏增加"文件管理"菜单项 |

---

## 二、OAuth2 第三方登录

### 设计思路

采用 **OAuth2 授权码流程**，支持 GitHub 和 Google 第三方登录：
- 前端点击"GitHub登录"/"Google登录"按钮 → 跳转到第三方授权页
- 用户授权后 → 第三方回调到后端 → 后端用 code 换取 access_token → 获取用户信息
- 后端查找或创建本地用户 → 签发 JWT → 重定向到前端并携带 token
- 前端从 URL 参数中提取 token → 存入 store → 完成登录

### 后端新增文件

| 文件 | 说明 |
|------|------|
| `entity/Oauth2User.java` | OAuth2 绑定用户实体（id, user_id, provider, provider_id, provider_username, avatar_url, email, create_time, update_time） |
| `mapper/Oauth2UserMapper.java` | OAuth2 用户 Mapper 接口 |
| `mapper/Oauth2UserMapper.xml` | OAuth2 用户 Mapper XML |
| `service/Oauth2Service.java` | OAuth2 服务（构建授权URL、处理回调、查找/创建/绑定用户） |
| `controller/Oauth2Controller.java` | OAuth2 接口（/oauth2/authorize/{provider}, /oauth2/callback/{provider}, /oauth2/bind/{provider}） |

### 后端修改文件

| 文件 | 修改内容 |
|------|----------|
| `pom.xml` | 增加 okhttp 依赖（用于 OAuth2 HTTP 请求） |
| `application.yml` | 增加 oauth2.github / oauth2.google 配置（client-id, client-secret, redirect-uri） |
| `WebMvcConfig.java` | 排除 /oauth2/callback/** 路径（OAuth2 回调不需要 JWT 认证） |
| `database/init.sql` | 增加 sys_oauth2_user 表 |
| `AuthService.java` | 增加 oauth2Login 方法 |

### 前端新增文件

| 文件 | 说明 |
|------|------|
| `api/oauth2.ts` | OAuth2 API 接口 |
| `views/login/Oauth2Callback.vue` | OAuth2 回调页面（从 URL 提取 token 并完成登录） |

### 前端修改文件

| 文件 | 修改内容 |
|------|----------|
| `types/user.d.ts` | 增加 Oauth2Provider 类型 |
| `api/auth.ts` | 增加 getOauth2AuthorizeUrl 方法 |
| `router/index.ts` | 增加 /oauth2/callback 路由 |
| `views/login/Login.vue` | 增加 GitHub/Google 第三方登录按钮 |

---

## 三、数据库变更

### 新增表：sys_oss_file

```sql
CREATE TABLE IF NOT EXISTS sys_oss_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(200) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(200) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件MIME类型',
    url VARCHAR(500) COMMENT '访问URL',
    upload_user_id BIGINT COMMENT '上传用户ID',
    upload_user_name VARCHAR(50) COMMENT '上传用户名',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS文件记录表';
```

### 新增表：sys_oauth2_user

```sql
CREATE TABLE IF NOT EXISTS sys_oauth2_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '本地用户ID',
    provider VARCHAR(20) NOT NULL COMMENT '提供方(github/google)',
    provider_id VARCHAR(100) NOT NULL COMMENT '第三方用户ID',
    provider_username VARCHAR(100) COMMENT '第三方用户名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_provider_id (provider, provider_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2用户绑定表';
```

---

## 四、实施步骤

### 步骤 1：数据库 — 更新 init.sql
在 `database/init.sql` 末尾追加 `sys_oss_file` 和 `sys_oauth2_user` 建表语句。

### 步骤 2：后端 — 更新 pom.xml
增加 `com.squareup.okhttp3:okhttp:4.12.0` 依赖（用于 OAuth2 HTTP 请求）。

### 步骤 3：后端 — 更新 application.yml
增加 `oss.storage-path` 和 `oauth2.github` / `oauth2.google` 配置。

### 步骤 4：后端 — OSS 实体和 Mapper
创建 `OssFile.java`、`OssFileMapper.java`、`OssFileMapper.xml`。

### 步骤 5：后端 — OSS Service 和 Controller
创建 `OssFileService.java`（本地文件系统实现）、`OssController.java`。

### 步骤 6：后端 — OAuth2 实体和 Mapper
创建 `Oauth2User.java`、`Oauth2UserMapper.java`、`Oauth2UserMapper.xml`。

### 步骤 7：后端 — OAuth2 Service 和 Controller
创建 `Oauth2Service.java`（构建授权URL、处理回调、查找/创建用户）、`Oauth2Controller.java`。

### 步骤 8：后端 — 修改现有文件
- `WebMvcConfig.java` — 排除 /oss/download/** 和 /oauth2/callback/**
- `AuthService.java` — 增加 oauth2Login 方法

### 步骤 9：后端编译验证
执行 `mvn compile` 确保无编译错误。

### 步骤 10：前端 — 类型定义
创建 `types/oss.d.ts`，更新 `types/user.d.ts`。

### 步骤 11：前端 — API 模块
创建 `api/oss.ts`、`api/oauth2.ts`，更新 `api/auth.ts`。

### 步骤 12：前端 — OSS 页面
创建 `views/system/oss/OssList.vue`（上传 + 列表 + 删除 + 预览）。

### 步骤 13：前端 — OAuth2 页面
创建 `views/login/Oauth2Callback.vue`，更新 `views/login/Login.vue`（增加第三方登录按钮）。

### 步骤 14：前端 — 路由和布局
更新 `router/index.ts`（增加 /system/oss 和 /oauth2/callback 路由），更新 `layout/MainLayout.vue`（增加文件管理菜单项）。

### 步骤 15：前端编译验证
执行 `vue-tsc --noEmit && vite build` 确保无错误。

---

## 五、关键设计决策

- **OSS 采用本地文件系统实现**：参考 demo 项目的 `OssService` 模式，文件元信息存 MySQL，文件本体存本地磁盘，通过 API 可扩展为 MinIO/S3
- **OAuth2 不依赖 Spring Security**：纯手写授权码流程，参考 demo 项目的 `OidcServer` 模式，使用 OkHttp 发起 HTTP 请求
- **OAuth2 回调流程**：后端 /oauth2/callback/{provider} 处理回调后，签发 JWT 并重定向到前端 /oauth2/callback?token=xxx 页面
- **OAuth2 用户自动注册**：首次第三方登录时自动创建本地用户并绑定，后续登录直接关联
- **JWT 拦截器排除**：/oss/download/** 和 /oauth2/callback/** 不需要 JWT 认证
