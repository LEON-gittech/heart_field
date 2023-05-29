package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.consultant.record.RecordDTO;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import com.example.heart_field.entity.Record;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:30 AM
 */
public interface RecordService extends IService<Record> {
    List<RecordListDTO> getRecords(String visitorId, String state,Integer pageSize,Integer pageNum);

    List<RecordDTO> queryRecords(String searchValue, int pageSize, int pageNum, LocalDateTime fromDate, LocalDateTime toDate);

    ResultInfo addRecordByChatId(Integer chatId);

    ResultInfo addComment(Integer recordId, String comment, Integer score);
}
