-- 查看所有验证码记录
SELECT 
    id,
    phone,
    code,
    type,
    CASE 
        WHEN type = 1 THEN '注册'
        WHEN type = 2 THEN '登录'
        WHEN type = 3 THEN '重置密码'
        ELSE '未知'
    END as type_name,
    used,
    CASE 
        WHEN used = 0 THEN '未使用'
        WHEN used = 1 THEN '已使用'
        ELSE '未知'
    END as used_status,
    expire_time,
    CASE 
        WHEN expire_time > NOW() THEN '有效'
        ELSE '已过期'
    END as status,
    create_time,
    use_time
FROM sms_code 
ORDER BY create_time DESC;

-- 查看特定手机号的验证码
SELECT * FROM sms_code 
WHERE phone = '13725656629' 
ORDER BY create_time DESC;

-- 查看有效的验证码
SELECT * FROM sms_code 
WHERE used = 0 AND expire_time > NOW() 
ORDER BY create_time DESC;

-- 查看当前服务器时间
SELECT NOW() as current_time;

-- 检查表结构
DESCRIBE sms_code;
