package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.service.ConsultantService;
import org.springframework.stereotype.Service;

@Service
public class ConsultantServiceImpl extends ServiceImpl<ConsultantMapper, Consultant> implements ConsultantService {
}
