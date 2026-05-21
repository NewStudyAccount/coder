# 商城后台系统增加计划

## 项目现状分析

现有项目是一个 Spring Boot 2.7 + Vue 3 + Element Plus 的后台管理系统，包含：
- **后端**: Spring Boot 2.7.18, MyBatis, MySQL, JWT 认证, Lombok, Hutool
- **前端**: Vue 3, Element Plus, Pinia, Vue Router, Axios, TypeScript, Vite
- **数据库**: MySQL，系统表使用 `sys_` 前缀
- **代码模式**: Entity → Mapper(接口+XML) → Service → Controller，前端 api → types → views

## 商城模块设计

商城模块数据库表统一使用 `mall_` 前缀，后端代码放在 `com.admin` 包下对应目录，前端代码按功能模块组织。

---

## 一、数据库层（init.sql 追加）

### 1. 商品分类表 `mall_category`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 分类ID |
| name | VARCHAR(50) | 分类名称 |
| parent_id | BIGINT DEFAULT 0 | 父分类ID |
| icon | VARCHAR(200) | 分类图标 |
| sort | INT DEFAULT 0 | 排序 |
| status | TINYINT DEFAULT 1 | 状态：1-启用 0-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 2. 商品表 `mall_product`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 商品ID |
| name | VARCHAR(100) | 商品名称 |
| category_id | BIGINT | 分类ID |
| main_image | VARCHAR(500) | 主图URL |
| images | TEXT | 图片列表(JSON) |
| description | TEXT | 商品描述 |
| price | DECIMAL(10,2) | 销售价 |
| original_price | DECIMAL(10,2) | 原价 |
| stock | INT DEFAULT 0 | 总库存 |
| sales | INT DEFAULT 0 | 销量 |
| status | TINYINT DEFAULT 1 | 状态：0-下架 1-上架 |
| sort | INT DEFAULT 0 | 排序 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 3. 商品SKU表 `mall_product_sku`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | SKU ID |
| product_id | BIGINT | 商品ID |
| name | VARCHAR(100) | SKU名称 |
| specs | VARCHAR(500) | 规格属性(JSON) |
| price | DECIMAL(10,2) | 销售价 |
| original_price | DECIMAL(10,2) | 原价 |
| stock | INT DEFAULT 0 | 库存 |
| image | VARCHAR(500) | SKU图片 |
| status | TINYINT DEFAULT 1 | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 4. 会员表 `mall_member`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 会员ID |
| username | VARCHAR(50) UNIQUE | 用户名 |
| nickname | VARCHAR(50) | 昵称 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| avatar | VARCHAR(200) | 头像 |
| level | INT DEFAULT 1 | 会员等级 |
| points | INT DEFAULT 0 | 积分 |
| balance | DECIMAL(10,2) DEFAULT 0 | 余额 |
| status | TINYINT DEFAULT 1 | 状态：1-启用 0-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 5. 订单表 `mall_order`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 订单ID |
| order_no | VARCHAR(32) UNIQUE | 订单编号 |
| member_id | BIGINT | 会员ID |
| total_amount | DECIMAL(10,2) | 订单总金额 |
| pay_amount | DECIMAL(10,2) | 实付金额 |
| pay_time | DATETIME | 支付时间 |
| pay_type | TINYINT | 支付方式：1-微信 2-支付宝 |
| status | TINYINT DEFAULT 0 | 状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消 5-已退款 |
| receiver_name | VARCHAR(50) | 收货人 |
| receiver_phone | VARCHAR(20) | 收货电话 |
| receiver_address | VARCHAR(200) | 收货地址 |
| remark | VARCHAR(200) | 订单备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 6. 订单明细表 `mall_order_item`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 明细ID |
| order_id | BIGINT | 订单ID |
| product_id | BIGINT | 商品ID |
| sku_id | BIGINT | SKU ID |
| product_name | VARCHAR(100) | 商品名称 |
| sku_name | VARCHAR(100) | SKU名称 |
| image | VARCHAR(500) | 商品图片 |
| price | DECIMAL(10,2) | 单价 |
| quantity | INT | 数量 |
| total_price | DECIMAL(10,2) | 小计金额 |

### 7. 优惠券表 `mall_coupon`
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 优惠券ID |
| name | VARCHAR(50) | 优惠券名称 |
| type | TINYINT | 类型：1-满减 2-折扣 3-无门槛 |
| discount | DECIMAL(10,2) | 优惠金额/折扣率 |
| min_amount | DECIMAL(10,2) | 最低消费金额 |
| total_count | INT | 发放总量 |
| remain_count | INT | 剩余数量 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| status | TINYINT DEFAULT 1 | 状态：1-启用 0-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 8. 菜单数据插入
向 `sys_menu` 插入商城管理的一级菜单和子菜单，向 `sys_role_menu` 给超级管理员分配商城菜单权限。

