package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.entity.DiningTable;
import com.qr.ordering.service.DiningTableService;
// import io.swagger.annotations.*;
// import io.swagger.annotations.*;
// import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 餐桌管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/table")
// @Api
@CrossOrigin
public class TableController {
    
    @Autowired
    private DiningTableService diningTableService;
    
    @GetMapping("/list")
    // @ApiOperation
    public Result<List<DiningTable>> getAllTables() {
        List<DiningTable> tables = diningTableService.getAllTables();
        return Result.success(tables);
    }
    
    @GetMapping("/{id}")
    // @ApiOperation
    public Result<DiningTable> getTableById(
            @PathVariable Long id) {
        DiningTable table = diningTableService.getById(id);
        if (table == null) {
            return Result.error("餐桌不存在");
        }
        return Result.success(table);
    }
    
    @GetMapping("/number/{tableNumber}")
    // @ApiOperation
    public Result<DiningTable> getTableByNumber(
            @PathVariable String tableNumber) {
        DiningTable table = diningTableService.getByTableNumber(tableNumber);
        if (table == null) {
            return Result.error("餐桌不存在");
        }
        return Result.success(table);
    }
    
    @PostMapping("/create")
    // @ApiOperation
    public Result<Boolean> createTable(@RequestBody DiningTable table) {
        log.info("创建餐桌：{}", table);
        try {
            boolean result = diningTableService.createTable(table);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("餐桌号已存在");
            }
        } catch (Exception e) {
            log.error("创建餐桌失败", e);
            return Result.error("创建餐桌失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/batch-create")
    // @ApiOperation
    public Result<Boolean> batchCreateTables(
            @RequestParam Integer startNumber,
            @RequestParam Integer count,
            @RequestParam Integer seats) {
        log.info("批量创建餐桌：起始{}，数量{}，座位数：{}", startNumber, count, seats);
        try {
            boolean result = diningTableService.batchCreateTables(startNumber, startNumber + count - 1, seats);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("批量创建餐桌失败");
            }
        } catch (Exception e) {
            log.error("批量创建餐桌失败", e);
            return Result.error("批量创建餐桌失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    // @ApiOperation
    public Result<Boolean> updateTable(
            @PathVariable Long id,
            @RequestBody DiningTable table) {
        table.setId(id);
        boolean result = diningTableService.updateById(table);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新餐桌信息失败");
        }
    }
    
    @PutMapping("/{id}/status")
    // @ApiOperation
    public Result<Boolean> updateTableStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        boolean result = diningTableService.updateTableStatus(id, status);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("更新餐桌状态失败");
        }
    }
    
    @DeleteMapping("/{id}")
    // @ApiOperation
    public Result<Boolean> deleteTable(
            @PathVariable Long id) {
        boolean result = diningTableService.removeById(id);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error("删除餐桌失败");
        }
    }
    
    @GetMapping("/{id}/qrcode")
    // @ApiOperation
    public Result<String> getTableQrCode(
            @PathVariable Long id) {
        try {
            String qrCode = diningTableService.generateQrCode(id);
            return Result.success(qrCode);
        } catch (Exception e) {
            log.error("获取餐桌{}二维码失败", id, e);
            return Result.error("生成二维码失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}/qrurl")
    // @ApiOperation
    public Result<String> getTableQrUrl(
            @PathVariable Long id) {
        try {
            // 获取餐桌信息
            DiningTable table = diningTableService.getById(id);
            if (table == null) {
                return Result.error("餐桌不存在");
            }
            
            // 直接返回二维码内容URL
            String qrUrl = "http://localhost:3003/menu?table=" + table.getTableNumber();
            return Result.success(qrUrl);
        } catch (Exception e) {
            log.error("获取餐桌{}二维码URL失败", id, e);
            return Result.error("获取二维码URL失败: " + e.getMessage());
        }
    }
}


