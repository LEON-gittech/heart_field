package com.example.heart_field.service.impl.impl;


import com.example.heart_field.entity.StaffStat;
import com.example.heart_field.entity.StaffStatService;
import com.example.heart_field.mapper.StaffStatMapper;
import org.springframework.stereotype.Service;

@Service
public class StaffStatServiceImpl extends ServiceImpl<StaffStatMapper, StaffStat> implements StaffStatService {
}
