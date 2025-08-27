package com.qr.ordering.service;

import com.qr.ordering.dto.AdminLoginDTO;
import com.qr.ordering.entity.Admin;
import com.qr.ordering.vo.AdminLoginVO;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO loginDTO, String ip);
    
    /**
     * 根据token获取管理员信息
     */
    Admin getAdminByToken(String token);
    
    /**
     * 根据ID获取管理员信息
     */
    Admin getById(Long id);
    
    /**
     * 更新最后登录时间和IP
     */
    void updateLastLogin(Long adminId, String ip);
}













