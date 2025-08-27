package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员Mapper接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    
    /**
     * 根据用户名查询管理员
     */
    @Select("SELECT * FROM admin WHERE username = #{username} AND status = 1")
    Admin findByUsername(String username);
}













