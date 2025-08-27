package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.entity.OrderDetail;

import java.util.List;

/**
 * 订单详情服务接口
 */
public interface OrderDetailService extends IService<OrderDetail> {
    
    /**
     * 根据订单ID获取订单详情列表
     */
    List<OrderDetail> getByOrderId(Long orderId);
    
    /**
     * 批量保存订单详情
     */
    boolean saveBatch(List<OrderDetail> orderDetails);
}
