package com.qr.ordering.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 餐桌ID
     */
    private Long tableId;
    
    /**
     * 餐桌号
     */
    private String tableNumber;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态：1-待支付 2-已支付 3-制作中 4-已完成 5-已取消
     */
    private Integer status;
    
    /**
     * 状态文本
     */
    private String statusText;
    
    /**
     * 支付方式：1-微信支付 2-支付宝
     */
    private Integer paymentMethod;
    
    /**
     * 支付方式文本
     */
    private String paymentMethodText;
    
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /**
     * 订单详情列表
     */
    private List<OrderItemVO> items;
    
    /**
     * 订单项VO
     */
    @Data
    public static class OrderItemVO {
        /**
         * 订单详情ID
         */
        private Long id;
        
        /**
         * 菜品ID
         */
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
        private BigDecimal dishPrice;
        
        /**
         * 数量
         */
        private Integer quantity;
        
        /**
         * 小计金额
         */
        private BigDecimal subtotal;
    }
}
