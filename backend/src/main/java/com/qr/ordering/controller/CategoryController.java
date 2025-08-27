package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.entity.Category;
import com.qr.ordering.service.CategoryService;
// import io.swagger.annotations.*;
// import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/category")
// @Api
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/list")
    // @ApiOperation
    public Result<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.list();
        return Result.success(categories);
    }
    
    @GetMapping("/{id}")
    // @ApiOperation
    public Result<Category> getCategoryDetail(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }
    
    @PostMapping("/create")
    // @ApiOperation
    public Result<Category> createCategory(@RequestBody Category category) {
        try {
            boolean result = categoryService.save(category);
            if (result) {
                return Result.success(category);
            } else {
                return Result.error("创建分类失败");
            }
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error("创建分类失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    // @ApiOperation
    public Result<Boolean> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            boolean result = categoryService.updateById(category);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error("更新分类失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    // @ApiOperation
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        try {
            boolean result = categoryService.removeById(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error("删除分类失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}/status")
    // @ApiOperation
    public Result<Boolean> updateCategoryStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            Category category = new Category();
            category.setId(id);
            category.setStatus(status);
            boolean result = categoryService.updateById(category);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新分类状态失败", e);
            return Result.error("更新分类状态失败：" + e.getMessage());
        }
    }
}













