-- 修复 orders 表结构，添加 user_id 字段
-- 使用前请确保数据库连接正常

USE qr_ordering;

-- 添加 user_id 字段（允许 NULL，支持游客模式）
ALTER TABLE orders ADD COLUMN user_id BIGINT NULL COMMENT '用户ID（可选，游客下单时为空）' AFTER table_id;

-- 查看修改结果
DESCRIBE orders;

-- 显示修复完成信息
SELECT 'orders表user_id字段添加完成！' as message;
