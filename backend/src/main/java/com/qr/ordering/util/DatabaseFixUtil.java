package com.qr.ordering.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库修复工具
 * 用于修复数据库表结构不匹配的问题
 */
@Slf4j
@Component
public class DatabaseFixUtil implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            fixOrdersTable();
            fixUserTable();
            fixSmsCodeTable();
            fixUserLoginLogTable();
            fixFilesTable();
        } catch (Exception e) {
            log.warn("数据库修复检查：{}", e.getMessage());
        }
    }

    /**
     * 修复orders表：添加user_id和table_number字段
     */
    private void fixOrdersTable() {
        try {
            // 检查user_id字段是否存在
            String checkColumnSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'user_id'";
            
            Integer columnExists = jdbcTemplate.queryForObject(checkColumnSql, Integer.class);
            
            if (columnExists == 0) {
                log.info("检测到orders表缺少user_id字段，开始自动修复...");
                
                // 添加user_id字段
                String addColumnSql = "ALTER TABLE orders ADD COLUMN user_id BIGINT NULL " +
                        "COMMENT '用户ID（可选，游客下单时为空）' AFTER table_id";
                
                jdbcTemplate.execute(addColumnSql);
                
                log.info("✅ orders表user_id字段添加成功！");
            } else {
                log.debug("orders表user_id字段已存在，无需修复");
            }
            
            // 检查table_number字段是否存在
            String checkTableNumberSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'table_number'";
            
            Integer tableNumberExists = jdbcTemplate.queryForObject(checkTableNumberSql, Integer.class);
            
            if (tableNumberExists == 0) {
                log.info("检测到orders表缺少table_number字段，开始自动修复...");
                
                // 添加table_number字段
                String addTableNumberSql = "ALTER TABLE orders ADD COLUMN table_number VARCHAR(10) NULL " +
                        "COMMENT '餐桌号' AFTER table_id";
                
                jdbcTemplate.execute(addTableNumberSql);
                
                log.info("✅ orders表table_number字段添加成功！");
            } else {
                log.debug("orders表table_number字段已存在，无需修复");
            }
            
        } catch (Exception e) {
            log.error("修复orders表失败: {}", e.getMessage());
            throw new RuntimeException("数据库表结构修复失败", e);
        }
    }

    /**
     * 检查并创建user表
     */
    private void fixUserTable() {
        try {
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.TABLES " +
                    "WHERE table_schema = DATABASE() AND table_name = 'user'";
            
            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            
            if (tableExists == 0) {
                log.info("检测到user表不存在，开始创建...");
                
                String createTableSql = "CREATE TABLE user (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号'," +
                        "password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)'," +
                        "nickname VARCHAR(50) COMMENT '昵称'," +
                        "avatar VARCHAR(500) COMMENT '头像URL'," +
                        "gender TINYINT DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女'," +
                        "birthday DATE COMMENT '生日'," +
                        "status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-禁用'," +
                        "last_login_time TIMESTAMP NULL COMMENT '最后登录时间'," +
                        "last_login_ip VARCHAR(50) COMMENT '最后登录IP'," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                        "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                        "UNIQUE KEY uk_phone (phone)," +
                        "INDEX idx_status (status)," +
                        "INDEX idx_create_time (create_time)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'";
                
                jdbcTemplate.execute(createTableSql);
                log.info("✅ user表创建成功！");
            } else {
                log.debug("user表已存在，无需创建");
            }
        } catch (Exception e) {
            log.error("创建user表失败: {}", e.getMessage());
            throw new RuntimeException("数据库表结构修复失败", e);
        }
    }

    /**
     * 检查并创建sms_code表
     */
    private void fixSmsCodeTable() {
        try {
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.TABLES " +
                    "WHERE table_schema = DATABASE() AND table_name = 'sms_code'";
            
            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            
            if (tableExists == 0) {
                log.info("检测到sms_code表不存在，开始创建...");
                
                String createTableSql = "CREATE TABLE sms_code (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "phone VARCHAR(20) NOT NULL COMMENT '手机号'," +
                        "code VARCHAR(10) NOT NULL COMMENT '验证码'," +
                        "type TINYINT NOT NULL COMMENT '类型 1-注册 2-登录 3-重置密码'," +
                        "used TINYINT DEFAULT 0 COMMENT '是否已使用 0-未使用 1-已使用'," +
                        "expire_time TIMESTAMP NOT NULL COMMENT '过期时间'," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                        "use_time TIMESTAMP NULL COMMENT '使用时间'," +
                        "INDEX idx_phone (phone)," +
                        "INDEX idx_code (code)," +
                        "INDEX idx_expire_time (expire_time)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信验证码表'";
                
                jdbcTemplate.execute(createTableSql);
                log.info("✅ sms_code表创建成功！");
            } else {
                log.debug("sms_code表已存在，无需创建");
            }
        } catch (Exception e) {
            log.error("创建sms_code表失败: {}", e.getMessage());
            throw new RuntimeException("数据库表结构修复失败", e);
        }
    }

    /**
     * 检查并创建user_login_log表
     */
    private void fixUserLoginLogTable() {
        try {
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.TABLES " +
                    "WHERE table_schema = DATABASE() AND table_name = 'user_login_log'";
            
            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            
            if (tableExists == 0) {
                log.info("检测到user_login_log表不存在，开始创建...");
                
                String createTableSql = "CREATE TABLE user_login_log (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "user_id BIGINT NOT NULL COMMENT '用户ID'," +
                        "login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'," +
                        "login_ip VARCHAR(50) COMMENT '登录IP'," +
                        "device_info VARCHAR(500) COMMENT '设备信息'," +
                        "login_type TINYINT DEFAULT 1 COMMENT '登录类型 1-注册登录 2-正常登录'," +
                        "login_status TINYINT DEFAULT 1 COMMENT '登录状态 1-成功 0-失败'," +
                        "fail_reason VARCHAR(200) COMMENT '失败原因'," +
                        "INDEX idx_user_id (user_id)," +
                        "INDEX idx_login_time (login_time)," +
                        "INDEX idx_login_status (login_status)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表'";
                
                jdbcTemplate.execute(createTableSql);
                log.info("✅ user_login_log表创建成功！");
            } else {
                log.debug("user_login_log表已存在，无需创建");
            }
        } catch (Exception e) {
            log.error("创建user_login_log表失败: {}", e.getMessage());
            throw new RuntimeException("数据库表结构修复失败", e);
        }
    }

    /**
     * 检查并创建files表
     */
    private void fixFilesTable() {
        try {
            String checkTableSql = "SELECT COUNT(*) FROM information_schema.TABLES " +
                    "WHERE table_schema = DATABASE() AND table_name = 'files'";
            
            Integer tableExists = jdbcTemplate.queryForObject(checkTableSql, Integer.class);
            
            if (tableExists == 0) {
                log.info("检测到files表不存在，开始创建...");
                
                String createTableSql = "CREATE TABLE files (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "original_name VARCHAR(255) NOT NULL COMMENT '原始文件名'," +
                        "file_name VARCHAR(255) NOT NULL COMMENT '存储文件名'," +
                        "file_path VARCHAR(500) NOT NULL COMMENT '文件路径'," +
                        "file_url VARCHAR(500) NOT NULL COMMENT '访问URL'," +
                        "file_size BIGINT NOT NULL COMMENT '文件大小(字节)'," +
                        "file_type VARCHAR(100) NOT NULL COMMENT '文件类型'," +
                        "mime_type VARCHAR(100) NOT NULL COMMENT 'MIME类型'," +
                        "category VARCHAR(50) DEFAULT 'general' COMMENT '文件分类 avatar-头像 dish-菜品 general-通用'," +
                        "uploader_id BIGINT COMMENT '上传者ID'," +
                        "uploader_type TINYINT DEFAULT 1 COMMENT '上传者类型 1-用户 2-管理员'," +
                        "status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-删除'," +
                        "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                        "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                        "INDEX idx_category (category)," +
                        "INDEX idx_uploader (uploader_id, uploader_type)," +
                        "INDEX idx_status (status)," +
                        "INDEX idx_create_time (create_time)" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件管理表'";
                
                jdbcTemplate.execute(createTableSql);
                log.info("✅ files表创建成功！");
            } else {
                log.debug("files表已存在，无需创建");
            }
        } catch (Exception e) {
            log.error("创建files表失败: {}", e.getMessage());
            throw new RuntimeException("数据库表结构修复失败", e);
        }
    }
}
