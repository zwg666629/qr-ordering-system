package com.qr.ordering.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qr.ordering.common.Result;
import com.qr.ordering.entity.Admin;
import com.qr.ordering.mapper.AdminMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员管理控制器（管理端）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/admin")
@CrossOrigin
@Api(tags = "管理员管理（管理端）")
public class AdminManagementController {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 分页查询管理员列表
     */
    @ApiOperation("分页查询管理员列表")
    @GetMapping("/list")
    public Result<Page<Admin>> getAdminList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("角色") @RequestParam(required = false) Integer role,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        try {
            Page<Admin> pageObj = new Page<>(page, size);
            LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();

            // 关键词搜索（用户名、真实姓名、手机号、邮箱）
            if (StringUtils.hasText(keyword)) {
                queryWrapper.and(wrapper -> 
                    wrapper.like(Admin::getUsername, keyword)
                           .or().like(Admin::getRealName, keyword)
                           .or().like(Admin::getPhone, keyword)
                           .or().like(Admin::getEmail, keyword)
                );
            }

            // 角色筛选
            if (role != null) {
                queryWrapper.eq(Admin::getRole, role);
            }

            // 状态筛选
            if (status != null) {
                queryWrapper.eq(Admin::getStatus, status);
            }

            // 按创建时间倒序
            queryWrapper.orderByDesc(Admin::getCreateTime);

            Page<Admin> adminPage = adminMapper.selectPage(pageObj, queryWrapper);
            
            // 清除密码信息
            adminPage.getRecords().forEach(admin -> admin.setPassword(null));

            log.info("查询管理员列表成功，页码：{}，大小：{}，总数：{}", page, size, adminPage.getTotal());
            return Result.success(adminPage);
        } catch (Exception e) {
            log.error("查询管理员列表失败", e);
            return Result.error("查询管理员列表失败");
        }
    }

    /**
     * 获取管理员详情
     */
    @ApiOperation("获取管理员详情")
    @GetMapping("/{id}")
    public Result<Admin> getAdminDetail(@ApiParam("管理员ID") @PathVariable Long id) {
        try {
            Admin admin = adminMapper.selectById(id);
            if (admin == null) {
                return Result.error("管理员不存在");
            }

            // 清除密码信息
            admin.setPassword(null);

            log.info("获取管理员详情成功，管理员ID：{}", id);
            return Result.success(admin);
        } catch (Exception e) {
            log.error("获取管理员详情失败", e);
            return Result.error("获取管理员详情失败");
        }
    }

    /**
     * 创建管理员
     */
    @ApiOperation("创建管理员")
    @PostMapping("/create")
    public Result<Boolean> createAdmin(@ApiParam("管理员信息") @RequestBody Admin admin) {
        try {
            // 校验必填字段
            if (!StringUtils.hasText(admin.getUsername()) || !StringUtils.hasText(admin.getPassword())) {
                return Result.error("用户名和密码不能为空");
            }

            // 检查用户名是否已存在
            Admin existAdmin = adminMapper.findByUsername(admin.getUsername());
            if (existAdmin != null) {
                return Result.error("用户名已存在");
            }

            // 设置默认值
            admin.setId(null);
            admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
            admin.setStatus(admin.getStatus() == null ? 1 : admin.getStatus());
            admin.setRole(admin.getRole() == null ? 2 : admin.getRole());
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());

            int result = adminMapper.insert(admin);
            if (result > 0) {
                log.info("创建管理员成功，用户名：{}", admin.getUsername());
                return Result.success(true, "创建成功");
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            log.error("创建管理员失败", e);
            return Result.error("创建管理员失败");
        }
    }

    /**
     * 更新管理员信息
     */
    @ApiOperation("更新管理员信息")
    @PutMapping("/{id}")
    public Result<Boolean> updateAdmin(
            @ApiParam("管理员ID") @PathVariable Long id,
            @ApiParam("管理员信息") @RequestBody Admin admin) {
        try {
            Admin existAdmin = adminMapper.selectById(id);
            if (existAdmin == null) {
                return Result.error("管理员不存在");
            }

            // 如果要修改用户名，检查是否重复
            if (StringUtils.hasText(admin.getUsername()) && 
                !admin.getUsername().equals(existAdmin.getUsername())) {
                Admin duplicateAdmin = adminMapper.findByUsername(admin.getUsername());
                if (duplicateAdmin != null && !duplicateAdmin.getId().equals(id)) {
                    return Result.error("用户名已存在");
                }
            }

            // 更新字段
            admin.setId(id);
            if (StringUtils.hasText(admin.getPassword())) {
                admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
            } else {
                admin.setPassword(null); // 不更新密码
            }
            admin.setUpdateTime(LocalDateTime.now());

            int result = adminMapper.updateById(admin);
            if (result > 0) {
                log.info("更新管理员成功，管理员ID：{}", id);
                return Result.success(true, "更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新管理员失败", e);
            return Result.error("更新管理员失败");
        }
    }

    /**
     * 更新管理员状态
     */
    @ApiOperation("更新管理员状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateAdminStatus(
            @ApiParam("管理员ID") @PathVariable Long id,
            @ApiParam("状态信息") @RequestBody Map<String, Object> request) {
        try {
            Integer status = (Integer) request.get("status");
            if (status == null || (status != 0 && status != 1)) {
                return Result.error("状态参数无效");
            }

            Admin admin = adminMapper.selectById(id);
            if (admin == null) {
                return Result.error("管理员不存在");
            }

            admin.setStatus(status);
            admin.setUpdateTime(LocalDateTime.now());
            
            int result = adminMapper.updateById(admin);
            if (result > 0) {
                String action = status == 1 ? "启用" : "禁用";
                log.info("{}管理员成功，管理员ID：{}，用户名：{}", action, id, admin.getUsername());
                return Result.success(true, action + "成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("更新管理员状态失败", e);
            return Result.error("更新管理员状态失败");
        }
    }

    /**
     * 重置管理员密码
     */
    @ApiOperation("重置管理员密码")
    @PostMapping("/{id}/reset-password")
    public Result<Boolean> resetAdminPassword(@ApiParam("管理员ID") @PathVariable Long id) {
        try {
            Admin admin = adminMapper.selectById(id);
            if (admin == null) {
                return Result.error("管理员不存在");
            }

            // 重置为默认密码
            String defaultPassword = "123456";
            admin.setPassword(DigestUtils.md5DigestAsHex(defaultPassword.getBytes()));
            admin.setUpdateTime(LocalDateTime.now());

            int result = adminMapper.updateById(admin);
            if (result > 0) {
                log.info("重置管理员密码成功，管理员ID：{}，用户名：{}", id, admin.getUsername());
                return Result.success(true, "密码重置为：" + defaultPassword);
            } else {
                return Result.error("重置失败");
            }
        } catch (Exception e) {
            log.error("重置管理员密码失败", e);
            return Result.error("重置管理员密码失败");
        }
    }

    /**
     * 获取管理员统计信息
     */
    @ApiOperation("获取管理员统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getAdminStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总管理员数
            LambdaQueryWrapper<Admin> totalWrapper = new LambdaQueryWrapper<>();
            long totalAdmins = adminMapper.selectCount(totalWrapper);
            statistics.put("totalAdmins", totalAdmins);
            
            // 正常管理员数
            long activeAdmins = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getStatus, 1)
            );
            statistics.put("activeAdmins", activeAdmins);
            
            // 禁用管理员数
            long inactiveAdmins = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getStatus, 0)
            );
            statistics.put("inactiveAdmins", inactiveAdmins);
            
            // 超级管理员数
            long superAdmins = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getRole, 1)
            );
            statistics.put("superAdmins", superAdmins);

            // 普通管理员数
            long normalAdmins = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getRole, 2)
            );
            statistics.put("normalAdmins", normalAdmins);

            log.info("获取管理员统计信息成功");
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取管理员统计信息失败", e);
            return Result.error("获取管理员统计信息失败");
        }
    }
}
