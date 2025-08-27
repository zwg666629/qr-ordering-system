package com.qr.ordering.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 模拟短信服务实现（用于开发测试）
 */
@Slf4j
@Service
public class MockSmsServiceImpl implements SmsService {

    @Override
    public boolean sendSms(String phone, String code, Integer type) {
        String typeText = getTypeText(type);
        log.info("【模拟短信】发送{}验证码到手机号: {}, 验证码: {}", typeText, phone, code);

        // 模拟发送成功
        return true;
    }

    private String getTypeText(Integer type) {
        switch (type) {
            case 1: return "注册";
            case 2: return "登录";
            case 3: return "找回密码";
            default: return "未知";
        }
    }
}