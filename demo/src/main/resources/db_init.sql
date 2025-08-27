-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(100)
);

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(100)
);

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission(id) ON DELETE CASCADE
);

-- 初始化角色
INSERT INTO role (role_name, description) VALUES
('ADMIN', '管理员'),
('USER', '普通用户')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 初始化权限
INSERT INTO permission (permission_name, description) VALUES
('user:view', '查看用户'),
('user:edit', '编辑用户'),
('role:view', '查看角色'),
('role:edit', '编辑角色')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 初始化用户（密码需加密后再插入，以下为明文示例）
INSERT INTO user (username, password, enabled) VALUES
('admin', 'admin123', TRUE)
ON DUPLICATE KEY UPDATE password=VALUES(password);

-- 绑定admin用户为ADMIN角色
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM user u, role r WHERE u.username='admin' AND r.role_name='ADMIN'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 绑定ADMIN角色所有权限
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p WHERE r.role_name='ADMIN'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 菜单表
CREATE TABLE IF NOT EXISTS menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT NULL,
    menu_name VARCHAR(50) NOT NULL,
    path VARCHAR(100),
    perms VARCHAR(100),
    type VARCHAR(20), -- 目录/菜单/按钮
    icon VARCHAR(50),
    order_num INT DEFAULT 0,
    visible BOOLEAN DEFAULT TRUE
);

-- 角色-菜单关联表
CREATE TABLE IF NOT EXISTS role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menu(id) ON DELETE CASCADE
);

-- 初始化菜单
INSERT INTO menu (parent_id, menu_name, path, perms, type, icon, order_num, visible) VALUES
(NULL, '系统管理', '/system', NULL, '目录', 'setting', 1, TRUE),
(1, '用户管理', '/system/user', 'user:view', '菜单', 'user', 1, TRUE),
(1, '角色管理', '/system/role', 'role:view', '菜单', 'role', 2, TRUE),
(1, '菜单管理', '/system/menu', 'menu:view', '菜单', 'menu', 3, TRUE)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name);

-- 绑定ADMIN角色所有菜单
INSERT INTO role_menu (role_id, menu_id)
SELECT r.id, m.id FROM role r, menu m WHERE r.role_name='ADMIN'
ON DUPLICATE KEY UPDATE role_id=role_id;
