-- 调试验证码问题的完整SQL脚本

-- 1. 检查数据库连接和当前数据库
SELECT DATABASE() as current_database, NOW() as current_time;

-- 2. 检查所有表
SHOW TABLES;

-- 3. 检查sms_code表是否存在
SELECT COUNT(*) as table_exists 
FROM information_schema.TABLES 
WHERE table_schema = DATABASE() 
AND table_name = 'sms_code';

-- 4. 如果表存在，查看表结构
-- DESCRIBE sms_code;

-- 5. 如果表存在，查看所有验证码数据
-- SELECT * FROM sms_code ORDER BY create_time DESC LIMIT 10;

-- 6. 查看特定手机号的验证码
-- SELECT * FROM sms_code WHERE phone = '13725656629';

-- 7. 查看有效的验证码（未使用且未过期）
-- SELECT * FROM sms_code 
-- WHERE used = 0 AND expire_time > NOW() 
-- ORDER BY create_time DESC;

-- 手动创建表的完整SQL（如果需要）
/*
CREATE TABLE IF NOT EXISTS sms_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type TINYINT NOT NULL COMMENT '类型 1-注册 2-登录 3-重置密码',
    used TINYINT DEFAULT 0 COMMENT '是否已使用 0-未使用 1-已使用',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    use_time TIMESTAMP NULL COMMENT '使用时间',
    INDEX idx_phone (phone),
    INDEX idx_code (code),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表';

-- 插入测试验证码
INSERT INTO sms_code (phone, code, type, used, expire_time, create_time) 
VALUES ('13725656629', '107829', 1, 0, '2099-12-31 23:59:59', NOW());
*/
