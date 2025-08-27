package com.qr.ordering.service.impl;

import com.qr.ordering.dto.AdminLoginDTO;
import com.qr.ordering.entity.Admin;
import com.qr.ordering.mapper.AdminMapper;
import com.qr.ordering.service.AdminService;
import com.qr.ordering.util.JwtUtil;
import com.qr.ordering.vo.AdminLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public AdminLoginVO login(AdminLoginDTO loginDTO, String ip) {
        // 根据用户名查询管理员
        Admin admin = adminMapper.findByUsername(loginDTO.getUsername());
        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码（这里使用MD5加密，实际项目建议使用BCrypt）
        String encodedPassword = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!encodedPassword.equals(admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查账号状态
        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername());
        
        // 更新最后登录时间和IP
        updateLastLogin(admin.getId(), ip);
        
        log.info("管理员登录成功: {}", admin.getUsername());
        
        return new AdminLoginVO(token, admin.getId(), admin.getUsername(), 
                admin.getRealName(), admin.getRole());
    }
    
    @Override
    public Admin getAdminByToken(String token) {
        Long adminId = jwtUtil.getAdminIdFromToken(token);
        if (adminId == null) {
            return null;
        }
        return adminMapper.selectById(adminId);
    }
    
    @Override
    public Admin getById(Long id) {
        return adminMapper.selectById(id);
    }
    
    @Override
    public void updateLastLogin(Long adminId, String ip) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setLastLoginIp(ip);
        adminMapper.updateById(admin);
    }
}













