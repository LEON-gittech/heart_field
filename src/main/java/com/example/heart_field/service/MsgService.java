package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.entity.MsgProperites;

public interface MsgService extends IService<MsgProperites> {
    String sendMsg(String phone);
}
