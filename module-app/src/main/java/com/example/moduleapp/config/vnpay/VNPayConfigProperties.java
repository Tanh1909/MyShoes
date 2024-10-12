package com.example.moduleapp.config.vnpay;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "vnpay")
public class VNPayConfigProperties {
    private String tmnCode;
    private String hashSecret;
    private String url;
    private String version;
    private String command;
    private String orderType;
    private String returnUrl;
    private Long expirationTime;

}
