package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.service.SupervisorService;
import org.springframework.stereotype.Service;

@Service
public class SupervisorServiceImpl extends ServiceImpl<SupervisorMapper, Supervisor> implements SupervisorService {
}
