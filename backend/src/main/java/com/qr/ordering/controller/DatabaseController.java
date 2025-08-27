package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 数据库维护控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/database")
@CrossOrigin
public class DatabaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 修复order_detail表缺失的dish_image列
     */
    @PostMapping("/fix-order-detail")
    public Result<String> fixOrderDetailTable() {
        try {
            // 检查列是否存在
            String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                              "WHERE TABLE_SCHEMA = DATABASE() " +
                              "AND TABLE_NAME = 'order_detail' " +
                              "AND COLUMN_NAME = 'dish_image'";
            
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
            
            if (count == 0) {
                // 列不存在，添加列
                String alterSql = "ALTER TABLE order_detail " +
                                 "ADD COLUMN dish_image VARCHAR(500) COMMENT '菜品图片' " +
                                 "AFTER dish_name";
                jdbcTemplate.execute(alterSql);
                log.info("成功添加dish_image列到order_detail表");
                return Result.success("已成功添加dish_image列");
            } else {
                log.info("dish_image列已存在");
                return Result.success("dish_image列已存在，无需修复");
            }
        } catch (Exception e) {
            log.error("修复order_detail表失败", e);
            return Result.error("修复失败: " + e.getMessage());
        }
    }

    /**
     * 检查表结构
     */
    @GetMapping("/check-order-detail")
    public Result<String> checkOrderDetailTable() {
        try {
            String sql = "DESCRIBE order_detail";
            jdbcTemplate.queryForList(sql);
            return Result.success("order_detail表结构正常");
        } catch (Exception e) {
            log.error("检查表结构失败", e);
            return Result.error("检查失败: " + e.getMessage());
        }
    }
}
