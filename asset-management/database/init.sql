-- 资产管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS asset_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE asset_management;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    department VARCHAR(100) COMMENT '部门',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 资产分类表
CREATE TABLE IF NOT EXISTS asset_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (category_code),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产分类表';

-- 资产信息表
CREATE TABLE IF NOT EXISTS asset_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资产ID',
    asset_code VARCHAR(50) NOT NULL UNIQUE COMMENT '资产编号',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    specifications VARCHAR(200) COMMENT '规格型号',
    unit VARCHAR(20) DEFAULT '台' COMMENT '单位',
    purchase_date DATE COMMENT '购买日期',
    purchase_price DECIMAL(12,2) COMMENT '购买价格',
    supplier VARCHAR(100) COMMENT '供应商',
    warranty_period INT COMMENT '保修期(月)',
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用，IN_USE-使用中，MAINTENANCE-维修中，SCRAPPED-已报废',
    location VARCHAR(100) COMMENT '存放位置',
    responsible_person VARCHAR(50) COMMENT '责任人',
    description TEXT COMMENT '资产描述',
    image_url VARCHAR(200) COMMENT '资产图片',
    create_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (asset_code),
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (category_id) REFERENCES asset_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产信息表';

-- 资产借用记录表
CREATE TABLE IF NOT EXISTS asset_borrow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '借用记录ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    borrower_id BIGINT NOT NULL COMMENT '借用人ID',
    borrower_name VARCHAR(50) NOT NULL COMMENT '借用人姓名',
    borrow_date DATETIME NOT NULL COMMENT '借用日期',
    expected_return_date DATETIME COMMENT '预计归还日期',
    actual_return_date DATETIME COMMENT '实际归还日期',
    borrow_reason VARCHAR(200) COMMENT '借用原因',
    return_status VARCHAR(20) NOT NULL DEFAULT 'BORROWED' COMMENT '归还状态：BORROWED-借用中，RETURNED-已归还，OVERDUE-已逾期',
    return_condition VARCHAR(20) COMMENT '归还状态：GOOD-完好，DAMAGED-损坏',
    remarks TEXT COMMENT '备注',
    approver VARCHAR(50) COMMENT '审批人',
    approve_time DATETIME COMMENT '审批时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_asset (asset_id),
    INDEX idx_borrower (borrower_id),
    INDEX idx_status (return_status),
    INDEX idx_borrow_date (borrow_date),
    FOREIGN KEY (asset_id) REFERENCES asset_info(id),
    FOREIGN KEY (borrower_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产借用记录表';

-- 资产维护记录表
CREATE TABLE IF NOT EXISTS asset_maintenance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '维护记录ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    maintenance_type VARCHAR(20) NOT NULL COMMENT '维护类型：REPAIR-维修，MAINTENANCE-保养，INSPECTION-检查',
    maintenance_date DATETIME NOT NULL COMMENT '维护日期',
    maintenance_cost DECIMAL(10,2) COMMENT '维护费用',
    maintenance_company VARCHAR(100) COMMENT '维护单位',
    maintenance_person VARCHAR(50) COMMENT '维护人员',
    problem_description TEXT COMMENT '问题描述',
    solution TEXT COMMENT '解决方案',
    result VARCHAR(20) COMMENT '维护结果：SUCCESS-成功，FAILED-失败',
    remarks TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_asset (asset_id),
    INDEX idx_type (maintenance_type),
    INDEX idx_date (maintenance_date),
    FOREIGN KEY (asset_id) REFERENCES asset_info(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产维护记录表';

-- 资产变更记录表
CREATE TABLE IF NOT EXISTS asset_change_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '变更记录ID',
    asset_id BIGINT NOT NULL COMMENT '资产ID',
    change_type VARCHAR(20) NOT NULL COMMENT '变更类型：CREATE-新增，UPDATE-修改，DELETE-删除，STATUS-状态变更',
    change_field VARCHAR(50) COMMENT '变更字段',
    old_value TEXT COMMENT '原值',
    new_value TEXT COMMENT '新值',
    change_reason VARCHAR(200) COMMENT '变更原因',
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_asset (asset_id),
    INDEX idx_type (change_type),
    INDEX idx_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产变更记录表';

-- 初始化数据

-- 插入默认用户（密码都是原密码加密后的结果，这里用明文示意，实际应用中会加密）
INSERT INTO sys_user (username, password, real_name, email, phone, department, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', '13800138000', '信息部', 'ADMIN', 1),
('user', '$2a$10$2pWXBLAoHb7d1u7hZEeM.uijY7PzNaOXLEQBGmVQnNXX5XPl0lQ6O', '普通用户', 'user@example.com', '13800138001', '运营部', 'USER', 1);

-- 插入资产分类
INSERT INTO asset_category (category_name, category_code, parent_id, description, sort_order) VALUES
('办公设备', 'OFFICE', 0, '办公室常用设备', 1),
('电脑', 'COMPUTER', 1, '台式机、笔记本等', 1),
('外设', 'PERIPHERAL', 1, '打印机、扫描仪等', 2),
('IT设备', 'IT', 0, 'IT基础设施设备', 2),
('服务器', 'SERVER', 4, '各类服务器', 1),
('网络设备', 'NETWORK', 4, '路由器、交换机等', 2),
('家具', 'FURNITURE', 0, '办公家具', 3),
('办公桌椅', 'DESK_CHAIR', 7, '办公桌、椅子', 1);

-- 插入示例资产
INSERT INTO asset_info (asset_code, asset_name, category_id, specifications, unit, purchase_date, purchase_price, supplier, warranty_period, status, location, responsible_person, description, create_by) VALUES
('A20240001', '联想ThinkPad T14', 2, 'i5-11代/16G/512G SSD', '台', '2024-01-15', 6800.00, '联想官方旗舰店', 36, 'IN_USE', '3楼研发部', '张三', '研发部开发用笔记本', 'admin'),
('A20240002', 'Dell OptiPlex 7090', 2, 'i7-11代/32G/1T SSD', '台', '2024-02-20', 8500.00, '戴尔官方', 36, 'AVAILABLE', '仓库A区', '李四', '高配台式机', 'admin'),
('A20240003', 'HP LaserJet Pro', 3, 'M404dn 黑白激光打印机', '台', '2024-03-10', 2200.00, '惠普专卖店', 12, 'IN_USE', '2楼行政部', '王五', '行政部打印机', 'admin'),
('A20240004', 'Dell PowerEdge R740', 5, '双路至强/128G/4T', '台', '2023-06-01', 45000.00, '戴尔企业服务', 60, 'IN_USE', '机房1', '赵六', '应用服务器', 'admin'),
('A20240005', 'Cisco Catalyst 2960', 6, '24口千兆交换机', '台', '2023-08-15', 3500.00, '思科代理商', 36, 'IN_USE', '机房1', '赵六', '核心交换机', 'admin'),
('A20240006', '实木办公桌', 8, '1.6m*0.8m', '张', '2023-05-20', 1200.00, '办公家具城', 12, 'IN_USE', '4楼总经理办公室', '陈七', '总经理办公桌', 'admin');

-- 插入借用记录示例
INSERT INTO asset_borrow (asset_id, borrower_id, borrower_name, borrow_date, expected_return_date, borrow_reason, return_status) VALUES
(1, 2, '普通用户', '2024-01-20 09:00:00', '2024-07-20 18:00:00', '项目开发需要', 'BORROWED'),
(3, 2, '普通用户', '2024-03-15 10:00:00', '2024-03-15 17:00:00', '打印文件', 'RETURNED');

-- 更新已归还记录
UPDATE asset_borrow SET actual_return_date = '2024-03-15 16:30:00', return_condition = 'GOOD', return_status = 'RETURNED' WHERE id = 2;

-- 插入维护记录示例
INSERT INTO asset_maintenance (asset_id, maintenance_type, maintenance_date, maintenance_cost, maintenance_company, maintenance_person, problem_description, solution, result) VALUES
(3, 'REPAIR', '2024-04-10 14:00:00', 150.00, '惠普售后服务', '维修工程师', '卡纸故障', '清理纸道，更换搓纸轮', 'SUCCESS');

-- 插入变更日志示例
INSERT INTO asset_change_log (asset_id, change_type, change_field, old_value, new_value, change_reason, operator) VALUES
(1, 'STATUS', 'status', 'AVAILABLE', 'IN_USE', '分配给员工使用', 'admin'),
(3, 'STATUS', 'status', 'MAINTENANCE', 'IN_USE', '维修完成', 'admin');

COMMIT;
