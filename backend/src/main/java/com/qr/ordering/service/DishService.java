package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * 根据分类ID获取菜品列表（客户端用）
     */
    List<Dish> getDishList(Long categoryId);
    
    /**
     * 获取菜品列表（管理端用）
     */
    List<Dish> getAdminDishList(Long categoryId, Integer status, String name);
}