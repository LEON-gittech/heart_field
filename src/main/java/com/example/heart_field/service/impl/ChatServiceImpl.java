package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Chat;
import com.example.heart_field.mapper.ChatMapper;
import com.example.heart_field.service.ChatService;
import org.springframework.stereotype.Service;

/**
 * 聊天记录表(Chat)表服务实现类
 *
 * @author makejava
 * @since 2023-05-15 16:53:39
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

}

