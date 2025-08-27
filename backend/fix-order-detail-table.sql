-- 修复 order_detail 表缺失的 dish_image 列
-- 检查列是否已存在，如果不存在则添加

-- 添加 dish_image 列
ALTER TABLE order_detail 
ADD COLUMN IF NOT EXISTS dish_image VARCHAR(500) COMMENT '菜品图片' 
AFTER dish_name;

-- 验证表结构
DESCRIBE order_detail;
