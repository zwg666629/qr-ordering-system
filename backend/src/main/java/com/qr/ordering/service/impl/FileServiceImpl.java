package com.qr.ordering.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qr.ordering.entity.FileEntity;
import com.qr.ordering.mapper.FileEntityMapper;
import com.qr.ordering.service.FileService;
import com.qr.ordering.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileEntityMapper, FileEntity> implements FileService {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileEntity uploadFile(MultipartFile file, String category, Long uploaderId, Integer uploaderType) {
        try {
            // 上传文件
            String fileUrl = fileUploadUtil.uploadFile(file, category);
            
            // 创建文件记录
            FileEntity fileEntity = new FileEntity();
            fileEntity.setOriginalName(file.getOriginalFilename());
            fileEntity.setFileName(extractFileName(fileUrl));
            fileEntity.setFilePath(fileUrl);
            fileEntity.setFileUrl(fileUrl);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileType(getFileExtension(file.getOriginalFilename()));
            fileEntity.setMimeType(file.getContentType());
            fileEntity.setCategory(category);
            fileEntity.setUploaderId(uploaderId);
            fileEntity.setUploaderType(uploaderType);
            fileEntity.setStatus(1);
            fileEntity.setCreateTime(LocalDateTime.now());
            fileEntity.setUpdateTime(LocalDateTime.now());

            // 保存到数据库
            this.save(fileEntity);
            
            log.info("文件上传成功: {}", fileEntity.getOriginalName());
            return fileEntity;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public Page<FileEntity> getFileList(int page, int size, String category, String keyword) {
        Page<FileEntity> pageObj = new Page<>(page, size);
        return baseMapper.selectPageWithCondition(pageObj, category, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFile(Long fileId) {
        try {
            FileEntity fileEntity = this.getById(fileId);
            if (fileEntity == null) {
                return false;
            }
            
            // 逻辑删除
            fileEntity.setStatus(0);
            fileEntity.setUpdateTime(LocalDateTime.now());
            
            boolean result = this.updateById(fileEntity);
            if (result) {
                log.info("文件删除成功: {}", fileEntity.getOriginalName());
            }
            return result;
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteFiles(Long[] fileIds) {
        try {
            return Arrays.stream(fileIds)
                    .allMatch(this::deleteFile);
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            return false;
        }
    }

    @Override
    public FileStatistics getFileStatistics() {
        try {
            FileStatistics stats = new FileStatistics();
            
            // 总文件数
            stats.setTotalFiles(this.lambdaQuery().eq(FileEntity::getStatus, 1).count());
            
            // 总大小
            stats.setTotalSize(baseMapper.getTotalFileSize());
            
            // 分类统计
            stats.setAvatarCount(baseMapper.countByCategory("avatar"));
            stats.setDishCount(baseMapper.countByCategory("dish"));
            stats.setGeneralCount(baseMapper.countByCategory("general"));
            
            return stats;
        } catch (Exception e) {
            log.error("获取文件统计信息失败", e);
            return new FileStatistics();
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFileName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return "";
        }
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf(".");
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }
}
