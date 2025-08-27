package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.UserLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登录日志Mapper接口
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLog> {
}