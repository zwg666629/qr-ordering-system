package com.qr.ordering.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 */
@Slf4j
@Component
public class QrCodeUtil {
    
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String FORMAT = "PNG";
    
    /**
     * 生成二维码图片并返回Base64编码
     */
    public String generateQrCodeBase64(String content) {
        return generateQrCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    /**
     * 生成指定尺寸的二维码图片并返回Base64编码
     */
    public String generateQrCodeBase64(String content, int width, int height) {
        try {
            // 设置二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            
            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            
            // 创建BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // 将BitMatrix转换为图片
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            
            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, FORMAT, baos);
            byte[] imageBytes = baos.toByteArray();
            
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
            
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成二维码失败", e);
        }
    }
    
    /**
     * 生成餐桌二维码内容
     */
    public String generateTableQrContent(String tableNumber, String baseUrl) {
        // 这里可以根据实际需求构造二维码内容
        // 例如：前端页面URL + 餐桌参数
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:3003"; // 默认前端地址
        }
        return baseUrl + "/menu?table=" + tableNumber;
    }
    
    /**
     * 生成带Logo的二维码（可选功能）
     */
    public String generateQrCodeWithLogo(String content, String logoPath) {
        // TODO: 实现带Logo的二维码生成
        // 这里可以添加Logo嵌入功能
        return generateQrCodeBase64(content);
    }
}













