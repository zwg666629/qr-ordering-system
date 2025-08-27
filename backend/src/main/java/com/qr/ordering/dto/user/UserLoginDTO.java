package com.qr.ordering.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 密码（密码登录时必填）
     */
    private String password;

    /**
     * 验证码（验证码登录时必填）
     */
    private String code;

    /**
     * 登录类型 1-密码登录 2-验证码登录
     */
    @NotBlank(message = "登录类型不能为空")
    private String loginType;
}