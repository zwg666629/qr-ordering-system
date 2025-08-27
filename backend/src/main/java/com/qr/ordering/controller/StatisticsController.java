package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.service.StatisticsService;
import com.qr.ordering.vo.StatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
@Api(tags = "统计数据管理")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取仪表盘统计数据
     */
    @ApiOperation("获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<StatisticsVO> getDashboardStatistics() {
        try {
            StatisticsVO statistics = statisticsService.getDashboardStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return Result.error("获取统计数据失败");
        }
    }
}
