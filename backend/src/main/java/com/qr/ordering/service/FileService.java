package com.qr.ordering.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qr.ordering.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService extends IService<FileEntity> {

    /**
     * 上传文件
     */
    FileEntity uploadFile(MultipartFile file, String category, Long uploaderId, Integer uploaderType);

    /**
     * 分页查询文件列表
     */
    Page<FileEntity> getFileList(int page, int size, String category, String keyword);

    /**
     * 删除文件（逻辑删除）
     */
    boolean deleteFile(Long fileId);

    /**
     * 批量删除文件
     */
    boolean batchDeleteFiles(Long[] fileIds);

    /**
     * 获取文件统计信息
     */
    FileStatistics getFileStatistics();

    /**
     * 文件统计信息内部类
     */
    class FileStatistics {
        private Long totalFiles;
        private Long totalSize;
        private Long avatarCount;
        private Long dishCount;
        private Long generalCount;

        // getters and setters
        public Long getTotalFiles() { return totalFiles; }
        public void setTotalFiles(Long totalFiles) { this.totalFiles = totalFiles; }
        public Long getTotalSize() { return totalSize; }
        public void setTotalSize(Long totalSize) { this.totalSize = totalSize; }
        public Long getAvatarCount() { return avatarCount; }
        public void setAvatarCount(Long avatarCount) { this.avatarCount = avatarCount; }
        public Long getDishCount() { return dishCount; }
        public void setDishCount(Long dishCount) { this.dishCount = dishCount; }
        public Long getGeneralCount() { return generalCount; }
        public void setGeneralCount(Long generalCount) { this.generalCount = generalCount; }
    }
}
