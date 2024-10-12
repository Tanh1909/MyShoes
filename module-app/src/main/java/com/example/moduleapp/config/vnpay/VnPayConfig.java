package com.example.moduleapp.config.vnpay;

import com.example.common.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class VnPayConfig {
    private final VNPayConfigProperties vnPayConfigProperties;
    private final String VNPAY_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

    @Bean("vnpayConfig")
    public Map<String, String> getVnPayConfig() {
        Map<String, String> map = new HashMap<>();
        map.put("vnp_Version", vnPayConfigProperties.getVersion());
        map.put("vnp_Command", vnPayConfigProperties.getCommand());
        map.put("vnp_TmnCode", vnPayConfigProperties.getTmnCode());
        map.put("vnp_CurrCode", "VND");
        map.put("vnp_OrderType", vnPayConfigProperties.getOrderType());
        map.put("vnp_Locale", "vn");
        map.put("vnp_ReturnUrl", vnPayConfigProperties.getReturnUrl());
        map.put("vnp_SecretKey", vnPayConfigProperties.getHashSecret());
        map.put("vnp_CreateDate", TimeUtils.getFormat(LocalDateTime.now(), VNPAY_DATE_TIME_FORMAT));
        map.put("vnp_ExpireDate", TimeUtils.getFormat(LocalDateTime.now().plusMinutes(vnPayConfigProperties.getExpirationTime() / (60 * 1000)), VNPAY_DATE_TIME_FORMAT));
        return map;
    }
}
