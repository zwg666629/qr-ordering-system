package com.qr.ordering.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情实体类
 */
@Data
@TableName("order_detail")
public class OrderDetail {
    /**
     * 订单详情ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
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
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
