package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.mapper.AdminMapper;
import com.example.heart_field.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
