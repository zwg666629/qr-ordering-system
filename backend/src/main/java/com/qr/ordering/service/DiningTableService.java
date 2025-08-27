package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.entity.DiningTable;

import java.util.List;

/**
 * 餐桌服务接口
 */
public interface DiningTableService extends IService<DiningTable> {
    
    /**
     * 获取所有餐桌列表
     */
    List<DiningTable> getAllTables();
    
    /**
     * 根据餐桌号获取餐桌信息
     */
    DiningTable getByTableNumber(String tableNumber);
    
    /**
     * 创建餐桌
     */
    boolean createTable(DiningTable table);
    
    /**
     * 更新餐桌状态
     */
    boolean updateTableStatus(Long id, Integer status);
    
    /**
     * 生成餐桌二维码
     */
    String generateQrCode(Long tableId);
    
    /**
     * 批量生成餐桌
     */
    boolean batchCreateTables(int startNumber, int endNumber, int seats);
}
