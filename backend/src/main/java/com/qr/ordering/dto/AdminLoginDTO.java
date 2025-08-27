package com.qr.ordering.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 管理员登录DTO
 */
@Data
public class AdminLoginDTO {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}













