package com.example.heart_field.dto.consultant.record;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/19 10:03 AM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordListDTO {
    //该记录id
    private Integer id;

    //访客信息
    private Integer visitorId;
    private String visitorName;
    private String visitorAvatar;

    //咨询师信息
    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;

    //督导信息
    private Integer supervisorId;
    private String supervisorName;
    private String supervisorAvatar;

    //咨询开始和结束时间
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;

    //咨询是否完成，0表示未完成，1表示已完成
    private Integer isCompleted;

    //访客对咨询师的评价
    private Integer visitorScore;
    private String visitorComment;

    //对应的聊天id和求助会话id
    private Integer chatId;
    private Integer helpId;




}
