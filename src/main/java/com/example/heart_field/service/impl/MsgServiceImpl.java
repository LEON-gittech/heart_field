package com.example.heart_field.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.MsgProperites;
import com.example.heart_field.entity.MsgTemplateParam;
import com.example.heart_field.mapper.MsgPropertiesMapper;
import com.example.heart_field.service.MsgService;
import com.example.heart_field.utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MsgServiceImpl extends ServiceImpl<MsgPropertiesMapper,MsgProperites> implements MsgService {
    public static final String PREFIX_REDIS_KEY = "msg:phone:";
    @Resource
    MsgProperites msgProperites;
    @Resource
    IAcsClient client;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    ObjectMapper objectMapper;

    /**
     * @param phone
     * @return
     */
    @Override
    public String sendMsg(String phone) {
        String number = RandomUtil.randomNumbers(6);
        System.out.println("验证码为：" + number);
        MsgTemplateParam msgTemplateParam = new MsgTemplateParam();
        msgTemplateParam.setCode(number);
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setSignName(msgProperites.getSignName());
        request.setTemplateCode(msgProperites.getTemplateCode());
        try {
            request.setTemplateParam(objectMapper.writeValueAsString(msgTemplateParam));
            SendSmsResponse response = client.getAcsResponse(request);
            if ("OK".equals(response.getCode())) {
                redisUtil.set(PREFIX_REDIS_KEY + phone, number);
                System.out.println("发送成功");
            }
            else {
                System.out.println(response.getMessage());
            }
        } catch (ClientException | JsonProcessingException e) {
            System.out.println("发送失败");
        }
        return "success";
    }

}

