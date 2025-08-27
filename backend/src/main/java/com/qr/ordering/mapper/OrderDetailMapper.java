package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单详情数据访问层
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    
    /**
     * 根据订单ID查询订单详情列表
     */
    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> selectByOrderId(Long orderId);
}
