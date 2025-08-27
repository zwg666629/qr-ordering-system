package com.qr.ordering.service.sms;

/**
 * 短信服务接口
 */
public interface SmsService {

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 类型 1-注册 2-登录 3-找回密码
     * @return 是否发送成功
     */
    boolean sendSms(String phone, String code, Integer type);
}