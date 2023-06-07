package com.example.heart_field.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.example.heart_field.entity.MsgProperites;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class AliyunMsgConfig {
    @Resource
    MsgProperites msgProperites;

    @Bean
    public IAcsClient iAcsClient() throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(msgProperites.getRegionId(), msgProperites.getAccessKeyId(), msgProperites.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        return client;
    }
}
