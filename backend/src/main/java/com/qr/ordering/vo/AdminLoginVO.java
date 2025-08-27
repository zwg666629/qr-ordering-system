package com.qr.ordering.vo;

import lombok.Data;

/**
 * 管理员登录返回VO
 */
@Data
public class AdminLoginVO {
    
    private String token;
    
    private Long adminId;
    
    private String username;
    
    private String realName;
    
    private Integer role;
    
    public AdminLoginVO(String token, Long adminId, String username, String realName, Integer role) {
        this.token = token;
        this.adminId = adminId;
        this.username = username;
        this.realName = realName;
        this.role = role;
    }
}













