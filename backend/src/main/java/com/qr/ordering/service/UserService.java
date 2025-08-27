package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.dto.user.UserLoginDTO;
import com.qr.ordering.dto.user.UserRegisterDTO;
import com.qr.ordering.dto.user.UserUpdateDTO;
import com.qr.ordering.entity.User;
import com.qr.ordering.vo.user.UserLoginVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    UserLoginVO register(UserRegisterDTO registerDTO, String ip);

    /**
     * 用户登录
     */
    UserLoginVO login(UserLoginDTO loginDTO, String ip);

    /**
     * 根据token获取用户信息
     */
    User getUserByToken(String token);

    /**
     * 更新用户信息
     */
    boolean updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 根据手机号查询用户
     */
    User getUserByPhone(String phone);
}