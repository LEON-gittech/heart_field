package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.RecordListDTO;
import com.example.heart_field.entity.Record;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:30 AM
 */
public interface RecordService extends IService<Record> {
    ResultInfo<List<RecordListDTO>> getRecords(String visitorId, String state);
}
