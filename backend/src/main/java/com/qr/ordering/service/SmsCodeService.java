package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.entity.SmsCode;

/**
 * 短信验证码服务接口
 */
public interface SmsCodeService extends IService<SmsCode> {

    /**
     * 发送验证码
     * @param phone 手机号
     * @param type 类型 1-注册 2-登录 3-找回密码
     * @param ip 客户端IP
     * @return 是否发送成功
     */
    boolean sendCode(String phone, Integer type, String ip);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 类型
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code, Integer type);

    /**
     * 检查发送频率限制
     * @param phone 手机号
     * @param ip 客户端IP
     * @return 是否可以发送
     */
    boolean checkSendLimit(String phone, String ip);
}