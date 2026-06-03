CREATE DATABASE IF NOT EXISTS admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE admin_system;

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    parent_id   BIGINT       DEFAULT 0     COMMENT '父部门ID',
    ancestors   VARCHAR(200) DEFAULT ''    COMMENT '祖级列表',
    dept_name   VARCHAR(50)  NOT NULL      COMMENT '部门名称',
    sort        INT          DEFAULT 0     COMMENT '排序',
    leader      VARCHAR(50)  DEFAULT ''    COMMENT '负责人',
    phone       VARCHAR(20)  DEFAULT ''    COMMENT '联系电话',
    email       VARCHAR(50)  DEFAULT ''    COMMENT '邮箱',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag    TINYINT      DEFAULT 0     COMMENT '删除标志：0-存在，1-删除',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 岗位表
-- ----------------------------
DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    post_code   VARCHAR(50)  NOT NULL      COMMENT '岗位编码',
    post_name   VARCHAR(50)  NOT NULL      COMMENT '岗位名称',
    sort        INT          DEFAULT 0     COMMENT '排序',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    remark      VARCHAR(200) DEFAULT ''    COMMENT '备注',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_post_code (post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    dept_id     BIGINT       DEFAULT NULL  COMMENT '部门ID',
    username    VARCHAR(50)  NOT NULL      COMMENT '用户名',
    nickname    VARCHAR(50)  DEFAULT ''    COMMENT '昵称',
    password    VARCHAR(100) NOT NULL      COMMENT '密码(BCrypt)',
    email       VARCHAR(50)  DEFAULT ''    COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT ''    COMMENT '手机号',
    gender      TINYINT      DEFAULT 0     COMMENT '性别：0-未知，1-男，2-女',
    avatar      VARCHAR(200) DEFAULT ''    COMMENT '头像',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    del_flag    TINYINT      DEFAULT 0     COMMENT '删除标志：0-存在，1-删除',
    login_ip    VARCHAR(50)  DEFAULT ''    COMMENT '最后登录IP',
    login_date  DATETIME     DEFAULT NULL  COMMENT '最后登录时间',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200) DEFAULT ''    COMMENT '备注',
    UNIQUE KEY uk_username (username),
    INDEX idx_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name   VARCHAR(50)  NOT NULL      COMMENT '角色名称',
    role_key    VARCHAR(50)  NOT NULL      COMMENT '角色标识',
    role_sort   INT          DEFAULT 0     COMMENT '排序',
    data_scope  TINYINT      DEFAULT 1     COMMENT '数据范围：1-全部数据，2-自定义，3-本部门，4-本部门及以下，5-仅本人',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    del_flag    TINYINT      DEFAULT 0     COMMENT '删除标志：0-存在，1-删除',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200) DEFAULT ''    COMMENT '备注',
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 菜单表
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    menu_name   VARCHAR(50)  NOT NULL      COMMENT '菜单名称',
    parent_id   BIGINT       DEFAULT 0     COMMENT '父菜单ID',
    sort        INT          DEFAULT 0     COMMENT '排序',
    path        VARCHAR(200) DEFAULT ''    COMMENT '路由路径',
    component   VARCHAR(200) DEFAULT ''    COMMENT '组件路径',
    query_param VARCHAR(200) DEFAULT ''    COMMENT '路由参数',
    is_frame    TINYINT      DEFAULT 1     COMMENT '是否外链：0-是，1-否',
    is_cache    TINYINT      DEFAULT 0     COMMENT '是否缓存：0-缓存，1-不缓存',
    menu_type   TINYINT      NOT NULL      COMMENT '类型：1-目录，2-菜单，3-按钮',
    visible     TINYINT      DEFAULT 0     COMMENT '显示状态：0-显示，1-隐藏',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    perms       VARCHAR(100) DEFAULT ''    COMMENT '权限标识',
    icon        VARCHAR(100) DEFAULT ''    COMMENT '图标',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200) DEFAULT ''    COMMENT '备注',
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 用户岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_post;
CREATE TABLE sys_user_post (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '岗位ID',
    UNIQUE KEY uk_user_post (user_id, post_id),
    INDEX idx_post (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户岗位关联表';

-- ----------------------------
-- 角色部门关联表(数据权限)
-- ----------------------------
DROP TABLE IF EXISTS sys_role_dept;
CREATE TABLE sys_role_dept (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    UNIQUE KEY uk_role_dept (role_id, dept_id),
    INDEX idx_dept (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色部门关联表';

-- ----------------------------
-- 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    dict_name   VARCHAR(50)  NOT NULL      COMMENT '字典名称',
    dict_type   VARCHAR(50)  NOT NULL      COMMENT '字典类型',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200) DEFAULT ''    COMMENT '备注',
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '字典数据ID',
    dict_type   VARCHAR(50)  NOT NULL      COMMENT '字典类型',
    dict_label  VARCHAR(100) NOT NULL      COMMENT '字典标签',
    dict_value  VARCHAR(100) NOT NULL      COMMENT '字典值',
    dict_sort   INT          DEFAULT 0     COMMENT '排序',
    css_class   VARCHAR(50)  DEFAULT ''    COMMENT '样式属性',
    list_class  VARCHAR(50)  DEFAULT ''    COMMENT '表格回显样式',
    is_default  TINYINT      DEFAULT 0     COMMENT '是否默认：0-否，1-是',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_by   VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark      VARCHAR(200) DEFAULT ''    COMMENT '备注',
    INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 参数配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID',
    config_name  VARCHAR(100) NOT NULL      COMMENT '参数名称',
    config_key   VARCHAR(100) NOT NULL      COMMENT '参数键',
    config_value VARCHAR(200) NOT NULL      COMMENT '参数值',
    config_type  TINYINT      DEFAULT 0     COMMENT '系统内置：0-是，1-否',
    create_by    VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by    VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark       VARCHAR(200) DEFAULT ''    COMMENT '备注',
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    title          VARCHAR(50)  DEFAULT ''    COMMENT '操作模块',
    business_type  TINYINT      DEFAULT 0     COMMENT '业务类型：0-其它，1-新增，2-修改，3-删除',
    method         VARCHAR(200) DEFAULT ''    COMMENT '方法名称',
    request_method VARCHAR(10)  DEFAULT ''    COMMENT '请求方式',
    oper_name      VARCHAR(50)  DEFAULT ''    COMMENT '操作人员',
    oper_url       VARCHAR(200) DEFAULT ''    COMMENT '请求URL',
    oper_ip        VARCHAR(50)  DEFAULT ''    COMMENT '主机地址',
    oper_param     TEXT         DEFAULT NULL  COMMENT '请求参数',
    json_result    TEXT         DEFAULT NULL  COMMENT '返回参数',
    status         TINYINT      DEFAULT 0     COMMENT '状态：0-正常，1-异常',
    error_msg      TEXT         DEFAULT NULL  COMMENT '错误消息',
    oper_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    cost_time      BIGINT       DEFAULT 0     COMMENT '消耗时间(毫秒)',
    INDEX idx_oper_time (oper_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    username       VARCHAR(50)  DEFAULT ''    COMMENT '用户名',
    ipaddr         VARCHAR(50)  DEFAULT ''    COMMENT '登录IP',
    login_location VARCHAR(200) DEFAULT ''    COMMENT '登录地点',
    browser        VARCHAR(50)  DEFAULT ''    COMMENT '浏览器',
    os             VARCHAR(50)  DEFAULT ''    COMMENT '操作系统',
    status         TINYINT      DEFAULT 0     COMMENT '状态：0-成功，1-失败',
    msg            VARCHAR(200) DEFAULT ''    COMMENT '提示消息',
    login_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ----------------------------
-- 消息通知表
-- ----------------------------
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    notice_title  VARCHAR(100) NOT NULL      COMMENT '通知标题',
    notice_type   TINYINT      NOT NULL      COMMENT '类型：1-通知，2-公告',
    notice_content TEXT        DEFAULT NULL  COMMENT '通知内容',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-关闭',
    create_by     VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by     VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark        VARCHAR(200) DEFAULT ''    COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- ----------------------------
-- 定时任务表
-- ----------------------------
DROP TABLE IF EXISTS sys_job;
CREATE TABLE sys_job (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    job_name        VARCHAR(64)  NOT NULL      COMMENT '任务名称',
    job_group       VARCHAR(64)  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    invoke_target   VARCHAR(500) NOT NULL      COMMENT '调用目标',
    cron_expression VARCHAR(255) NOT NULL      COMMENT 'Cron表达式',
    misfire_policy  TINYINT      DEFAULT 1     COMMENT '执行策略：1-立即执行，2-执行一次，3-放弃执行',
    concurrent      TINYINT      DEFAULT 1     COMMENT '是否并发：0-允许，1-禁止',
    status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-暂停',
    create_by       VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(200) DEFAULT ''    COMMENT '备注',
    UNIQUE KEY uk_job_name_group (job_name, job_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务表';

-- ----------------------------
-- 定时任务执行日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_job_log;
CREATE TABLE sys_job_log (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    job_id          BIGINT       NOT NULL      COMMENT '任务ID',
    job_name        VARCHAR(64)  NOT NULL      COMMENT '任务名称',
    job_group       VARCHAR(64)  NOT NULL      COMMENT '任务组名',
    invoke_target   VARCHAR(500) NOT NULL      COMMENT '调用目标',
    job_message     VARCHAR(200) DEFAULT ''    COMMENT '日志信息',
    status          TINYINT      DEFAULT 0     COMMENT '状态：0-正常，1-失败',
    exception_info  TEXT         DEFAULT NULL  COMMENT '异常信息',
    start_time      DATETIME     NOT NULL      COMMENT '开始时间',
    end_time        DATETIME     DEFAULT NULL  COMMENT '结束时间',
    INDEX idx_job_id (job_id),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行日志表';

-- ----------------------------
-- 代码生成-表
-- ----------------------------
DROP TABLE IF EXISTS gen_table;
CREATE TABLE gen_table (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    table_name     VARCHAR(200) DEFAULT ''    COMMENT '表名称',
    table_comment  VARCHAR(500) DEFAULT ''    COMMENT '表描述',
    class_name     VARCHAR(100) DEFAULT ''    COMMENT '实体类名称',
    package_name   VARCHAR(100) DEFAULT ''    COMMENT '包名',
    module_name    VARCHAR(50)  DEFAULT ''    COMMENT '模块名',
    business_name  VARCHAR(50)  DEFAULT ''    COMMENT '业务名',
    function_name  VARCHAR(50)  DEFAULT ''    COMMENT '功能名称',
    function_author VARCHAR(50) DEFAULT ''    COMMENT '功能作者',
    gen_type       TINYINT      DEFAULT 0     COMMENT '生成代码方式：0-ZIP下载，1-自定义路径',
    gen_path       VARCHAR(200) DEFAULT '/'   COMMENT '生成路径',
    tpl_category   VARCHAR(50)  DEFAULT 'crud' COMMENT '模板类型：crud/tree/sub',
    create_by      VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark         VARCHAR(200) DEFAULT ''    COMMENT '备注',
    UNIQUE KEY uk_table_name (table_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成表';

-- ----------------------------
-- 代码生成-列
-- ----------------------------
DROP TABLE IF EXISTS gen_table_column;
CREATE TABLE gen_table_column (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    table_id       BIGINT       NOT NULL      COMMENT '表ID',
    column_name    VARCHAR(200) DEFAULT ''    COMMENT '列名称',
    column_comment VARCHAR(500) DEFAULT ''    COMMENT '列描述',
    column_type    VARCHAR(100) DEFAULT ''    COMMENT '列类型',
    java_type      VARCHAR(50)  DEFAULT ''    COMMENT 'Java类型',
    java_field     VARCHAR(50)  DEFAULT ''    COMMENT 'Java字段名',
    is_pk          TINYINT      DEFAULT 0     COMMENT '是否主键：0-否，1-是',
    is_increment   TINYINT      DEFAULT 0     COMMENT '是否自增：0-否，1-是',
    is_required    TINYINT      DEFAULT 0     COMMENT '是否必填：0-否，1-是',
    is_insert      TINYINT      DEFAULT 0     COMMENT '是否插入：0-否，1-是',
    is_edit        TINYINT      DEFAULT 0     COMMENT '是否编辑：0-否，1-是',
    is_list        TINYINT      DEFAULT 0     COMMENT '是否列表：0-否，1-是',
    is_query       TINYINT      DEFAULT 0     COMMENT '是否查询：0-否，1-是',
    query_type     VARCHAR(50)  DEFAULT 'EQ'  COMMENT '查询方式：EQ/NE/GT/LT/LIKE/BETWEEN',
    html_type      VARCHAR(50)  DEFAULT 'input' COMMENT '显示类型：input/select/radio/checkbox/datetime/textarea',
    dict_type      VARCHAR(200) DEFAULT ''    COMMENT '字典类型',
    sort           INT          DEFAULT 0     COMMENT '排序',
    create_by      VARCHAR(50)  DEFAULT ''    COMMENT '创建者',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by      VARCHAR(50)  DEFAULT ''    COMMENT '更新者',
    update_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='代码生成列';

-- =============================================
-- 初始化数据
-- =============================================

-- 部门
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, sort, leader, status) VALUES
(100, 0,   '0',      '总公司', 0, '管理员', 1),
(101, 100, '0,100',  '研发部', 1, '张三',   1),
(102, 100, '0,100',  '市场部', 2, '李四',   1),
(103, 100, '0,100',  '财务部', 3, '王五',   1),
(104, 101, '0,100,101', '前端组', 1, '赵六', 1),
(105, 101, '0,100,101', '后端组', 2, '钱七', 1);

-- 岗位
INSERT INTO sys_post (id, post_code, post_name, sort, status, remark) VALUES
(1, 'ceo',  '董事长',  1, 1, ''),
(2, 'se',   '项目经理', 2, 1, ''),
(3, 'hr',   '人力资源', 3, 1, ''),
(4, 'user', '普通员工', 4, 1, '');

-- 用户 (admin/123456)
INSERT INTO sys_user (id, dept_id, username, nickname, password, email, phone, gender, avatar, status, create_by, remark) VALUES
(1, 100, 'admin', '系统管理员', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin@example.com', '13800138000', 1, '', 1, 'admin', '管理员'),
(2, 101, 'ry',    '普通用户',   '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'ry@example.com',    '13800138001', 1, '', 1, 'admin', '测试用户');

-- 角色
INSERT INTO sys_role (id, role_name, role_key, role_sort, data_scope, status, create_by, remark) VALUES
(1, '超级管理员', 'admin', 1, 1, 1, 'admin', '超级管理员'),
(2, '普通角色',   'common', 2, 2, 1, 'admin', '普通角色');

-- 用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- 用户岗位
INSERT INTO sys_user_post (user_id, post_id) VALUES
(1, 1),
(2, 4);

-- 菜单
INSERT INTO sys_menu (id, menu_name, parent_id, sort, path, component, menu_type, visible, status, perms, icon, create_by) VALUES
-- 一级目录
(1,  '首页',       0, 1, '/dashboard',   'dashboard/index',    2, 0, 1, '',                'HomeFilled', 'admin'),
(2,  '系统管理',   0, 2, '/system',      '',                   1, 0, 1, '',                'Setting',    'admin'),
(3,  '系统监控',   0, 3, '/monitor',     '',                   1, 0, 1, '',                'Monitor',    'admin'),
(4,  '开发工具',   0, 4, '/devtools',    '',                   1, 0, 1, '',                'Tools',      'admin'),
-- 系统管理子菜单
(100, '用户管理',  2, 1, '/system/user',       'system/user/index',       2, 0, 1, 'system:user:list',    'User',        'admin'),
(101, '角色管理',  2, 2, '/system/role',       'system/role/index',       2, 0, 1, 'system:role:list',    'UserFilled',  'admin'),
(102, '菜单管理',  2, 3, '/system/menu',       'system/menu/index',       2, 0, 1, 'system:menu:list',    'Menu',        'admin'),
(103, '部门管理',  2, 4, '/system/dept',       'system/dept/index',       2, 0, 1, 'system:dept:list',    'OfficeBuilding', 'admin'),
(104, '岗位管理',  2, 5, '/system/post',       'system/post/index',       2, 0, 1, 'system:post:list',    'Postcard',    'admin'),
(105, '字典管理',  2, 6, '/system/dict',       'system/dict/index',       2, 0, 1, 'system:dict:list',    'Collection',  'admin'),
(106, '参数配置',  2, 7, '/system/config',     'system/config/index',     2, 0, 1, 'system:config:list',  'SetUp',       'admin'),
(107, '通知公告',  2, 8, '/system/notice',     'system/notice/index',     2, 0, 1, 'system:notice:list', 'Bell',        'admin'),
-- 用户管理按钮
(1000, '用户查询', 100, 1, '', '', 3, 0, 1, 'system:user:query',    '', 'admin'),
(1001, '用户新增', 100, 2, '', '', 3, 0, 1, 'system:user:add',     '', 'admin'),
(1002, '用户修改', 100, 3, '', '', 3, 0, 1, 'system:user:edit',    '', 'admin'),
(1003, '用户删除', 100, 4, '', '', 3, 0, 1, 'system:user:remove',  '', 'admin'),
(1004, '重置密码', 100, 5, '', '', 3, 0, 1, 'system:user:resetPwd','', 'admin'),
(1005, '用户导出', 100, 6, '', '', 3, 0, 1, 'system:user:export',  '', 'admin'),
-- 角色管理按钮
(1006, '角色查询', 101, 1, '', '', 3, 0, 1, 'system:role:query',   '', 'admin'),
(1007, '角色新增', 101, 2, '', '', 3, 0, 1, 'system:role:add',     '', 'admin'),
(1008, '角色修改', 101, 3, '', '', 3, 0, 1, 'system:role:edit',    '', 'admin'),
(1009, '角色删除', 101, 4, '', '', 3, 0, 1, 'system:role:remove',  '', 'admin'),
-- 菜单管理按钮
(1010, '菜单查询', 102, 1, '', '', 3, 0, 1, 'system:menu:query',   '', 'admin'),
(1011, '菜单新增', 102, 2, '', '', 3, 0, 1, 'system:menu:add',     '', 'admin'),
(1012, '菜单修改', 102, 3, '', '', 3, 0, 1, 'system:menu:edit',    '', 'admin'),
(1013, '菜单删除', 102, 4, '', '', 3, 0, 1, 'system:menu:remove',  '', 'admin'),
-- 部门管理按钮
(1014, '部门查询', 103, 1, '', '', 3, 0, 1, 'system:dept:query',   '', 'admin'),
(1015, '部门新增', 103, 2, '', '', 3, 0, 1, 'system:dept:add',     '', 'admin'),
(1016, '部门修改', 103, 3, '', '', 3, 0, 1, 'system:dept:edit',    '', 'admin'),
(1017, '部门删除', 103, 4, '', '', 3, 0, 1, 'system:dept:remove',  '', 'admin'),
-- 岗位管理按钮
(1018, '岗位查询', 104, 1, '', '', 3, 0, 1, 'system:post:query',   '', 'admin'),
(1019, '岗位新增', 104, 2, '', '', 3, 0, 1, 'system:post:add',     '', 'admin'),
(1020, '岗位修改', 104, 3, '', '', 3, 0, 1, 'system:post:edit',    '', 'admin'),
(1021, '岗位删除', 104, 4, '', '', 3, 0, 1, 'system:post:remove',  '', 'admin'),
-- 字典管理按钮
(1022, '字典查询', 105, 1, '', '', 3, 0, 1, 'system:dict:query',   '', 'admin'),
(1023, '字典新增', 105, 2, '', '', 3, 0, 1, 'system:dict:add',     '', 'admin'),
(1024, '字典修改', 105, 3, '', '', 3, 0, 1, 'system:dict:edit',    '', 'admin'),
(1025, '字典删除', 105, 4, '', '', 3, 0, 1, 'system:dict:remove',  '', 'admin'),
-- 参数配置按钮
(1026, '参数查询', 106, 1, '', '', 3, 0, 1, 'system:config:query',  '', 'admin'),
(1027, '参数新增', 106, 2, '', '', 3, 0, 1, 'system:config:add',    '', 'admin'),
(1028, '参数修改', 106, 3, '', '', 3, 0, 1, 'system:config:edit',   '', 'admin'),
(1029, '参数删除', 106, 4, '', '', 3, 0, 1, 'system:config:remove', '', 'admin'),
-- 通知公告按钮
(1030, '公告查询', 107, 1, '', '', 3, 0, 1, 'system:notice:query',  '', 'admin'),
(1031, '公告新增', 107, 2, '', '', 3, 0, 1, 'system:notice:add',    '', 'admin'),
(1032, '公告修改', 107, 3, '', '', 3, 0, 1, 'system:notice:edit',   '', 'admin'),
(1033, '公告删除', 107, 4, '', '', 3, 0, 1, 'system:notice:remove', '', 'admin'),
-- 系统监控子菜单
(110, '操作日志',  3, 1, '/monitor/operlog',    'monitor/operlog/index',    2, 0, 1, 'monitor:operlog:list',    'Document',    'admin'),
(111, '登录日志',  3, 2, '/monitor/loginlog',   'monitor/loginlog/index',   2, 0, 1, 'monitor:loginlog:list',   'Key',         'admin'),
(112, '在线用户',  3, 3, '/monitor/online',     'monitor/online/index',     2, 0, 1, 'monitor:online:list',     'UserFilled',  'admin'),
(113, '服务器监控',3, 4, '/monitor/server',     'monitor/server/index',     2, 0, 1, 'monitor:server:list',     'Cpu',         'admin'),
-- 开发工具子菜单
(120, '代码生成',  4, 1, '/devtools/gen',       'devtools/gen/index',       2, 0, 1, 'devtools:gen:list',       'Code',        'admin'),
(121, '定时任务',  4, 2, '/devtools/job',       'devtools/job/index',       2, 0, 1, 'devtools:job:list',       'Timer',       'admin');

-- 角色菜单关联（超级管理员拥有所有菜单）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,100),(1,101),(1,102),(1,103),(1,104),(1,105),(1,106),(1,107),
(1,110),(1,111),(1,112),(1,113),(1,120),(1,121),
(1,1000),(1,1001),(1,1002),(1,1003),(1,1004),(1,1005),
(1,1006),(1,1007),(1,1008),(1,1009),
(1,1010),(1,1011),(1,1012),(1,1013),
(1,1014),(1,1015),(1,1016),(1,1017),
(1,1018),(1,1019),(1,1020),(1,1021),
(1,1022),(1,1023),(1,1024),(1,1025),
(1,1026),(1,1027),(1,1028),(1,1029),
(1,1030),(1,1031),(1,1032),(1,1033);
-- 普通角色仅有首页
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1);

-- 角色部门关联（普通角色-自定义数据权限）
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (2, 101), (2, 104), (2, 105);

-- 字典类型
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, create_by, remark) VALUES
(1,  '用户性别', 'sys_user_sex',     1, 'admin', '用户性别列表'),
(2,  '菜单状态', 'sys_show_hide',    1, 'admin', '菜单状态列表'),
(3,  '系统开关', 'sys_normal_disable',1, 'admin', '系统开关列表'),
(4,  '任务状态', 'sys_job_status',   1, 'admin', '任务状态列表'),
(5,  '系统是否', 'sys_yes_no',       1, 'admin', '系统是否列表'),
(6,  '通知类型', 'sys_notice_type',  1, 'admin', '通知类型列表'),
(7,  '通知状态', 'sys_notice_status',1, 'admin', '通知状态列表'),
(8,  '操作类型', 'sys_oper_type',    1, 'admin', '操作类型列表'),
(9,  '数据范围', 'sys_data_scope',   1, 'admin', '数据范围列表'),
(10, '菜单类型', 'sys_menu_type',    1, 'admin', '菜单类型列表');

-- 字典数据
INSERT INTO sys_dict_data (dict_type, dict_label, dict_value, dict_sort, is_default, status, create_by, remark) VALUES
('sys_user_sex',      '男',   '1', 1, 0, 1, 'admin', ''),
('sys_user_sex',      '女',   '2', 2, 0, 1, 'admin', ''),
('sys_user_sex',      '未知', '0', 3, 1, 1, 'admin', ''),
('sys_show_hide',     '显示', '0', 1, 1, 1, 'admin', ''),
('sys_show_hide',     '隐藏', '1', 2, 0, 1, 'admin', ''),
('sys_normal_disable', '正常', '1', 1, 1, 1, 'admin', ''),
('sys_normal_disable', '停用', '0', 2, 0, 1, 'admin', ''),
('sys_job_status',    '启用', '1', 1, 1, 1, 'admin', ''),
('sys_job_status',    '暂停', '0', 2, 0, 1, 'admin', ''),
('sys_yes_no',        '是',   '1', 1, 1, 1, 'admin', ''),
('sys_yes_no',        '否',   '0', 2, 0, 1, 'admin', ''),
('sys_notice_type',   '通知', '1', 1, 1, 1, 'admin', ''),
('sys_notice_type',   '公告', '2', 2, 0, 1, 'admin', ''),
('sys_notice_status', '正常', '1', 1, 1, 1, 'admin', ''),
('sys_notice_status', '关闭', '0', 2, 0, 1, 'admin', ''),
('sys_oper_type',     '其它', '0', 1, 1, 1, 'admin', ''),
('sys_oper_type',     '新增', '1', 2, 0, 1, 'admin', ''),
('sys_oper_type',     '修改', '2', 3, 0, 1, 'admin', ''),
('sys_oper_type',     '删除', '3', 4, 0, 1, 'admin', ''),
('sys_data_scope',    '全部数据',   '1', 1, 1, 1, 'admin', ''),
('sys_data_scope',    '自定义',     '2', 2, 0, 1, 'admin', ''),
('sys_data_scope',    '本部门',     '3', 3, 0, 1, 'admin', ''),
('sys_data_scope',    '本部门及以下','4', 4, 0, 1, 'admin', ''),
('sys_data_scope',    '仅本人',     '5', 5, 0, 1, 'admin', ''),
('sys_menu_type',     '目录', '1', 1, 0, 1, 'admin', ''),
('sys_menu_type',     '菜单', '2', 2, 0, 1, 'admin', ''),
('sys_menu_type',     '按钮', '3', 3, 0, 1, 'admin', '');

-- 参数配置
INSERT INTO sys_config (id, config_name, config_key, config_value, config_type, create_by, remark) VALUES
(1, '用户初始密码',     'sys.user.initPassword',  '123456', 0, 'admin', '用户初始密码'),
(2, '账号自助-是否开启', 'sys.account.register',   'false', 0, 'admin', '是否开启注册'),
(3, '用户管理-账号初始', 'sys.user.defaultAvatar', '',       0, 'admin', '默认头像');

-- 消息通知
INSERT INTO sys_notice (id, notice_title, notice_type, notice_content, status, create_by, remark) VALUES
(1, '系统上线通知', 1, '后台管理系统已上线，请各位知悉。', 1, 'admin', ''),
(2, '系统维护公告', 2, '系统将于本周六凌晨进行维护升级。', 1, 'admin', '');

-- 定时任务
INSERT INTO sys_job (id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, remark) VALUES
(1, '系统任务(无参)', 'DEFAULT', 'ryTask.noParams',  '0/10 * * * * ?', 1, 1, 0, 'admin', ''),
(2, '系统任务(有参)', 'DEFAULT', 'ryTask.params(\'ry\')', '0/15 * * * * ?', 1, 1, 0, 'admin', '');

-- 代码生成业务表
CREATE TABLE IF NOT EXISTS gen_table (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    table_name  VARCHAR(200) DEFAULT ''  COMMENT '表名称',
    table_comment VARCHAR(500) DEFAULT '' COMMENT '表描述',
    class_name  VARCHAR(100) DEFAULT ''  COMMENT '实体类名称',
    package_name VARCHAR(100) DEFAULT '' COMMENT '包路径',
    module_name VARCHAR(30)  DEFAULT ''  COMMENT '模块名',
    business_name VARCHAR(30) DEFAULT '' COMMENT '业务名',
    function_name VARCHAR(50) DEFAULT '' COMMENT '功能名',
    function_name_query VARCHAR(50) DEFAULT '' COMMENT '功能名(查询)',
    gen_type    CHAR(1)      DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
    gen_path    VARCHAR(200) DEFAULT '/' COMMENT '生成路径',
    tpl_category VARCHAR(30) DEFAULT 'crud' COMMENT '使用的模板',
    tpl_web_type VARCHAR(30) DEFAULT '' COMMENT '前端类型',
    parent_menu_id VARCHAR(50) DEFAULT '' COMMENT '上级菜单ID',
    parent_menu_name VARCHAR(50) DEFAULT '' COMMENT '上级菜单名称',
    menu_id     BIGINT       DEFAULT NULL COMMENT '菜单ID',
    create_by   VARCHAR(64)  DEFAULT ''  COMMENT '创建者',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT ''  COMMENT '更新者',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='代码生成业务表';

-- 代码生成字段表
CREATE TABLE IF NOT EXISTS gen_table_column (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '编号',
    table_id    BIGINT       DEFAULT NULL COMMENT '归属表编号',
    column_name VARCHAR(200) DEFAULT ''  COMMENT '列名称',
    column_comment VARCHAR(500) DEFAULT '' COMMENT '列描述',
    column_type VARCHAR(100) DEFAULT ''  COMMENT '列类型',
    java_type   VARCHAR(500) DEFAULT ''  COMMENT 'JAVA类型',
    java_field  VARCHAR(200) DEFAULT ''  COMMENT 'JAVA字段名',
    is_pk       INT          DEFAULT 0   COMMENT '是否主键（1是）',
    is_increment INT         DEFAULT 0   COMMENT '是否自增（1是）',
    is_required INT          DEFAULT 0   COMMENT '是否必填（1是）',
    is_insert   INT          DEFAULT 0   COMMENT '是否为插入字段（1是）',
    is_edit     INT          DEFAULT 0   COMMENT '是否编辑字段（1是）',
    is_list     INT          DEFAULT 0   COMMENT '是否列表字段（1是）',
    is_query    INT          DEFAULT 0   COMMENT '是否查询字段（1是）',
    query_type  VARCHAR(200) DEFAULT 'EQ' COMMENT '查询方式',
    html_type   VARCHAR(200) DEFAULT ''  COMMENT '显示类型',
    dict_type   VARCHAR(200) DEFAULT ''  COMMENT '字典类型',
    sort        INT          DEFAULT 0   COMMENT '排序',
    create_by   VARCHAR(64)  DEFAULT ''  COMMENT '创建者',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64)  DEFAULT ''  COMMENT '更新者',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='代码生成字段表';

COMMIT;
