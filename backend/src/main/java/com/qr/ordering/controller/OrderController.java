package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.dto.OrderCreateDTO;
import com.qr.ordering.service.OrderService;
import com.qr.ordering.vo.OrderVO;
// import io.swagger.annotations.*;
// import io.swagger.annotations.*;
// import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
// @Api
@CrossOrigin
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping("/create")
    // @ApiOperation
    public Result<OrderVO> createOrder(@Validated @RequestBody OrderCreateDTO orderCreateDTO) {
        log.info("创建订单：{}", orderCreateDTO);
        try {
            OrderVO orderVO = orderService.createOrder(orderCreateDTO);
            return Result.success(orderVO);
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    // @ApiOperation
    public Result<OrderVO> getOrderDetail(
            @PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        if (orderVO == null) {
            return Result.error("订单不存在");
        }
        return Result.success(orderVO);
    }
    
    @GetMapping("/number/{orderNumber}")
    // @ApiOperation
    public Result<OrderVO> getOrderByNumber(
            @PathVariable String orderNumber) {
        OrderVO orderVO = orderService.getOrderByNumber(orderNumber);
        if (orderVO == null) {
            return Result.error("订单不存在");
        }
        return Result.success(orderVO);
    }
    
    @GetMapping("/list")
    // @ApiOperation
    public Result<List<OrderVO>> getOrderList(
            @RequestParam(required = false) Long tableId) {
        List<OrderVO> list = orderService.getOrderList(tableId);
        return Result.success(list);
    }
    
    @PutMapping("/{id}/status")
    // @ApiOperation
    public Result<Boolean> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        boolean result = orderService.updateOrderStatus(id, status);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新订单状态失败");
        }
    }
    
    @PostMapping("/{id}/cancel")
    // @ApiOperation
    public Result<Boolean> cancelOrder(
            @PathVariable Long id) {
        boolean result = orderService.cancelOrder(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("取消订单失败，只有待支付的订单可以取消");
        }
    }
    
    @PostMapping("/{id}/pay")
    // @ApiOperation
    public Result<Boolean> payOrder(
            @PathVariable Long id,
            @RequestParam Integer paymentMethod) {
        boolean result = orderService.payOrder(id, paymentMethod);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("支付订单失败");
        }
    }
    
    @PostMapping("/{id}/complete")
    // @ApiOperation
    public Result<Boolean> completeOrder(
            @PathVariable Long id) {
        boolean result = orderService.completeOrder(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("完成订单失败");
        }
    }
}
