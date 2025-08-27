package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qr.ordering.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 根据餐桌ID查询订单列表
     */
    @Select("SELECT * FROM orders WHERE table_id = #{tableId} ORDER BY create_time DESC")
    List<Order> selectByTableId(Long tableId);
    
    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM orders WHERE order_number = #{orderNumber}")
    Order selectByOrderNumber(String orderNumber);
    
    /**
     * 查询指定状态的订单
     */
    @Select("SELECT * FROM orders WHERE status = #{status} ORDER BY create_time DESC")
    List<Order> selectByStatus(Integer status);
}
