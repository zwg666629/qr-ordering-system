package com.qr.ordering.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    
    /**
     * 餐桌ID
     */
    @NotNull(message = "餐桌ID不能为空")
    private Long tableId;

    /**
     * 用户ID（可选，游客下单时为空）
     */
    private Long userId;
    
    /**
     * 餐桌号
     */
    private String tableNumber;
    
    /**
     * 订单总金额
     */
    @NotNull(message = "订单金额不能为空")
    private BigDecimal totalAmount;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 订单项列表
     */
    @NotNull(message = "订单项不能为空")
    @Size(min = 1, message = "至少选择一个菜品")
    private List<OrderItemDTO> items;
    
    /**
     * 订单项DTO
     */
    @Data
    public static class OrderItemDTO {
        /**
         * 菜品ID
         */
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;
        
        /**
         * 菜品名称
         */
        private String dishName;
        
        /**
         * 菜品图片
         */
        private String dishImage;
        
        /**
         * 菜品单价
         */
        @NotNull(message = "菜品价格不能为空")
        private BigDecimal dishPrice;
        
        /**
         * 数量
         */
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        
        /**
         * 小计
         */
        private BigDecimal subtotal;
    }
}
