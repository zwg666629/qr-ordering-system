package com.qr.ordering.controller;

import com.qr.ordering.common.Result;
// import io.swagger.annotations.*;
// import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
// @Api
@CrossOrigin
public class UploadController {
    
    @Value("${app.upload.path:/tmp/qr-ordering/uploads}")
    private String uploadPath;
    
    @Value("${app.upload.url:http://localhost:8080/uploads}")
    private String uploadUrl;
    
    @PostMapping
    // @ApiOperation
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        
        try {
            // 检查文件类型 - 使用文件扩展名验证
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名不能为空");
            }
            
            String extension = "";
            if (originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }
            
            // 支持的图片格式
            String[] allowedExtensions = {
                ".jpg", ".jpeg", ".png", ".gif", ".bmp", 
                ".webp", ".svg", ".tiff", ".tif", ".ico", 
                ".avif", ".heic", ".heif", ".raw", ".dng"
            };
            
            boolean isValidImage = false;
            for (String allowedExt : allowedExtensions) {
                if (allowedExt.equals(extension)) {
                    isValidImage = true;
                    break;
                }
            }
            
            if (!isValidImage) {
                return Result.error("不支持的文件格式，仅支持图片文件");
            }
            
            // 检查文件大小（限制�?MB�?
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小不能超过5MB");
            }
            
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名 (originalFilename 已在上面处理过)
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            
            // 构造返回URL
            String fileUrl = uploadUrl + "/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("originalName", originalFilename);
            
            log.info("文件上传成功: {} -> {}", originalFilename, fileUrl);
            
            return Result.success(result);
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/avatar")
    // @ApiOperation
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 头像上传的特殊处理，可以有不同的大小限制�?
        return uploadImage(file);
    }
}













