package com.qr.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体类
 */
@Data
@TableName("admin")
public class Admin {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String phone;
    
    private String email;
    
    private Integer status; // 1-正常 0-禁用
    
    private Integer role; // 1-超级管理员 2-普通管理员
    
    private LocalDateTime lastLoginTime;
    
    private String lastLoginIp;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}













