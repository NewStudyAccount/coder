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

COMMIT;
