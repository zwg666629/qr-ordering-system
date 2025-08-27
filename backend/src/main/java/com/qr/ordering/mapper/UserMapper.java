package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND status = 1")
    User findByPhone(String phone);
}