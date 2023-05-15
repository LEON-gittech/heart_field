package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.entity.Record;
import com.example.heart_field.mapper.RecordMapper;
import com.example.heart_field.service.RecordService;
import org.springframework.stereotype.Service;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:31 AM
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
}
