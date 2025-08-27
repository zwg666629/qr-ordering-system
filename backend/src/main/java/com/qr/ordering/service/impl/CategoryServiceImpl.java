package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.entity.Category;
import com.qr.ordering.mapper.CategoryMapper;
import com.qr.ordering.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}