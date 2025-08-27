package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.dto.user.*;
import com.qr.ordering.entity.User;
import com.qr.ordering.service.SmsCodeService;
import com.qr.ordering.service.UserService;
import com.qr.ordering.vo.user.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsCodeService smsCodeService;

    /**
     * 发送短信验证码
     */
    @ApiOperation("发送短信验证码")
    @PostMapping("/sms/send")
    public Result<Boolean> sendSms(@ApiParam("发送短信请求") @Validated @RequestBody SendSmsDTO sendSmsDTO,
                                   HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            boolean result = smsCodeService.sendCode(sendSmsDTO.getPhone(), sendSmsDTO.getType(), ip);
            if (result) {
                return Result.success(true, "验证码发送成功");
            } else {
                return Result.error("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<UserLoginVO> register(@ApiParam("注册信息") @Validated @RequestBody UserRegisterDTO registerDTO,
                                        HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            UserLoginVO loginVO = userService.register(registerDTO, ip);
            log.info("用户注册成功: {}", registerDTO.getPhone());
            return Result.success(loginVO);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@ApiParam("登录信息") @Validated @RequestBody UserLoginDTO loginDTO,
                                     HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            UserLoginVO loginVO = userService.login(loginDTO, ip);
            log.info("用户登录成功: {}", loginDTO.getPhone());
            return Result.success(loginVO);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // 去掉Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            User user = userService.getUserByToken(token);
            if (user == null) {
                return Result.error("用户不存在或token无效");
            }

            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestHeader("Authorization") String token,
                                          @ApiParam("用户信息") @Validated @RequestBody UserUpdateDTO updateDTO) {
        try {
            // 去掉Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            User user = userService.getUserByToken(token);
            if (user == null) {
                return Result.error("用户不存在或token无效");
            }

            boolean result = userService.updateUserInfo(user.getId(), updateDTO);
            if (result) {
                return Result.success(true, "更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Boolean> changePassword(@RequestHeader("Authorization") String token,
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        try {
            // 去掉Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            User user = userService.getUserByToken(token);
            if (user == null) {
                return Result.error("用户不存在或token无效");
            }

            boolean result = userService.changePassword(user.getId(), oldPassword, newPassword);
            if (result) {
                return Result.success(true, "密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}