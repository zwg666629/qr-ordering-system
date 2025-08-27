-- MySQL数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS qr_ordering CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE qr_ordering;

-- 创建用户（可选，如果需要专门的数据库用户）
-- CREATE USER 'qr_ordering_user'@'localhost' IDENTIFIED BY 'qr_ordering_password';
-- GRANT ALL PRIVILEGES ON qr_ordering.* TO 'qr_ordering_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 注意：表结构和初始数据将由Spring Boot自动执行schema.sql和data.sql创建













