package com.qr.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qr.ordering.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文件Mapper接口
 */
@Mapper
public interface FileEntityMapper extends BaseMapper<FileEntity> {

    /**
     * 分页查询文件列表
     */
    @Select("<script>" +
            "SELECT * FROM files WHERE status = 1" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (original_name LIKE CONCAT('%', #{keyword}, '%') OR file_type LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    Page<FileEntity> selectPageWithCondition(Page<FileEntity> page, 
                                           @Param("category") String category, 
                                           @Param("keyword") String keyword);

    /**
     * 根据分类统计文件数量
     */
    @Select("SELECT COUNT(*) FROM files WHERE status = 1 AND category = #{category}")
    Long countByCategory(@Param("category") String category);

    /**
     * 统计文件总大小
     */
    @Select("SELECT COALESCE(SUM(file_size), 0) FROM files WHERE status = 1")
    Long getTotalFileSize();
}
