package com.qr.ordering.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qr.ordering.common.Result;
import com.qr.ordering.entity.FileEntity;
import com.qr.ordering.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件管理控制器（管理端）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/file")
@CrossOrigin
@Api(tags = "文件管理（管理端）")
public class FileManagementController {

    @Autowired
    private FileService fileService;

    /**
     * 分页查询文件列表
     */
    @ApiOperation("分页查询文件列表")
    @GetMapping("/list")
    public Result<Page<FileEntity>> getFileList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("文件分类") @RequestParam(required = false) String category,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        try {
            Page<FileEntity> filePage = fileService.getFileList(page, size, category, keyword);
            log.info("查询文件列表成功，页码：{}，大小：{}，总数：{}", page, size, filePage.getTotal());
            return Result.success(filePage);
        } catch (Exception e) {
            log.error("查询文件列表失败", e);
            return Result.error("查询文件列表失败");
        }
    }

    /**
     * 上传文件
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public Result<FileEntity> uploadFile(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("文件分类") @RequestParam(defaultValue = "general") String category) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 文件大小限制（例如：10MB）
            long maxSize = 10 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过10MB");
            }

            // 这里应该从JWT token中获取管理员ID，简化处理
            Long adminId = 1L; // TODO: 从token获取真实的管理员ID
            Integer uploaderType = 2; // 管理员上传

            FileEntity fileEntity = fileService.uploadFile(file, category, adminId, uploaderType);
            log.info("文件上传成功：{}", fileEntity.getOriginalName());
            return Result.success(fileEntity, "文件上传成功");
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件详情
     */
    @ApiOperation("获取文件详情")
    @GetMapping("/{id}")
    public Result<FileEntity> getFileDetail(@ApiParam("文件ID") @PathVariable Long id) {
        try {
            FileEntity fileEntity = fileService.getById(id);
            if (fileEntity == null || fileEntity.getStatus() == 0) {
                return Result.error("文件不存在");
            }

            log.info("获取文件详情成功，文件ID：{}", id);
            return Result.success(fileEntity);
        } catch (Exception e) {
            log.error("获取文件详情失败", e);
            return Result.error("获取文件详情失败");
        }
    }

    /**
     * 删除文件
     */
    @ApiOperation("删除文件")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteFile(@ApiParam("文件ID") @PathVariable Long id) {
        try {
            FileEntity fileEntity = fileService.getById(id);
            if (fileEntity == null) {
                return Result.error("文件不存在");
            }

            boolean result = fileService.deleteFile(id);
            if (result) {
                log.info("删除文件成功，文件名：{}", fileEntity.getOriginalName());
                return Result.success(true, "删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return Result.error("删除文件失败");
        }
    }

    /**
     * 批量删除文件
     */
    @ApiOperation("批量删除文件")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteFiles(@ApiParam("文件ID数组") @RequestBody Long[] fileIds) {
        try {
            if (fileIds == null || fileIds.length == 0) {
                return Result.error("请选择要删除的文件");
            }

            boolean result = fileService.batchDeleteFiles(fileIds);
            if (result) {
                log.info("批量删除文件成功，删除数量：{}", fileIds.length);
                return Result.success(true, "批量删除成功");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            return Result.error("批量删除文件失败");
        }
    }

    /**
     * 获取文件统计信息
     */
    @ApiOperation("获取文件统计信息")
    @GetMapping("/statistics")
    public Result<FileService.FileStatistics> getFileStatistics() {
        try {
            FileService.FileStatistics statistics = fileService.getFileStatistics();
            log.info("获取文件统计信息成功");
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取文件统计信息失败", e);
            return Result.error("获取文件统计信息失败");
        }
    }

    /**
     * 下载文件
     */
    @ApiOperation("下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@ApiParam("文件ID") @PathVariable Long id) {
        try {
            // 获取文件信息
            FileEntity fileEntity = fileService.getById(id);
            if (fileEntity == null || fileEntity.getStatus() == 0) {
                log.error("文件不存在，ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // 从fileUrl中提取相对路径
            // fileUrl格式: http://localhost:8080/uploads/avatar/2025/08/27/xxx.jpg
            String fileUrl = fileEntity.getFileUrl();
            String relativePath = "";
            
            if (fileUrl.contains("/uploads/")) {
                relativePath = fileUrl.substring(fileUrl.indexOf("/uploads/") + "/uploads/".length());
            } else {
                log.error("无效的文件URL格式: {}", fileUrl);
                return ResponseEntity.notFound().build();
            }
            
            // 构建物理文件路径
            String uploadPath = System.getProperty("user.dir") + "/uploads/";
            Path filePath = Paths.get(uploadPath + relativePath);
            
            log.info("尝试访问文件: {}", filePath.toString());
            
            // 检查文件是否存在
            if (!Files.exists(filePath)) {
                log.error("物理文件不存在，路径: {}", filePath);
                return ResponseEntity.notFound().build();
            }

            // 读取文件内容
            byte[] fileContent = Files.readAllBytes(filePath);
            
            // 设置响应头
            String encodedFileName = URLEncoder.encode(fileEntity.getOriginalName(), StandardCharsets.UTF_8.toString());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fileEntity.getMimeType()));
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentLength(fileContent.length);
            
            log.info("文件下载成功，文件名：{}, 大小：{} bytes", fileEntity.getOriginalName(), fileContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
                    
        } catch (Exception e) {
            log.error("文件下载失败，ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
