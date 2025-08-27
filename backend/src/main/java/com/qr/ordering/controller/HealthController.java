package com.qr.ordering.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Api(tags = "健康检查")
public class HealthController {

    @GetMapping("/health")
    @ApiOperation("健康检查接口")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "Service is running");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
    
    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "QR Ordering System API");
        result.put("version", "1.0.0");
        result.put("status", "running");
        return result;
    }
}