package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.entity.DiningTable;
import com.qr.ordering.mapper.DiningTableMapper;
import com.qr.ordering.service.DiningTableService;
import com.qr.ordering.util.QrCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 餐桌服务实现
 */
@Slf4j
@Service
public class DiningTableServiceImpl extends ServiceImpl<DiningTableMapper, DiningTable> implements DiningTableService {
    
    @Autowired
    private QrCodeUtil qrCodeUtil;
    
    @Value("${app.frontend.url:http://localhost:3003}")
    private String frontendUrl;
    
    @Override
    public List<DiningTable> getAllTables() {
        LambdaQueryWrapper<DiningTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DiningTable::getTableNumber);
        return this.list(wrapper);
    }
    
    @Override
    public DiningTable getByTableNumber(String tableNumber) {
        LambdaQueryWrapper<DiningTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiningTable::getTableNumber, tableNumber);
        return this.getOne(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTable(DiningTable table) {
        // 检查餐桌号是否已存在
        DiningTable existing = getByTableNumber(table.getTableNumber());
        if (existing != null) {
            log.warn("餐桌号 {} 已存在", table.getTableNumber());
            return false;
        }
        
        table.setStatus(1); // 空闲状态
        table.setCreateTime(LocalDateTime.now());
        table.setUpdateTime(LocalDateTime.now());
        
        // 生成二维码
        boolean saved = this.save(table);
        if (saved) {
            String qrCode = generateQrCode(table.getId());
            table.setQrCode(qrCode);
            this.updateById(table);
        }
        
        return saved;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTableStatus(Long id, Integer status) {
        DiningTable table = new DiningTable();
        table.setId(id);
        table.setStatus(status);
        table.setUpdateTime(LocalDateTime.now());
        return this.updateById(table);
    }
    
    @Override
    public String generateQrCode(Long tableId) {
        try {
            log.info("开始生成餐桌 {} 的二维码", tableId);
            
            // 获取餐桌信息
            DiningTable table = this.getById(tableId);
            if (table == null) {
                log.error("餐桌 {} 不存在", tableId);
                throw new RuntimeException("餐桌不存在");
            }
            log.info("餐桌信息: {}", table);
            
            // 生成二维码内容（前端菜单页面URL + 餐桌号参数）
            log.info("前端URL: {}", frontendUrl);
            String qrContent = qrCodeUtil.generateTableQrContent(table.getTableNumber(), frontendUrl);
            log.info("二维码内容: {}", qrContent);
            
            // 生成二维码图片（Base64格式）
            String qrCodeBase64 = qrCodeUtil.generateQrCodeBase64(qrContent);
            log.info("二维码生成成功，长度: {}", qrCodeBase64.length());
            
            // 更新餐桌的二维码字段
            table.setQrCode(qrCodeBase64);
            this.updateById(table);
            
            log.info("生成餐桌 {} 的二维码成功，内容：{}", tableId, qrContent);
            
            return qrCodeBase64;
        } catch (Exception e) {
            log.error("生成餐桌 {} 的二维码失败", tableId, e);
            throw new RuntimeException("生成二维码失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateTables(int startNumber, int endNumber, int seats) {
        if (startNumber > endNumber) {
            log.error("起始编号不能大于结束编号");
            return false;
        }
        
        List<DiningTable> tables = new ArrayList<>();
        for (int i = startNumber; i <= endNumber; i++) {
            DiningTable table = new DiningTable();
            table.setTableNumber(String.format("%03d", i)); // 格式化为3位数字
            table.setSeats(seats);
            table.setStatus(1); // 空闲状态
            table.setCreateTime(LocalDateTime.now());
            table.setUpdateTime(LocalDateTime.now());
            tables.add(table);
        }
        
        // 批量保存
        boolean result = this.saveBatch(tables);
        
        // 生成二维码
        if (result) {
            for (DiningTable table : tables) {
                String qrCode = generateQrCode(table.getId());
                table.setQrCode(qrCode);
            }
            this.updateBatchById(tables);
        }
        
        return result;
    }
}


