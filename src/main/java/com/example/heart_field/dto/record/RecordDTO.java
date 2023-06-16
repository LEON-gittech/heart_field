package com.example.heart_field.dto.record;

import com.example.heart_field.entity.Record;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/28 11:20 AM
 */
@Data
@Builder
public class RecordDTO {
    private Integer id;

    private Integer visitorId;
    private String visitorName;
    private String visitorAvatar;

    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar=null;

    private LocalDateTime startTime;
    private Integer continueTime;//持续时长

    private Integer chatId;

    private Integer consultRank;//用户对咨询师的评分
    private String consultComment;//用户对咨询师的评价

    private Integer visitorCompleted;//访客是否完成咨询
    private Integer consultantCompleted;
}
