package com.qr.ordering.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO {
    
    /**
     * 今日订单数
     */
    private Integer todayOrders;
    
    /**
     * 今日营收
     */
    private BigDecimal todayRevenue;
    
    /**
     * 今日顾客数
     */
    private Integer todayCustomers;
    
    /**
     * 菜品总数
     */
    private Integer totalDishes;
    
    /**
     * 热销菜品列表
     */
    private List<HotDishVO> hotDishes;
    
    /**
     * 最新订单列表
     */
    private List<OrderVO> recentOrders;
    
    /**
     * 热销菜品VO
     */
    @Data
    public static class HotDishVO {
        private Long id;
        private String name;
        private Integer sales;
    }
}
