# Vue3前端框架 OpenAPI 规范文档

本目录包含基于Vue3 + Vue Router + Pinia + Element-UI的前端框架的API规范文档。

## 目录结构

```
openapi/
├── openapi.yaml          # OpenAPI 3.0规范主文件
├── README.md            # 本文件
├── schemas/             # 数据模型定义
│   ├── auth.yaml       # 认证相关模型
│   ├── user.yaml       # 用户相关模型
│   └── common.yaml     # 通用模型
└── examples/            # 请求/响应示例
    ├── auth-examples.json
    ├── user-examples.json
    └── config-examples.json
```

## 文件说明

### openapi.yaml
主要的OpenAPI规范文件，定义了所有的API端点、请求参数、响应格式和数据模型。包含以下主要部分：

- **认证接口**: 登录、注册、登出、令牌刷新
- **用户管理**: 用户信息查询、更新、列表管理
- **系统配置**: 系统设置的获取和更新

## 使用方法

### 1. 查看API文档

#### 使用Swagger UI
```bash
# 安装swagger-ui-express（如果使用Node.js后端）
npm install swagger-ui-express

# 在你的后端服务中引入
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const swaggerDocument = YAML.load('./openapi/openapi.yaml');

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
```

#### 使用在线工具
1. 访问 [Swagger Editor](https://editor.swagger.io/)
2. 将`openapi.yaml`文件内容复制粘贴到编辑器
3. 即可查看格式化的API文档

### 2. 生成API客户端代码

#### 使用OpenAPI Generator
```bash
# 安装OpenAPI Generator
npm install @openapitools/openapi-generator-cli -g

# 生成TypeScript axios客户端
openapi-generator-cli generate \
  -i openapi/openapi.yaml \
  -g typescript-axios \
  -o src/api/generated

# 生成JavaScript客户端
openapi-generator-cli generate \
  -i openapi/openapi.yaml \
  -g javascript \
  -o src/api/generated
```

### 3. Mock服务器

#### 使用Prism创建Mock服务器
```bash
# 安装Prism
npm install -g @stoplight/prism-cli

# 启动Mock服务器
prism mock openapi/openapi.yaml

# Mock服务器将运行在 http://localhost:4010
```

### 4. API验证

```bash
# 使用Spectral进行规范验证
npm install -g @stoplight/spectral-cli

# 验证OpenAPI文档
spectral lint openapi/openapi.yaml
```

## API概览

### 认证相关接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /auth/login | 用户登录 |
| POST | /auth/register | 用户注册 |
| POST | /auth/logout | 用户登出 |
| POST | /auth/refresh | 刷新令牌 |

### 用户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /users/profile | 获取当前用户信息 |
| PUT | /users/profile | 更新用户信息 |
| GET | /users | 获取用户列表 |
| GET | /users/{userId} | 获取指定用户信息 |
| DELETE | /users/{userId} | 删除用户 |

### 系统配置接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /config/settings | 获取系统配置 |
| PUT | /config/settings | 更新系统配置 |

## 认证机制

本API使用JWT(JSON Web Token)进行身份认证：

1. 用户通过`/auth/login`接口登录，获取访问令牌(`token`)和刷新令牌(`refreshToken`)
2. 后续请求需在HTTP Header中携带访问令牌：
   ```
   Authorization: Bearer <your_access_token>
   ```
3. 当访问令牌过期时，使用`/auth/refresh`接口通过刷新令牌获取新的访问令牌

## 响应格式

所有API响应遵循统一的格式：

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 实际数据
  }
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "请求参数错误",
  "error": "username字段不能为空"
}
```

## 常见HTTP状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/认证失败 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 409 | 资源冲突 |
| 500 | 服务器内部错误 |

## 在Vue3项目中使用

### 1. 创建API服务模块

```javascript
// src/api/auth.js
import request from '@/utils/request'

export const authAPI = {
  // 登录
  login(data) {
    return request.post('/auth/login', data)
  },
  
  // 注册
  register(data) {
    return request.post('/auth/register', data)
  },
  
  // 登出
  logout() {
    return request.post('/auth/logout')
  },
  
  // 刷新令牌
  refreshToken(refreshToken) {
    return request.post('/auth/refresh', { refreshToken })
  }
}
```

### 2. 在Pinia Store中使用

```javascript
// src/stores/user.js
import { defineStore } from 'pinia'
import { authAPI } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  
  actions: {
    async login(credentials) {
      const response = await authAPI.login(credentials)
      this.token = response.data.token
      this.userInfo = response.data.user
      localStorage.setItem('token', response.data.token)
      return response
    },
    
    async logout() {
      await authAPI.logout()
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})
```

## 扩展API规范

如需添加新的API端点：

1. 在`openapi.yaml`的`paths`部分添加新路径
2. 定义请求参数和响应格式
3. 在`components/schemas`中添加新的数据模型
4. 更新本README文档

## 相关资源

- [OpenAPI 规范](https://swagger.io/specification/)
- [Swagger Editor](https://editor.swagger.io/)
- [OpenAPI Generator](https://openapi-generator.tech/)
- [Prism Mock Server](https://stoplight.io/open-source/prism)
- [Vue3 文档](https://vuejs.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)

## 版本历史

- v1.0.0 (2024-01-01): 初始版本，包含基础认证和用户管理接口

## 联系方式

如有问题或建议，请联系：
- Email: support@example.com
- 项目仓库: [GitHub](https://github.com/yourproject)
