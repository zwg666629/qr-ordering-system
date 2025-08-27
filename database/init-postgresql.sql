-- PostgreSQL版本的数据库初始化脚本

-- 创建数据库
-- CREATE DATABASE qr_ordering;

-- 切换到数据库（在psql中执行）
-- \c qr_ordering;

-- 菜品分类表
CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    sort INTEGER DEFAULT 0,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 菜品表
CREATE TABLE IF NOT EXISTS dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image VARCHAR(500),
    description TEXT,
    status SMALLINT DEFAULT 1,
    sort INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 餐桌表
CREATE TABLE IF NOT EXISTS dining_table (
    id SERIAL PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL UNIQUE,
    qr_code VARCHAR(500),
    seats INTEGER DEFAULT 4,
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    table_id INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status SMALLINT DEFAULT 1,
    payment_method SMALLINT,
    payment_time TIMESTAMP,
    remark TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 订单详情表
CREATE TABLE IF NOT EXISTS order_detail (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    dish_id INTEGER NOT NULL,
    dish_name VARCHAR(100) NOT NULL,
    dish_price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    subtotal DECIMAL(10,2) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 管理员表
CREATE TABLE IF NOT EXISTS admin (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50),
    phone VARCHAR(20),
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_dish_category_id ON dish(category_id);
CREATE INDEX IF NOT EXISTS idx_orders_table_id ON orders(table_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_number ON orders(order_number);
CREATE INDEX IF NOT EXISTS idx_orders_create_time ON orders(create_time);
CREATE INDEX IF NOT EXISTS idx_order_detail_order_id ON order_detail(order_id);

-- 插入初始数据
INSERT INTO admin (username, password, name) VALUES 
('admin', '$2a$10$7JB720yubVSOfvVWdBXuHuCrb5C1vRFX.Jq8k2HvZdlL.QRnuq.1S', '管理员')
ON CONFLICT (username) DO NOTHING;

-- 插入菜品分类
INSERT INTO category (name, sort) VALUES 
('热菜', 1),
('凉菜', 2),
('汤品', 3),
('主食', 4),
('饮品', 5)
ON CONFLICT DO NOTHING;

-- 插入餐桌
INSERT INTO dining_table (table_number, seats) VALUES 
('1', 4), ('2', 4), ('3', 6), ('4', 6), ('5', 8)
ON CONFLICT (table_number) DO NOTHING;

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
('果汁', 5, 8.00, '新鲜果汁，营养健康', 2)
ON CONFLICT DO NOTHING;