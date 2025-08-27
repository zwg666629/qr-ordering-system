package com.qr.ordering.util;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类 (临时使用MD5，生产环境需要使用BCrypt)
 */
@Component
public class PasswordUtil {

    /**
     * 加密密码 (临时使用MD5)
     */
    public static String encode(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 如果MD5也不可用，直接返回原始密码（仅开发环境）
            return rawPassword;
        }
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        // 先尝试MD5匹配
        String encoded = encode(rawPassword);
        if (encoded.equals(encodedPassword)) {
            return true;
        }
        
        // 如果MD5不匹配，尝试明文匹配（兼容旧数据）
        return rawPassword.equals(encodedPassword);
    }
}