package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.dto.OrderCreateDTO;
import com.qr.ordering.entity.Order;
import com.qr.ordering.entity.OrderDetail;
import com.qr.ordering.mapper.OrderMapper;
import com.qr.ordering.service.OrderDetailService;
import com.qr.ordering.service.OrderService;
import com.qr.ordering.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private OrderDetailService orderDetailService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderCreateDTO orderCreateDTO) {
        // 1. 创建订单主表
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setTableId(orderCreateDTO.getTableId());
        order.setTableNumber(orderCreateDTO.getTableNumber()); // 设置餐桌号
        order.setUserId(orderCreateDTO.getUserId()); // 支持用户ID
        order.setTotalAmount(orderCreateDTO.getTotalAmount());
        order.setStatus(1); // 待支付
        order.setRemark(orderCreateDTO.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        this.save(order);
        
        // 2. 创建订单详情
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderCreateDTO.OrderItemDTO item : orderCreateDTO.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setDishId(item.getDishId());
            detail.setDishName(item.getDishName());
            detail.setDishImage(item.getDishImage());
            detail.setDishPrice(item.getDishPrice());
            detail.setQuantity(item.getQuantity());
            detail.setSubtotal(item.getSubtotal());
            detail.setCreateTime(LocalDateTime.now());
            orderDetails.add(detail);
        }
        
        // 批量保存订单详情
        orderDetailService.saveBatch(orderDetails);
        
        // 3. 返回订单VO
        return getOrderDetail(order.getId());
    }
    
    @Override
    public OrderVO getOrderDetail(Long orderId) {
        // 获取订单主表信息
        Order order = this.getById(orderId);
        if (order == null) {
            return null;
        }
        
        // 转换为VO
        OrderVO orderVO = convertToVO(order);
        
        // 获取订单详情
        List<OrderDetail> details = orderDetailService.getByOrderId(orderId);
        List<OrderVO.OrderItemVO> items = details.stream().map(detail -> {
            OrderVO.OrderItemVO item = new OrderVO.OrderItemVO();
            BeanUtils.copyProperties(detail, item);
            return item;
        }).collect(Collectors.toList());
        
        orderVO.setItems(items);
        
        return orderVO;
    }
    
    @Override
    public OrderVO getOrderByNumber(String orderNumber) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNumber, orderNumber);
        Order order = this.getOne(wrapper);
        
        if (order == null) {
            return null;
        }
        
        return getOrderDetail(order.getId());
    }
    
    @Override
    public List<OrderVO> getOrderList(Long tableId) {
        try {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            if (tableId != null) {
                wrapper.eq(Order::getTableId, tableId);
            }
            wrapper.orderByDesc(Order::getCreateTime);
            
            List<Order> orders = this.list(wrapper);
            log.info("查询到订单数量: {}", orders.size());
            
            return orders.stream().map(order -> {
                try {
                    OrderVO vo = convertToVO(order);
                    // 获取订单详情
                    List<OrderDetail> details = orderDetailService.getByOrderId(order.getId());
                    log.info("订单ID: {}, 详情数量: {}", order.getId(), details.size());
                    List<OrderVO.OrderItemVO> items = details.stream().map(detail -> {
                        OrderVO.OrderItemVO item = new OrderVO.OrderItemVO();
                        BeanUtils.copyProperties(detail, item);
                        return item;
                    }).collect(Collectors.toList());
                    vo.setItems(items);
                    return vo;
                } catch (Exception e) {
                    log.error("转换订单VO失败, 订单ID: {}", order.getId(), e);
                    throw new RuntimeException("转换订单VO失败", e);
                }
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            throw new RuntimeException("获取订单列表失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        if (status == 3) { // 制作中
            // 可以在这里添加通知厨房的逻辑
            log.info("订单 {} 开始制作", orderId);
        }
        
        return this.updateById(order);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            return false;
        }
        
        // 只有待支付的订单可以取消
        if (order.getStatus() != 1) {
            log.warn("订单 {} 状态为 {}，不能取消", orderId, order.getStatus());
            return false;
        }
        
        return updateOrderStatus(orderId, 5); // 已取消
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Integer paymentMethod) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2); // 已支付
        order.setPaymentMethod(paymentMethod);
        order.setPaymentTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        boolean result = this.updateById(order);
        
        if (result) {
            // 支付成功后，自动将订单状态改为制作中
            updateOrderStatus(orderId, 3);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrder(Long orderId) {
        return updateOrderStatus(orderId, 4); // 已完成
    }
    
    @Override
    public String generateOrderNumber() {
        // 生成订单号：时间戳 + 随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return timestamp + random;
    }
    
    /**
     * 转换订单实体为VO
     */
    private OrderVO convertToVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        
        // 优先使用订单中已保存的餐桌号，如果为空才使用tableId作为备用
        if (order.getTableNumber() == null || order.getTableNumber().trim().isEmpty()) {
            if (order.getTableId() != null) {
                try {
                    // 只有当没有餐桌号时才用tableId作为备用
                    orderVO.setTableNumber("餐桌" + order.getTableId());
                    log.debug("使用tableId作为餐桌号备用: {}", orderVO.getTableNumber());
                } catch (Exception e) {
                    log.warn("查询餐桌号失败, tableId: {}", order.getTableId());
                    orderVO.setTableNumber("餐桌" + order.getTableId());
                }
            }
        } else {
            // 使用数据库中保存的正确餐桌号
            log.debug("使用数据库中的餐桌号: {}", order.getTableNumber());
        }
        
        // 设置状态文本
        String statusText = "";
        switch (order.getStatus()) {
            case 1:
                statusText = "待支付";
                break;
            case 2:
                statusText = "已支付";
                break;
            case 3:
                statusText = "制作中";
                break;
            case 4:
                statusText = "已完成";
                break;
            case 5:
                statusText = "已取消";
                break;
            default:
                statusText = "未知状态";
        }
        orderVO.setStatusText(statusText);
        
        // 设置支付方式文本
        if (order.getPaymentMethod() != null) {
            String paymentMethodText = order.getPaymentMethod() == 1 ? "微信支付" : "支付宝";
            orderVO.setPaymentMethodText(paymentMethodText);
        }
        
        return orderVO;
    }
}
