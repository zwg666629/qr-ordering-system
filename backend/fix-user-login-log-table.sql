-- 创建用户登录日志表
CREATE TABLE IF NOT EXISTS user_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    login_ip VARCHAR(50) COMMENT '登录IP',
    device_info VARCHAR(500) COMMENT '设备信息',
    login_type TINYINT DEFAULT 1 COMMENT '登录类型 1-注册登录 2-正常登录',
    login_status TINYINT DEFAULT 1 COMMENT '登录状态 1-成功 0-失败',
    fail_reason VARCHAR(200) COMMENT '失败原因',
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_login_status (login_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表';

-- 验证表是否创建成功
DESCRIBE user_login_log;
