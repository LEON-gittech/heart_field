package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.entity.Chat;
import com.example.heart_field.param.ChatParam;

import java.util.List;

/**
 * 聊天记录表(Chat)表服务接口
 *
 * @author makejava
 * @since 2023-05-15 16:53:39
 */
public interface ChatService extends IService<Chat> {

    Integer getTotalCounselToday();

    Integer getTotalDurationToday();


    List<Integer> getWeekCounsels();

    List<Integer> getTodayCounsels();

    ResultInfo createChat(ChatParam chat);

    ResultInfo endChat(Integer chatId);
}