---

## 二、后端实现（7个模块）

每个模块遵循现有模式：Entity → Mapper → Service → Controller

### 模块1: 商品分类 (Category)
- `com.admin.entity.Category`
- `com.admin.mapper.CategoryMapper` + `CategoryMapper.xml`
- `com.admin.service.CategoryService`
- `com.admin.controller.CategoryController`
- API: CRUD + 树形列表

### 模块2: 商品管理 (Product)
- `com.admin.entity.Product`
- `com.admin.mapper.ProductMapper` + `ProductMapper.xml`
- `com.admin.service.ProductService`
- `com.admin.controller.ProductController`
- API: CRUD + 上下架 + 按分类/状态查询

### 模块3: 商品SKU (ProductSku)
- `com.admin.entity.ProductSku`
- `com.admin.mapper.ProductSkuMapper` + `ProductSkuMapper.xml`
- `com.admin.service.ProductSkuService`
- SKU 作为商品的子资源，在 ProductController 中提供 SKU 管理接口

### 模块4: 会员管理 (Member)
- `com.admin.entity.Member`
- `com.admin.mapper.MemberMapper` + `MemberMapper.xml`
- `com.admin.service.MemberService`
- `com.admin.controller.MemberController`
- API: CRUD + 调整余额/积分 + 重置密码

### 模块5: 订单管理 (Order)
- `com.admin.entity.Order` + `OrderItem`
- `com.admin.mapper.OrderMapper` + `OrderItemMapper` + XML
- `com.admin.service.OrderService`
- `com.admin.controller.OrderController`
- API: 列表查询 + 详情 + 发货 + 取消 + 退款

### 模块6: 优惠券管理 (Coupon)
- `com.admin.entity.Coupon`
- `com.admin.mapper.CouponMapper` + `CouponMapper.xml`
- `com.admin.service.CouponService`
- `com.admin.controller.CouponController`
- API: CRUD + 启用/禁用

---

## 三、前端实现

### 1. 类型定义文件
- `src/types/category.d.ts` - 分类类型
- `src/types/product.d.ts` - 商品类型
- `src/types/member.d.ts` - 会员类型
- `src/types/order.d.ts` - 订单类型
- `src/types/coupon.d.ts` - 优惠券类型

### 2. API 模块
- `src/api/category.ts` - 分类API
- `src/api/product.ts` - 商品API
- `src/api/member.ts` - 会员API
- `src/api/order.ts` - 订单API
- `src/api/coupon.ts` - 优惠券API

### 3. 视图组件
- `src/views/mall/category/CategoryList.vue` - 分类管理（树形表格）
- `src/views/mall/product/ProductList.vue` - 商品列表
- `src/views/mall/product/ProductEdit.vue` - 商品新增/编辑（含SKU管理）
- `src/views/mall/member/MemberList.vue` - 会员管理
- `src/views/mall/order/OrderList.vue` - 订单列表
- `src/views/mall/order/OrderDetail.vue` - 订单详情
- `src/views/mall/coupon/CouponList.vue` - 优惠券管理

### 4. 路由配置
在 `src/router/index.ts` 中添加商城管理相关路由

### 5. 侧边栏菜单
在 `src/layout/MainLayout.vue` 中添加"商城管理"菜单组

---

## 四、实施步骤（按顺序执行）

### 步骤1: 数据库脚本
- 在 `database/init.sql` 末尾追加8张商城表 + 菜单数据 + 角色菜单关联

### 步骤2: 后端 - Entity 类
- 创建 `Category.java`, `Product.java`, `ProductSku.java`, `Member.java`, `Order.java`, `OrderItem.java`, `Coupon.java`

### 步骤3: 后端 - Mapper 层
- 创建7个 Mapper 接口 + 7个 Mapper XML

### 步骤4: 后端 - Service 层
- 创建6个 Service 类

### 步骤5: 后端 - Controller 层
- 创建5个 Controller 类（ProductController 包含 SKU 管理）

### 步骤6: 前端 - 类型定义
- 创建5个 `.d.ts` 类型文件

### 步骤7: 前端 - API 模块
- 创建5个 API 文件

### 步骤8: 前端 - 视图组件
- 创建7个 Vue 组件

### 步骤9: 前端 - 路由与菜单
- 更新 `router/index.ts` 添加商城路由
- 更新 `MainLayout.vue` 添加商城菜单

### 步骤10: 验证
- 编译后端项目确认无错误
- 编译前端项目确认无错误
