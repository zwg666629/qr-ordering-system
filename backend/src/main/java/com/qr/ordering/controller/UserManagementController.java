package com.qr.ordering.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qr.ordering.common.Result;
import com.qr.ordering.entity.User;
import com.qr.ordering.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器（管理端）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/user")
@CrossOrigin
@Api(tags = "用户管理（管理端）")
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("用户状态") @RequestParam(required = false) Integer status) {
        try {
            Page<User> pageObj = new Page<>(page, size);
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

            // 关键词搜索（手机号或昵称）
            if (StringUtils.hasText(keyword)) {
                queryWrapper.and(wrapper -> 
                    wrapper.like(User::getPhone, keyword)
                           .or()
                           .like(User::getNickname, keyword)
                );
            }

            // 状态筛选
            if (status != null) {
                queryWrapper.eq(User::getStatus, status);
            }

            // 按创建时间倒序
            queryWrapper.orderByDesc(User::getCreateTime);

            Page<User> userPage = userService.page(pageObj, queryWrapper);
            
            // 清除密码信息
            userPage.getRecords().forEach(user -> user.setPassword(null));

            log.info("查询用户列表成功，页码：{}，大小：{}，总数：{}", page, size, userPage.getTotal());
            return Result.success(userPage);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return Result.error("查询用户列表失败");
        }
    }

    /**
     * 获取用户详情
     */
    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public Result<User> getUserDetail(@ApiParam("用户ID") @PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 清除密码信息
            user.setPassword(null);

            log.info("获取用户详情成功，用户ID：{}", id);
            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            return Result.error("获取用户详情失败");
        }
    }

    /**
     * 更新用户状态
     */
    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateUserStatus(
            @ApiParam("用户ID") @PathVariable Long id,
            @ApiParam("状态信息") @RequestBody Map<String, Object> request) {
        try {
            Integer status = (Integer) request.get("status");
            if (status == null || (status != 0 && status != 1)) {
                return Result.error("状态参数无效");
            }

            User user = userService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            user.setStatus(status);
            user.setUpdateTime(LocalDateTime.now());
            
            boolean result = userService.updateById(user);
            if (result) {
                String action = status == 1 ? "启用" : "禁用";
                log.info("{}用户成功，用户ID：{}，手机号：{}", action, id, user.getPhone());
                return Result.success(true, action + "成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
            return Result.error("更新用户状态失败");
        }
    }

    /**
     * 获取用户统计信息
     */
    @ApiOperation("获取用户统计信息")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 总用户数
            long totalUsers = userService.count();
            statistics.put("totalUsers", totalUsers);
            
            // 正常用户数
            long activeUsers = userService.lambdaQuery().eq(User::getStatus, 1).count();
            statistics.put("activeUsers", activeUsers);
            
            // 禁用用户数
            long inactiveUsers = userService.lambdaQuery().eq(User::getStatus, 0).count();
            statistics.put("inactiveUsers", inactiveUsers);
            
            // 今日新增用户
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            long todayNewUsers = userService.lambdaQuery()
                    .ge(User::getCreateTime, todayStart)
                    .count();
            statistics.put("todayNewUsers", todayNewUsers);

            log.info("获取用户统计信息成功");
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            return Result.error("获取用户统计信息失败");
        }
    }

    /**
     * 重置用户密码
     */
    @ApiOperation("重置用户密码")
    @PostMapping("/{id}/reset-password")
    public Result<Boolean> resetUserPassword(@ApiParam("用户ID") @PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 这里可以生成一个默认密码或随机密码
            // 实际项目中可能需要发送短信通知用户新密码
            String defaultPassword = "123456"; // 简单示例，实际应该更安全
            // TODO: 实现密码加密和短信通知
            
            log.info("重置用户密码功能需要完善，用户ID：{}", id);
            return Result.success(true, "重置密码功能开发中");
        } catch (Exception e) {
            log.error("重置用户密码失败", e);
            return Result.error("重置用户密码失败");
        }
    }
}
