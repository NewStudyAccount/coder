# Vue3 前端框架项目

基于 Vue3 + Vue Router + Pinia + Element-Plus 构建的现代化前端框架项目，包含完整的 OpenAPI 规范文档。

## 项目概述

这是一个企业级的 Vue3 前端框架模板项目，提供了：

- 🚀 完整的项目架构和目录结构
- 📝 符合 OpenAPI 3.0.3 规范的 API 文档
- 🔐 JWT 认证机制
- 👥 用户管理系统
- 🎨 Element-Plus UI 组件库
- 📦 Pinia 状态管理
- 🛣️ Vue Router 路由管理

## 技术栈

- **核心框架**: Vue 3.x (Composition API)
- **构建工具**: Vite 5.x
- **状态管理**: Pinia
- **路由管理**: Vue Router 4.x
- **UI 组件库**: Element-Plus
- **HTTP 客户端**: Axios
- **CSS 预处理器**: SCSS
- **代码规范**: ESLint + Prettier
- **API 规范**: OpenAPI 3.0.3

## 项目结构

```
vue3-frontend-framework/
├── openapi/                    # OpenAPI 规范文档目录
│   ├── openapi.yaml           # OpenAPI 主规范文件
│   ├── README.md              # OpenAPI 文档使用说明
│   ├── examples/              # API 请求/响应示例
│   │   ├── auth-examples.json
│   │   ├── user-examples.json
│   │   └── config-examples.json
│   └── schemas/               # 数据模型定义
│       ├── common.yaml        # 通用数据模型
│       ├── auth.yaml          # 认证相关模型
│       └── user.yaml          # 用户相关模型
├── src/                       # 源代码目录
│   ├── api/                   # API 接口封装
│   ├── assets/                # 静态资源
│   ├── components/            # 公共组件
│   ├── composables/           # 组合式函数
│   ├── layouts/               # 布局组件
│   ├── router/                # 路由配置
│   ├── stores/                # Pinia 状态管理
│   ├── utils/                 # 工具函数
│   ├── views/                 # 页面组件
│   ├── App.vue                # 根组件
│   └── main.js                # 入口文件
├── public/                    # 公共静态资源
├── .env.development           # 开发环境配置
├── .env.production            # 生产环境配置
├── index.html                 # HTML 模板
├── package.json               # 项目配置文件
├── vite.config.js             # Vite 配置
└── README.md                  # 项目说明文档
```

## OpenAPI 文档

本项目提供完整的 OpenAPI 3.0.3 规范文档，位于 `openapi/` 目录。

### API 模块

- **认证模块** (`/api/auth/*`)
  - 用户登录
  - 用户注册
  - 刷新令牌
  - 用户登出
  - 修改密码
  - 重置密码

- **用户管理** (`/api/users/*`)
  - 获取用户信息
  - 更新用户资料
  - 用户列表查询
  - 删除用户
  - 用户统计信息
  - 用户活动日志

- **系统配置** (`/api/config/*`)
  - 获取系统配置
  - 更新系统配置
  - 用户偏好设置

### 查看 API 文档

有多种方式查看和使用 OpenAPI 文档：

#### 1. 使用 Swagger UI（推荐）

```bash
# 安装 Swagger UI
npm install -g swagger-ui-watcher

# 启动 Swagger UI
swagger-ui-watcher openapi/openapi.yaml
```

访问 http://localhost:8080 查看交互式 API 文档。

#### 2. 使用在线编辑器

将 `openapi/openapi.yaml` 文件内容复制到以下任一在线编辑器：

- [Swagger Editor](https://editor.swagger.io/)
- [Stoplight Studio](https://stoplight.io/studio)

#### 3. VS Code 扩展

安装以下 VS Code 扩展以获得更好的编辑体验：

- OpenAPI (Swagger) Editor
- Swagger Viewer

详细使用说明请参阅 [OpenAPI 文档目录的 README](./openapi/README.md)。

## 快速开始

### 环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0 或 pnpm >= 8.0.0

### 安装依赖

```bash
# 使用 npm
npm install

# 或使用 pnpm
pnpm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:5173 查看应用。

### 生产构建

```bash
npm run build
```

构建产物将输出到 `dist/` 目录。

### 预览生产构建

```bash
npm run preview
```

## 核心功能

### 1. 认证系统

- JWT Token 认证
- 自动刷新令牌
- 登录状态持久化
- 权限路由守卫

### 2. 用户管理

- 用户信息管理
- 角色权限管理
- 用户资料编辑
- 用户偏好设置

### 3. 状态管理

使用 Pinia 进行全局状态管理：

- 用户状态 (stores/user.js)
- 系统配置 (stores/config.js)
- 应用设置 (stores/app.js)

### 4. 路由管理

- 动态路由加载
- 路由权限控制
- 路由懒加载
- 页面过渡动画

### 5. HTTP 请求

- Axios 封装
- 请求/响应拦截
- 错误统一处理
- Loading 状态管理

## 开发指南

### 代码规范

项目使用 ESLint 和 Prettier 进行代码规范检查：

```bash
# 代码检查
npm run lint

# 代码格式化
npm run format
```

### 提交规范

使用约定式提交规范（Conventional Commits）：

- `feat`: 新功能
- `fix`: 修复问题
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具相关

### API 开发流程

1. 在 `openapi/openapi.yaml` 中定义 API 接口
2. 在 `src/api/` 中封装 API 调用
3. 在组件中使用 API 接口
4. 更新相关文档和示例

### 添加新页面

1. 在 `src/views/` 创建页面组件
2. 在 `src/router/` 添加路由配置
3. 如需权限控制，配置路由 meta 信息
4. 更新导航菜单（如需要）

## 环境配置

### 开发环境 (.env.development)

```env
VITE_APP_TITLE=Vue3 前端框架
VITE_API_BASE_URL=http://localhost:3000/api
VITE_ENABLE_MOCK=true
```

### 生产环境 (.env.production)

```env
VITE_APP_TITLE=Vue3 前端框架
VITE_API_BASE_URL=https://api.example.com
VITE_ENABLE_MOCK=false
```

## 测试

```bash
# 单元测试
npm run test:unit

# E2E 测试
npm run test:e2e

# 测试覆盖率
npm run test:coverage
```

## 部署

### Docker 部署

```bash
# 构建镜像
docker build -t vue3-frontend-framework .

# 运行容器
docker run -p 80:80 vue3-frontend-framework
```

### Nginx 部署

1. 执行生产构建: `npm run build`
2. 将 `dist/` 目录内容复制到 Nginx 服务器
3. 配置 Nginx:

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend-server:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 性能优化

- ✅ 路由懒加载
- ✅ 组件按需导入
- ✅ 图片懒加载
- ✅ Gzip 压缩
- ✅ 浏览器缓存策略
- ✅ CDN 加速

## 浏览器支持

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 常见问题

### 1. 如何自定义主题？

参考 Element-Plus 的主题定制文档，在 `src/styles/` 目录中覆盖 CSS 变量。

### 2. 如何添加新的 API 接口？

1. 更新 `openapi/openapi.yaml`
2. 在 `src/api/` 中添加对应的接口方法
3. 更新 TypeScript 类型定义（如使用 TS）

### 3. 如何配置代理？

在 `vite.config.js` 中配置开发服务器代理：

```javascript
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true
      }
    }
  }
}
```

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证

[MIT License](LICENSE)

## 联系方式

- 作者: Your Name
- Email: your.email@example.com
- 项目地址: https://github.com/yourusername/vue3-frontend-framework

## 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本更新历史。

---

⭐ 如果这个项目对你有帮助，请给一个 Star！
