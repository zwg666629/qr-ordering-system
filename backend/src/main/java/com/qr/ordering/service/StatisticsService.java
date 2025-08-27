package com.qr.ordering.service;

import com.qr.ordering.vo.StatisticsVO;

/**
 * 统计数据服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取仪表盘统计数据
     */
    StatisticsVO getDashboardStatistics();
}
