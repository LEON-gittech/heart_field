package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Message;
import com.example.heart_field.mapper.MessageMapper;
import com.example.heart_field.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:24 AM
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
