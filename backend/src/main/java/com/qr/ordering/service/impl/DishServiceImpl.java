package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.entity.Dish;
import com.qr.ordering.mapper.DishMapper;
import com.qr.ordering.service.DishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Override
    public List<Dish> getDishList(Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus, 1); // 只查询启用的菜品
        
        if (categoryId != null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        }
        
        queryWrapper.orderByAsc(Dish::getSort);
        return this.list(queryWrapper);
    }
    
    @Override
    public List<Dish> getAdminDishList(Long categoryId, Integer status, String name) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        }
        
        if (status != null) {
            queryWrapper.eq(Dish::getStatus, status);
        }
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Dish::getName, name.trim());
        }
        
        queryWrapper.orderByDesc(Dish::getCreateTime);
        return this.list(queryWrapper);
    }
}