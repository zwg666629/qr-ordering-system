package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.entity.Order;
import com.qr.ordering.dto.OrderCreateDTO;
import com.qr.ordering.vo.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    OrderVO createOrder(OrderCreateDTO orderCreateDTO);
    
    /**
     * 根据订单ID获取订单详情
     */
    OrderVO getOrderDetail(Long orderId);
    
    /**
     * 根据订单号获取订单详情
     */
    OrderVO getOrderByNumber(String orderNumber);
    
    /**
     * 获取订单列表
     */
    List<OrderVO> getOrderList(Long tableId);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, Integer status);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);
    
    /**
     * 支付订单
     */
    boolean payOrder(Long orderId, Integer paymentMethod);
    
    /**
     * 完成订单
     */
    boolean completeOrder(Long orderId);
    
    /**
     * 生成订单号
     */
    String generateOrderNumber();
}
