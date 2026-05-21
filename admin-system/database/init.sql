CREATE DATABASE IF NOT EXISTS admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admin_system;

CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    dept_name VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    sort INT DEFAULT 0 COMMENT '排序',
    leader VARCHAR(50) COMMENT '负责人',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(200) COMMENT '头像',
    dept_id BIGINT COMMENT '部门ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    menu_type TINYINT NOT NULL COMMENT '类型：1-目录，2-菜单，3-按钮',
    perms VARCHAR(100) COMMENT '权限标识',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    INDEX idx_user (user_id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    INDEX idx_role (role_id),
    INDEX idx_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

INSERT INTO sys_dept (id, dept_name, parent_id, sort, leader, status) VALUES
(1, '总公司', 0, 0, '管理员', 1),
(2, '研发部', 1, 1, '张三', 1),
(3, '市场部', 1, 2, '李四', 1),
(4, '财务部', 1, 3, '王五', 1),
(5, '前端组', 2, 1, '赵六', 1),
(6, '后端组', 2, 2, '钱七', 1);

INSERT INTO sys_user (username, password, nickname, email, phone, avatar, dept_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', '13800138000', '', 1, 1);

INSERT INTO sys_role (id, role_name, role_key, sort, status, remark) VALUES
(1, '超级管理员', 'admin', 1, 1, '拥有所有权限'),
(2, '普通用户', 'user', 2, 1, '普通用户权限');

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

INSERT INTO sys_menu (id, menu_name, parent_id, path, component, icon, sort, menu_type, perms, status) VALUES
(1, '首页', 0, '/dashboard', 'views/dashboard/Dashboard.vue', 'HomeFilled', 1, 2, '', 1),
(2, '系统管理', 0, '/system', '', 'Setting', 2, 1, '', 1),
(3, '用户管理', 2, '/system/user', 'views/system/user/UserList.vue', 'User', 1, 2, 'system:user:list', 1),
(4, '角色管理', 2, '/system/role', 'views/system/role/RoleList.vue', 'UserFilled', 2, 2, 'system:role:list', 1),
(5, '菜单管理', 2, '/system/menu', 'views/system/menu/MenuList.vue', 'Menu', 3, 2, 'system:menu:list', 1),
(6, '部门管理', 2, '/system/dept', 'views/system/dept/DeptList.vue', 'OfficeBuilding', 4, 2, 'system:dept:list', 1),
(7, '用户新增', 3, '', '', '', 1, 3, 'system:user:add', 1),
(8, '用户修改', 3, '', '', '', 2, 3, 'system:user:edit', 1),
(9, '用户删除', 3, '', '', '', 3, 3, 'system:user:remove', 1),
(10, '角色新增', 4, '', '', '', 1, 3, 'system:role:add', 1),
(11, '角色修改', 4, '', '', '', 2, 3, 'system:role:edit', 1),
(12, '角色删除', 4, '', '', '', 3, 3, 'system:role:remove', 1),
(13, '菜单新增', 5, '', '', '', 1, 3, 'system:menu:add', 1),
(14, '菜单修改', 5, '', '', '', 2, 3, 'system:menu:edit', 1),
(15, '菜单删除', 5, '', '', '', 3, 3, 'system:menu:remove', 1),
(16, '部门新增', 6, '', '', '', 1, 3, 'system:dept:add', 1),
(17, '部门修改', 6, '', '', '', 2, 3, 'system:dept:edit', 1),
(18, '部门删除', 6, '', '', '', 3, 3, 'system:dept:remove', 1);

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18),
(2, 1);

CREATE TABLE IF NOT EXISTS sys_oss_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    original_name VARCHAR(200) NOT NULL COMMENT '原始文件名',
    file_name VARCHAR(200) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件MIME类型',
    url VARCHAR(500) COMMENT '访问URL',
    upload_user_id BIGINT COMMENT '上传用户ID',
    upload_user_name VARCHAR(50) COMMENT '上传用户名',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS文件记录表';

CREATE TABLE IF NOT EXISTS sys_oauth2_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '本地用户ID',
    provider VARCHAR(20) NOT NULL COMMENT '提供方(github/google)',
    provider_id VARCHAR(100) NOT NULL COMMENT '第三方用户ID',
    provider_username VARCHAR(100) COMMENT '第三方用户名',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_provider_id (provider, provider_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2用户绑定表';

INSERT INTO sys_menu (id, menu_name, parent_id, path, component, icon, sort, menu_type, perms, status) VALUES
(19, '文件管理', 2, '/system/oss', 'views/system/oss/OssList.vue', 'Folder', 5, 2, 'system:oss:list', 1),
(20, '文件上传', 19, '', '', '', 1, 3, 'system:oss:upload', 1),
(21, '文件删除', 19, '', '', '', 2, 3, 'system:oss:remove', 1);

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 19), (1, 20), (1, 21);

CREATE TABLE IF NOT EXISTS mall_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    icon VARCHAR(200) COMMENT '分类图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS mall_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    main_image VARCHAR(500) COMMENT '主图URL',
    images TEXT COMMENT '图片列表(JSON)',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '销售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '总库存',
    sales INT DEFAULT 0 COMMENT '销量',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE IF NOT EXISTS mall_product_sku (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'SKU ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    name VARCHAR(100) COMMENT 'SKU名称',
    specs VARCHAR(500) COMMENT '规格属性(JSON)',
    price DECIMAL(10,2) NOT NULL COMMENT '销售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存',
    image VARCHAR(500) COMMENT 'SKU图片',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

CREATE TABLE IF NOT EXISTS mall_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(200) COMMENT '头像',
    level INT DEFAULT 1 COMMENT '会员等级',
    points INT DEFAULT 0 COMMENT '积分',
    balance DECIMAL(10,2) DEFAULT 0 COMMENT '余额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

CREATE TABLE IF NOT EXISTS mall_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    pay_time DATETIME COMMENT '支付时间',
    pay_type TINYINT COMMENT '支付方式：1-微信，2-支付宝',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已退款',
    receiver_name VARCHAR(50) COMMENT '收货人',
    receiver_phone VARCHAR(20) COMMENT '收货电话',
    receiver_address VARCHAR(200) COMMENT '收货地址',
    remark VARCHAR(200) COMMENT '订单备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_member (member_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS mall_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT COMMENT 'SKU ID',
    product_name VARCHAR(100) COMMENT '商品名称',
    sku_name VARCHAR(100) COMMENT 'SKU名称',
    image VARCHAR(500) COMMENT '商品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    total_price DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

CREATE TABLE IF NOT EXISTS mall_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
    name VARCHAR(50) NOT NULL COMMENT '优惠券名称',
    type TINYINT NOT NULL COMMENT '类型：1-满减，2-折扣，3-无门槛',
    discount DECIMAL(10,2) NOT NULL COMMENT '优惠金额/折扣率',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    total_count INT NOT NULL COMMENT '发放总量',
    remain_count INT NOT NULL COMMENT '剩余数量',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

INSERT INTO sys_menu (id, menu_name, parent_id, path, component, icon, sort, menu_type, perms, status) VALUES
(30, '商城管理', 0, '/mall', '', 'ShoppingCart', 3, 1, '', 1),
(31, '商品分类', 30, '/mall/category', 'views/mall/category/CategoryList.vue', 'Menu', 1, 2, 'mall:category:list', 1),
(32, '商品管理', 30, '/mall/product', 'views/mall/product/ProductList.vue', 'Goods', 2, 2, 'mall:product:list', 1),
(33, '会员管理', 30, '/mall/member', 'views/mall/member/MemberList.vue', 'User', 3, 2, 'mall:member:list', 1),
(34, '订单管理', 30, '/mall/order', 'views/mall/order/OrderList.vue', 'List', 4, 2, 'mall:order:list', 1),
(35, '优惠券管理', 30, '/mall/coupon', 'views/mall/coupon/CouponList.vue', 'Ticket', 5, 2, 'mall:coupon:list', 1),
(36, '分类新增', 31, '', '', '', 1, 3, 'mall:category:add', 1),
(37, '分类修改', 31, '', '', '', 2, 3, 'mall:category:edit', 1),
(38, '分类删除', 31, '', '', '', 3, 3, 'mall:category:remove', 1),
(39, '商品新增', 32, '', '', '', 1, 3, 'mall:product:add', 1),
(40, '商品修改', 32, '', '', '', 2, 3, 'mall:product:edit', 1),
(41, '商品删除', 32, '', '', '', 3, 3, 'mall:product:remove', 1),
(42, '会员新增', 33, '', '', '', 1, 3, 'mall:member:add', 1),
(43, '会员修改', 33, '', '', '', 2, 3, 'mall:member:edit', 1),
(44, '会员删除', 33, '', '', '', 3, 3, 'mall:member:remove', 1),
(45, '订单发货', 34, '', '', '', 1, 3, 'mall:order:ship', 1),
(46, '订单取消', 34, '', '', '', 2, 3, 'mall:order:cancel', 1),
(47, '订单退款', 34, '', '', '', 3, 3, 'mall:order:refund', 1),
(48, '优惠券新增', 35, '', '', '', 1, 3, 'mall:coupon:add', 1),
(49, '优惠券修改', 35, '', '', '', 2, 3, 'mall:coupon:edit', 1),
(50, '优惠券删除', 35, '', '', '', 3, 3, 'mall:coupon:remove', 1);

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 30), (1, 31), (1, 32), (1, 33), (1, 34), (1, 35),
(1, 36), (1, 37), (1, 38), (1, 39), (1, 40), (1, 41),
(1, 42), (1, 43), (1, 44), (1, 45), (1, 46), (1, 47),
(1, 48), (1, 49), (1, 50);

COMMIT;
