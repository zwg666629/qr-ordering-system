package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
import com.qr.ordering.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器（用户端）
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 上传文件到avatar目录
            String fileUrl = fileUploadUtil.uploadFile(file, "avatar");
            
            log.info("头像上传成功: {}", fileUrl);
            return Result.success(fileUrl, "头像上传成功");
            
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传菜品图片
     */
    @PostMapping("/upload/dish")
    public Result<String> uploadDishImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 上传文件到dish目录
            String fileUrl = fileUploadUtil.uploadFile(file, "dish");
            
            log.info("菜品图片上传成功: {}", fileUrl);
            return Result.success(fileUrl, "菜品图片上传成功");
            
        } catch (Exception e) {
            log.error("菜品图片上传失败", e);
            return Result.error("菜品图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 通用文件上传
     */
    @PostMapping("/upload/{category}")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable("category") String category) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 上传文件
            String fileUrl = fileUploadUtil.uploadFile(file, category);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("originalName", file.getOriginalFilename());
            
            log.info("文件上传成功: {}", fileUrl);
            return Result.success(result, "文件上传成功");
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}