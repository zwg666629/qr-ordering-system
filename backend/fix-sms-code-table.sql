    -- 创建短信验证码表
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
