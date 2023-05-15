package com.example.heart_field.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.StaffStat;
import com.example.heart_field.service.StaffStatService;
import com.example.heart_field.mapper.StaffStatMapper;
import com.example.heart_field.service.StaffStatService;
import org.springframework.stereotype.Service;

@Service
public class StaffStatServiceImpl extends ServiceImpl<StaffStatMapper, StaffStat> implements StaffStatService {
}
