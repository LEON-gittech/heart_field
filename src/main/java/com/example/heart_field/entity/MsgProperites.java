package com.example.heart_field.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("aliyun.msg")
public class MsgProperites {
    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
}
