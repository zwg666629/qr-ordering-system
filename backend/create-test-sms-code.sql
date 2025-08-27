-- 创建测试验证码 107829 (永不过期)
INSERT INTO sms_code (
    phone, 
    code, 
    type, 
    used, 
    expire_time, 
    create_time
) VALUES (
    '13725656629',           -- 手机号（从截图中看到的）
    '107829',                -- 验证码
    1,                       -- 类型：1-注册
    0,                       -- 未使用
    '2099-12-31 23:59:59',   -- 过期时间：2099年（永不过期）
    NOW()                    -- 创建时间：当前时间
);

-- 验证插入结果
SELECT * FROM sms_code WHERE phone = '13725656629' AND code = '107829';
