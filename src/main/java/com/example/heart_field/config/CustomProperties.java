package com.example.heart_field.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tencent-im")
public class CustomProperties {
    private Long SDKAppID;
    private String SecretKey;
    private Integer expireTime;
}

