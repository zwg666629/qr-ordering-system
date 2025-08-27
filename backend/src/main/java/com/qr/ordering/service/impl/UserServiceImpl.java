package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.dto.user.UserLoginDTO;
import com.qr.ordering.dto.user.UserRegisterDTO;
import com.qr.ordering.dto.user.UserUpdateDTO;
import com.qr.ordering.entity.User;
import com.qr.ordering.entity.UserLoginLog;
import com.qr.ordering.mapper.UserLoginLogMapper;
import com.qr.ordering.mapper.UserMapper;
import com.qr.ordering.service.SmsCodeService;
import com.qr.ordering.service.UserService;
import com.qr.ordering.util.JwtUtil;
import com.qr.ordering.util.PasswordUtil;
import com.qr.ordering.vo.user.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserLoginLogMapper loginLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO register(UserRegisterDTO registerDTO, String ip) {
        // 验证验证码
        if (!smsCodeService.verifyCode(registerDTO.getPhone(), registerDTO.getCode(), 1)) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 检查手机号是否已注册
        User existUser = getUserByPhone(registerDTO.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已注册");
        }

        // 创建用户
        User user = new User();
        user.setPhone(registerDTO.getPhone());
        user.setPassword(PasswordUtil.encode(registerDTO.getPassword()));
        user.setNickname(StringUtils.hasText(registerDTO.getNickname()) ?
                registerDTO.getNickname() : "用户" + registerDTO.getPhone().substring(7));
        user.setStatus(1);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        this.save(user);

        // 记录登录日志
        recordLoginLog(user.getId(), 1, ip, null);

        // 生成token
        String token = jwtUtil.generateUserToken(user.getId(), user.getPhone());

        log.info("用户注册成功: phone={}, userId={}", registerDTO.getPhone(), user.getId());

        return new UserLoginVO(token, user.getId(), user.getPhone(), user.getNickname(), user.getAvatar());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO login(UserLoginDTO loginDTO, String ip) {
        // 查询用户
        User user = getUserByPhone(loginDTO.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        Integer loginType = Integer.parseInt(loginDTO.getLoginType());

        // 验证登录方式
        if (loginType == 1) {
            // 密码登录
            if (!StringUtils.hasText(loginDTO.getPassword())) {
                throw new RuntimeException("密码不能为空");
            }
            if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        } else if (loginType == 2) {
            // 验证码登录
            if (!StringUtils.hasText(loginDTO.getCode())) {
                throw new RuntimeException("验证码不能为空");
            }
            if (!smsCodeService.verifyCode(loginDTO.getPhone(), loginDTO.getCode(), 2)) {
                throw new RuntimeException("验证码错误或已过期");
            }
        } else {
            throw new RuntimeException("不支持的登录方式");
        }

        // 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setUpdateTime(LocalDateTime.now());
        this.updateById(user);

        // 记录登录日志
        recordLoginLog(user.getId(), loginType, ip, null);

        // 生成token
        String token = jwtUtil.generateUserToken(user.getId(), user.getPhone());

        log.info("用户登录成功: phone={}, userId={}, loginType={}", loginDTO.getPhone(), user.getId(), loginType);

        return new UserLoginVO(token, user.getId(), user.getPhone(), user.getNickname(), user.getAvatar());
    }

    @Override
    public User getUserByToken(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return null;
        }
        return this.getById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = new User();
        user.setId(userId);
        BeanUtils.copyProperties(updateDTO, user);
        user.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(user);
        if (result) {
            log.info("用户信息更新成功: userId={}", userId);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());

        boolean result = this.updateById(user);
        if (result) {
            log.info("用户密码修改成功: userId={}", userId);
        }
        return result;
    }

    @Override
    public User getUserByPhone(String phone) {
        return baseMapper.findByPhone(phone);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, Integer loginType, String ip, String userAgent) {
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setUserId(userId);
        loginLog.setLoginType(loginType);
        loginLog.setLoginIp(ip);
        loginLog.setUserAgent(userAgent);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(loginLog);
    }
}