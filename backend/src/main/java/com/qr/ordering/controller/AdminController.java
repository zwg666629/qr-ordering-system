package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.dto.AdminLoginDTO;
import com.qr.ordering.entity.Admin;
import com.qr.ordering.service.AdminService;
import com.qr.ordering.vo.AdminLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 管理员控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@Api(tags = "管理员管理")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@ApiParam("登录信息") @Validated @RequestBody AdminLoginDTO loginDTO,
                                      HttpServletRequest request) {
        try {
            String ip = request.getRemoteAddr();
            AdminLoginVO loginVO = adminService.login(loginDTO, ip);
            log.info("管理员登录成功: {}", loginDTO.getUsername());
            return Result.success(loginVO);
        } catch (Exception e) {
            log.error("管理员登录失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取管理员信息
     */
    @GetMapping("/info")
    public Result<Admin> getAdminInfo(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return Result.error("未登录或登录已过期");
            }
            
            Admin admin = adminService.getAdminByToken(token);
            if (admin == null) {
                return Result.error("用户信息不存在");
            }
            
            // 不返回密码
            admin.setPassword(null);
            return Result.success(admin);
        } catch (Exception e) {
            log.error("获取管理员信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}