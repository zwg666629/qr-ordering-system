package com.qr.ordering.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码DTO
 */
@Data
public class SendSmsDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 类型 1-注册 2-登录 3-找回密码
     */
    @NotNull(message = "验证码类型不能为空")
    private Integer type;
}