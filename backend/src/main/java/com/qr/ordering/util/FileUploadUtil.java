package com.qr.ordering.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Value("${app.upload.path:./uploads}")
    private String uploadPath;

    @Value("${app.upload.url:http://localhost:8080/uploads}")
    private String uploadUrl;

    private static final String[] ALLOWED_EXTENSIONS = {
        ".jpg", ".jpeg", ".png", ".gif", ".bmp", 
        ".webp", ".svg", ".tiff", ".tif", ".ico", 
        ".avif", ".heic", ".heif", ".raw", ".dng"
    };
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // 验证文件
        validateFile(file);

        // 创建上传目录
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 确保使用绝对路径
        String actualUploadPath = uploadPath;
        if (!uploadPath.startsWith("/") && !uploadPath.matches("^[A-Za-z]:[/\\\\].*")) {
            // 相对路径转绝对路径
            actualUploadPath = System.getProperty("user.dir") + "/" + uploadPath;
        }
        
        String fullPath = actualUploadPath + "/" + folder + "/" + datePath;
        log.info("准备创建目录: {}", fullPath);
        
        File uploadDir = new File(fullPath);
        if (!uploadDir.exists()) {
            boolean mkdirResult = uploadDir.mkdirs();
            if (!mkdirResult) {
                log.error("创建目录失败: {}", fullPath);
                throw new IOException("无法创建上传目录: " + fullPath);
            }
            log.info("创建目录成功: {}", fullPath);
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        File targetFile = new File(uploadDir, filename);
        log.info("准备保存文件到: {}", targetFile.getAbsolutePath());
        
        try {
            file.transferTo(targetFile);
            log.info("文件保存成功: {}", targetFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage());
            throw new IOException("文件保存失败: " + e.getMessage());
        }

        // 返回访问URL
        String fileUrl = uploadUrl + "/" + folder + "/" + datePath + "/" + filename;
        log.info("文件上传成功，访问URL: {}", fileUrl);

        return fileUrl;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        boolean isAllowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new RuntimeException("不支持的文件格式，仅支持图片文件");
        }
    }
}