package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.entity.OrderDetail;
import com.qr.ordering.mapper.OrderDetailMapper;
import com.qr.ordering.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单详情服务实现
 */
@Slf4j
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    
    @Override
    public List<OrderDetail> getByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDetail::getOrderId, orderId);
        return this.list(wrapper);
    }
    
    @Override
    public boolean saveBatch(List<OrderDetail> orderDetails) {
        return super.saveBatch(orderDetails);
    }
}
