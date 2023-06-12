package com.example.heart_field.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author albac0020@gmail.com
 * data 2023/6/11 7:03 PM
 */

@Data
@Component
@ConfigurationProperties(prefix = "tencent.cos")
public class TencentCOSproperties {
    private String rootSrc;
    private String bucketAddr;
    private String SecretId;
    private String SecretKey;
    private String bucketName;
}


