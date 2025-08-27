package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qr.ordering.entity.Dish;
import com.qr.ordering.entity.Order;
import com.qr.ordering.service.*;
import com.qr.ordering.vo.OrderVO;
import com.qr.ordering.vo.StatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计数据服务实现
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private DishService dishService;
    
    @Override
    public StatisticsVO getDashboardStatistics() {
        StatisticsVO statistics = new StatisticsVO();
        
        try {
            // 获取今日开始和结束时间
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            // 今日订单数和营收
            LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
            todayWrapper.between(Order::getCreateTime, todayStart, todayEnd);
            List<Order> todayOrders = orderService.list(todayWrapper);
            
            statistics.setTodayOrders(todayOrders.size());
            
            // 计算今日营收（已支付的订单）
            BigDecimal todayRevenue = todayOrders.stream()
                    .filter(order -> order.getStatus() != null && order.getStatus() >= 2) // 已支付及以上状态
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.setTodayRevenue(todayRevenue);
            
            // 今日顾客数（估算为订单数，实际可以根据业务需求调整）
            statistics.setTodayCustomers(todayOrders.size());
            
            // 菜品总数
            long totalDishes = dishService.count();
            statistics.setTotalDishes((int) totalDishes);
            
            // 热销菜品TOP5（模拟数据，实际需要根据订单详情统计）
            List<StatisticsVO.HotDishVO> hotDishes = new ArrayList<>();
            List<Dish> dishes = dishService.list(new LambdaQueryWrapper<Dish>().last("LIMIT 5"));
            for (int i = 0; i < dishes.size(); i++) {
                Dish dish = dishes.get(i);
                StatisticsVO.HotDishVO hotDish = new StatisticsVO.HotDishVO();
                hotDish.setId(dish.getId());
                hotDish.setName(dish.getName());
                hotDish.setSales(100 - i * 10); // 模拟销量
                hotDishes.add(hotDish);
            }
            statistics.setHotDishes(hotDishes);
            
            // 最新订单（最近5条）
            LambdaQueryWrapper<Order> recentWrapper = new LambdaQueryWrapper<>();
            recentWrapper.orderByDesc(Order::getCreateTime).last("LIMIT 5");
            List<Order> recentOrderList = orderService.list(recentWrapper);
            
            List<OrderVO> recentOrders = new ArrayList<>();
            for (Order order : recentOrderList) {
                List<OrderVO> orderVOList = orderService.getOrderList(null);
                // 找到对应的OrderVO
                orderVOList.stream()
                        .filter(vo -> vo.getId().equals(order.getId()))
                        .findFirst()
                        .ifPresent(recentOrders::add);
            }
            statistics.setRecentOrders(recentOrders);
            
            log.info("获取仪表盘统计数据成功: 今日订单数={}, 今日营收={}, 菜品总数={}", 
                    statistics.getTodayOrders(), statistics.getTodayRevenue(), statistics.getTotalDishes());
            
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            // 返回默认值
            statistics.setTodayOrders(0);
            statistics.setTodayRevenue(BigDecimal.ZERO);
            statistics.setTodayCustomers(0);
            statistics.setTotalDishes(0);
            statistics.setHotDishes(new ArrayList<>());
            statistics.setRecentOrders(new ArrayList<>());
        }
        
        return statistics;
    }
}
