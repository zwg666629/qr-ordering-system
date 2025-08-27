package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.entity.SmsCode;
import com.qr.ordering.mapper.SmsCodeMapper;
import com.qr.ordering.service.SmsCodeService;
import com.qr.ordering.service.sms.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证码服务实现
 */
@Slf4j
@Service
public class SmsCodeServiceImpl extends ServiceImpl<SmsCodeMapper, SmsCode> implements SmsCodeService {

    @Autowired
    private SmsService smsService;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SMS_LIMIT_KEY = "sms:limit:";
    private static final String SMS_IP_LIMIT_KEY = "sms:ip:limit:";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final int PHONE_LIMIT_MINUTES = 1; // 同一手机号1分钟内只能发送一次
    private static final int IP_LIMIT_COUNT = 10; // 同一IP每小时最多发送10次

    @Override
    public boolean sendCode(String phone, Integer type, String ip) {
        // 检查发送频率限制
        if (!checkSendLimit(phone, ip)) {
            return false;
        }

        // 生成验证码
        String code = generateCode();

        // 发送短信
        boolean sendSuccess = smsService.sendSms(phone, code, type);
        if (!sendSuccess) {
            log.error("发送短信失败: phone={}, type={}", phone, type);
            return false;
        }

        // 保存验证码到数据库
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setUsed(0);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES));
        smsCode.setCreateTime(LocalDateTime.now());
        this.save(smsCode);

        // 设置发送限制
        setSendLimit(phone, ip);

        log.info("验证码发送成功: phone={}, type={}, code={}", phone, type, code);
        return true;
    }

    @Override
    public boolean verifyCode(String phone, String code, Integer type) {
        // 查询有效的验证码
        SmsCode smsCode = baseMapper.findValidCode(phone, type);
        if (smsCode == null) {
            log.warn("验证码不存在或已过期: phone={}, type={}", phone, type);
            return false;
        }

        // 验证验证码
        if (!code.equals(smsCode.getCode())) {
            log.warn("验证码错误: phone={}, inputCode={}, correctCode={}", phone, code, smsCode.getCode());
            return false;
        }

        // 标记验证码为已使用
        smsCode.setUsed(1);
        this.updateById(smsCode);

        log.info("验证码验证成功: phone={}, type={}", phone, type);
        return true;
    }

    @Override
    public boolean checkSendLimit(String phone, String ip) {
        // 如果没有Redis配置，跳过频率限制检查（开发模式）
        if (redisTemplate == null) {
            log.info("Redis未配置，跳过短信发送频率限制检查（开发模式）");
            return true;
        }

        // 检查手机号发送限制
        String phoneKey = SMS_LIMIT_KEY + phone;
        if (redisTemplate.hasKey(phoneKey)) {
            log.warn("手机号发送过于频繁: {}", phone);
            return false;
        }

        // 检查IP发送限制
        String ipKey = SMS_IP_LIMIT_KEY + ip;
        Integer ipCount = (Integer) redisTemplate.opsForValue().get(ipKey);
        if (ipCount != null && ipCount >= IP_LIMIT_COUNT) {
            log.warn("IP发送次数超限: {}", ip);
            return false;
        }

        return true;
    }

    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 设置发送限制
     */
    private void setSendLimit(String phone, String ip) {
        // 如果没有Redis配置，跳过设置限制（开发模式）
        if (redisTemplate == null) {
            log.info("Redis未配置，跳过设置短信发送限制（开发模式）");
            return;
        }

        // 设置手机号发送限制
        String phoneKey = SMS_LIMIT_KEY + phone;
        redisTemplate.opsForValue().set(phoneKey, 1, PHONE_LIMIT_MINUTES, TimeUnit.MINUTES);

        // 设置IP发送限制
        String ipKey = SMS_IP_LIMIT_KEY + ip;
        Integer ipCount = (Integer) redisTemplate.opsForValue().get(ipKey);
        if (ipCount == null) {
            redisTemplate.opsForValue().set(ipKey, 1, 1, TimeUnit.HOURS);
        } else {
            redisTemplate.opsForValue().set(ipKey, ipCount + 1, 1, TimeUnit.HOURS);
        }
    }
}