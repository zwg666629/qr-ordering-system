package com.qr.ordering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的健康检查控制器
 */
@RestController
public class SimpleHealthController {

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "QR Ordering System API");
        result.put("version", "1.0.0");
        result.put("status", "running");
        result.put("message", "Welcome to QR Ordering System Backend");
        return result;
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "qr-ordering-backend");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}