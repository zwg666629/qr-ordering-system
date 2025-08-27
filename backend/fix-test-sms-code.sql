-- 修复验证码问题的SQL脚本

-- 1. 查看当前的验证码状态
SELECT 
    id,
    phone,
    code,
    type,
    used,
    expire_time,
    create_time,
    CASE 
        WHEN expire_time > NOW() THEN '有效'
        ELSE '已过期'
    END as status,
    NOW() as current_time
FROM sms_code 
WHERE phone = '13725656629'
ORDER BY create_time DESC;

-- 2. 删除该手机号的所有现有验证码
DELETE FROM sms_code WHERE phone = '13725656629';

-- 3. 插入指定的测试验证码（永不过期）
INSERT INTO sms_code (
    phone, 
    code, 
    type, 
    used, 
    expire_time, 
    create_time
) VALUES (
    '13725656629',           -- 手机号
    '107829',                -- 指定的验证码
    1,                       -- 类型：1-注册
    0,                       -- 未使用
    '2099-12-31 23:59:59',   -- 过期时间：2099年（永不过期）
    NOW()                    -- 创建时间：当前时间
);

-- 4. 验证结果
SELECT 
    id,
    phone,
    code,
    type,
    used,
    expire_time,
    create_time,
    CASE 
        WHEN expire_time > NOW() THEN '有效 ✅'
        ELSE '已过期 ❌'
    END as status,
    TIMESTAMPDIFF(YEAR, NOW(), expire_time) as years_until_expire
FROM sms_code 
WHERE phone = '13725656629' AND code = '107829';

-- 5. 测试验证逻辑（模拟系统查询）
SELECT 
    '验证码查找测试' as test_name,
    COUNT(*) as found_count,
    CASE 
        WHEN COUNT(*) > 0 THEN '✅ 能找到有效验证码'
        ELSE '❌ 找不到有效验证码'
    END as result
FROM sms_code 
WHERE phone = '13725656629' 
AND type = 1 
AND used = 0 
AND expire_time > NOW();
