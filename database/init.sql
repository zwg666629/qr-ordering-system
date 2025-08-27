-- 扫码点餐系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS qr_ordering DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE qr_ordering;

-- 菜品分类表
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '菜品分类表';

-- 菜品表
CREATE TABLE dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image VARCHAR(500) COMMENT '图片',
    description TEXT COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id)
) COMMENT '菜品表';

-- 餐桌表
CREATE TABLE dining_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL UNIQUE COMMENT '桌号',
    qr_code VARCHAR(500) COMMENT '二维码',
    seats INT DEFAULT 4 COMMENT '座位数',
    status TINYINT DEFAULT 1 COMMENT '状态 1-空闲 2-占用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '餐桌表';

-- 订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    table_id BIGINT NOT NULL COMMENT '桌号ID',
    user_id BIGINT COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    status TINYINT DEFAULT 1 COMMENT '订单状态 1-待支付 2-已支付 3-制作中 4-已完成 5-已取消',
    payment_method TINYINT COMMENT '支付方式 1-微信 2-支付宝',
    payment_time DATETIME COMMENT '支付时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_table_id (table_id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_create_time (create_time)
) COMMENT '订单表';

-- 订单详情表
CREATE TABLE order_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    dish_price DECIMAL(10,2) NOT NULL COMMENT '菜品价格',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id)
) COMMENT '订单详情表';

-- 管理员表
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '管理员表';

-- 插入初始数据
-- 插入管理员账号 (密码: admin123)
INSERT INTO admin (username, password, name) VALUES 
('admin', '$2a$10$7JB720yubVSOfvVWdBXuHuCrb5C1vRFX.Jq8k2HvZdlL.QRnuq.1S', '管理员');

-- 插入菜品分类
INSERT INTO category (name, sort) VALUES 
('热菜', 1),
('凉菜', 2),
('汤品', 3),
('主食', 4),
('饮品', 5);

-- 插入餐桌
INSERT INTO dining_table (table_number, seats) VALUES 
('1', 4), ('2', 4), ('3', 6), ('4', 6), ('5', 8);

-- 插入示例菜品
INSERT INTO dish (name, category_id, price, description, sort) VALUES
('宫保鸡丁', 1, 28.00, '经典川菜，鸡肉嫩滑，配菜丰富', 1),
('麻婆豆腐', 1, 18.00, '麻辣鲜香，豆腐嫩滑', 2),
('糖醋里脊', 1, 32.00, '酸甜可口，外酥内嫩', 3),
('凉拌黄瓜', 2, 12.00, '清爽解腻，爽脆可口', 1),
('拍黄瓜', 2, 10.00, '简单美味，清香脆嫩', 2),
('紫菜蛋花汤', 3, 15.00, '清淡营养，老少皆宜', 1),
('西红柿鸡蛋汤', 3, 16.00, '酸甜开胃，营养丰富', 2),
('米饭', 4, 3.00, '香甜软糯', 1),
('面条', 4, 8.00, '劲道爽滑', 2),
('可乐', 5, 6.00, '冰爽解腻', 1),
('果汁', 5, 8.00, '新鲜果汁，营养健康', 2);

-- 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    password VARCHAR(100) COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
    birthday DATE COMMENT '生日',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone),
    INDEX idx_status (status)
) COMMENT '用户表';

-- 短信验证码表
CREATE TABLE sms_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type TINYINT NOT NULL COMMENT '类型 1-注册 2-登录 3-找回密码',
    used TINYINT DEFAULT 0 COMMENT '是否已使用 0-未使用 1-已使用',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone_type (phone, type),
    INDEX idx_expire_time (expire_time)
) COMMENT '短信验证码表';

-- 用户登录日志表
CREATE TABLE user_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    login_type TINYINT NOT NULL COMMENT '登录方式 1-密码登录 2-验证码登录',
    login_ip VARCHAR(50) COMMENT '登录IP',
    user_agent VARCHAR(500) COMMENT '用户代理',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) COMMENT '用户登录日志表';

-- 插入测试用户数据
INSERT INTO user (phone, password, nickname, status) VALUES
('13800138000', '$2a$10$7JB720yubVSOfvVWdBXuHuCrb5C1vRFX.Jq8k2HvZdlL.QRnuq.1S', '测试用户1', 1),
('13900139000', '$2a$10$7JB720yubVSOfvVWdBXuHuCrb5C1vRFX.Jq8k2HvZdlL.QRnuq.1S', '测试用户2', 1);