package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Waiting;
import com.example.heart_field.mapper.WaitingMapper;
import com.example.heart_field.service.WaitingService;
import org.springframework.stereotype.Service;

@Service
public class WaitingServiceImpl extends ServiceImpl<WaitingMapper, Waiting> implements WaitingService {
}
