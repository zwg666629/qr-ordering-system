-- 插入初始数据

-- 清空现有数据（如果存在）
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM order_detail WHERE 1=1;
DELETE FROM orders WHERE 1=1;
DELETE FROM dish WHERE 1=1;
DELETE FROM category WHERE 1=1;
DELETE FROM dining_table WHERE 1=1;
DELETE FROM admin WHERE 1=1;

SET FOREIGN_KEY_CHECKS = 1;

-- 插入菜品分类
INSERT INTO category (id, name, sort) VALUES 
(1, '热菜', 1),
(2, '凉菜', 2),
(3, '汤品', 3),
(4, '主食', 4),
(5, '饮品', 5);

-- 插入餐桌
INSERT INTO dining_table (table_number, seats) VALUES 
('1', 4), ('2', 4), ('3', 6), ('4', 6), ('5', 8);

-- 插入示例菜品（使用本地占位图片）
INSERT INTO dish (name, category_id, price, image, description, sort) VALUES 
('宫保鸡丁', 1, 28.00, '/src/assets/dish-placeholder.svg', '经典川菜，鸡肉嫩滑，配菜丰富', 1),
('麻婆豆腐', 1, 18.00, '/src/assets/dish-placeholder.svg', '麻辣鲜香，豆腐嫩滑', 2),
('糖醋里脊', 1, 32.00, '/src/assets/dish-placeholder.svg', '酸甜可口，外酥内嫩', 3),
('凉拌黄瓜', 2, 12.00, '/src/assets/dish-placeholder.svg', '清爽解腻，爽脆可口', 1),
('拍黄瓜', 2, 10.00, '/src/assets/dish-placeholder.svg', '简单美味，清香脆嫩', 2),
('紫菜蛋花汤', 3, 15.00, '/src/assets/dish-placeholder.svg', '清淡营养，老少皆宜', 1),
('西红柿鸡蛋汤', 3, 16.00, '/src/assets/dish-placeholder.svg', '酸甜开胃，营养丰富', 2),
('米饭', 4, 3.00, '/src/assets/dish-placeholder.svg', '香甜软糯', 1),
('面条', 4, 8.00, '/src/assets/dish-placeholder.svg', '劲道爽滑', 2),
('可乐', 5, 6.00, '/src/assets/dish-placeholder.svg', '冰爽解腻', 1),
('果汁', 5, 8.00, '/src/assets/dish-placeholder.svg', '新鲜果汁，营养健康', 2);

-- 暂时注释掉测试订单数据，等系统启动后再手动创建
-- 订单数据将在系统运行后通过前端创建

-- 插入默认管理员账号 (admin/admin123, 密码MD5加密)
INSERT INTO admin (username, password, real_name, phone, email, status, role) VALUES 
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', '13800138000', 'admin@example.com', 1, 1),
('test', '098f6bcd4621d373cade4e832627b4f6', '测试管理员', '13900139000', 'test@example.com', 1, 2);