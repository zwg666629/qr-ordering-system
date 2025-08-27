package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 短信验证码Mapper接口
 */
@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {

    /**
     * 查询有效的验证码
     */
    @Select("SELECT * FROM sms_code WHERE phone = #{phone} AND type = #{type} AND used = 0 AND expire_time > CURRENT_TIMESTAMP ORDER BY create_time DESC LIMIT 1")
    SmsCode findValidCode(String phone, Integer type);
}