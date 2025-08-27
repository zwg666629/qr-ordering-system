package com.qr.ordering.config;

import com.qr.ordering.interceptor.UserAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.path:./uploads}")
    private String uploadPath;

    @Autowired
    private UserAuthInterceptor userAuthInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保使用绝对路径
        String actualUploadPath = uploadPath;
        if (!uploadPath.startsWith("/") && !uploadPath.matches("^[A-Za-z]:[/\\\\].*")) {
            // 相对路径转绝对路径
            actualUploadPath = System.getProperty("user.dir") + "/" + uploadPath;
        }
        
        // 配置静态资源访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + actualUploadPath + "/");
                
        System.out.println("配置静态资源路径: file:" + actualUploadPath + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置用户认证拦截器
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/api/user/info", "/api/user/password", "/api/file/upload/**")
                .excludePathPatterns("/api/user/register", "/api/user/login", "/api/user/sms/send");
    }
}