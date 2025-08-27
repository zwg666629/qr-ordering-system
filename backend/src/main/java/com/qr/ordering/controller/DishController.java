package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.entity.Category;
import com.qr.ordering.entity.Dish;
import com.qr.ordering.service.CategoryService;
import com.qr.ordering.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dish")
@CrossOrigin
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取菜品列表（支持分页和条件查询）
     */
    @GetMapping("/list")
    public Result<List<Dish>> getDishList(@RequestParam(required = false) Long categoryId,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) String name) {
        try {
            List<Dish> dishes;
            
            // 判断是否为管理员请求（有status参数或name参数）
            if (status != null || name != null) {
                // 管理员查询，支持所有状态
                dishes = dishService.getAdminDishList(categoryId, status, name);
            } else {
                // 客户端查询，只返回上架商品
                dishes = dishService.getDishList(categoryId);
            }
            
            return Result.success(dishes);
        } catch (Exception e) {
            log.error("获取菜品列表失败", e);
            return Result.error("获取菜品列表失败");
        }
    }

    /**
     * 获取菜品分类列表
     */
    @GetMapping("/categories")
    public Result<List<Category>> getCategories() {
        try {
            // 只返回启用状态的分类
            List<Category> categories = categoryService.lambdaQuery()
                    .eq(Category::getStatus, 1)
                    .orderByAsc(Category::getSort, Category::getId)
                    .list();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return Result.error("获取分类列表失败");
        }
    }

    /**
     * 创建菜品
     */
    @PostMapping("/create")
    public Result<String> createDish(@RequestBody Dish dish) {
        try {
            dishService.save(dish);
            return Result.success("创建成功");
        } catch (Exception e) {
            log.error("创建菜品失败", e);
            return Result.error("创建菜品失败");
        }
    }

    /**
     * 更新菜品
     */
    @PutMapping("/{id}")
    public Result<String> updateDish(@PathVariable Long id, @RequestBody Dish dish) {
        try {
            dish.setId(id);
            dishService.updateById(dish);
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新菜品失败", e);
            return Result.error("更新菜品失败");
        }
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteDish(@PathVariable Long id) {
        try {
            dishService.removeById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除菜品失败", e);
            return Result.error("删除菜品失败");
        }
    }

    /**
     * 更新菜品状态
     */
    @PutMapping("/{id}/status")
    public Result<String> updateDishStatus(@PathVariable Long id, @RequestBody Dish dish) {
        try {
            Dish updateDish = new Dish();
            updateDish.setId(id);
            updateDish.setStatus(dish.getStatus());
            dishService.updateById(updateDish);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            log.error("状态更新失败", e);
            return Result.error("状态更新失败");
        }
    }
}