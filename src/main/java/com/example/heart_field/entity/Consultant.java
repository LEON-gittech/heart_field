package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.heart_field.annotation.Phone;
import com.example.heart_field.dto.consultant.ConsultantDto;
import com.example.heart_field.dto.consultant.ExpertiseTag;
import com.example.heart_field.service.ConsultantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Consultant {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private boolean isOnline = false; // 默认值为false
    private boolean isDisabled = false; // 默认值为false
    private boolean isValid = false; // 默认值为false
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime ; // 默认值为当前时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime ; // 默认值为当前时间
    private String name;
    private Integer age;
    private Integer gender = 2; // 默认值为2
    private String password;
    private String avatar;
    private Integer maxConcurrent = 2; // 默认值为2
    private Integer maxNum = 20; // 默认值为20
    private Integer currentSessionCount;
    private Integer curStatus = 0; // 默认值为0
    private Integer helpNum = 0; // 默认值为0
    private Integer helpTotalNum = 0;
    private Integer totalHelpTime;
    private Integer todayTotalHelpTime;
    private Integer todayTotalHelpCount;
    private Double rating = 0.0; // 默认值为0.0
    @Phone
    private String phone;
    private String cardId; //身份证号码
    private String detailedIntro;
    private String briefIntro;
    private String workplace;
    private String email;
    private String title;
    private String field;
    private String expertiseTag;

    public ConsultantDto convert2ConsultantDto(ConsultantService consultantService) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return ConsultantDto.builder()
                .expertiseTag(this.expertiseTag==null? new ArrayList<>() : objectMapper.readValue(this.expertiseTag, new TypeReference<List<ExpertiseTag>>() {}))
                .id(String.valueOf(this.id))
                .briefIntroduction(this.briefIntro)
                .consultantAvatar(this.avatar)
                .consultantName(this.name)
                .consultState(this.curStatus)
                .helpCount(this.helpNum)
                .consultTotalCount(this.helpTotalNum)
                .supervisorBindings(consultantService.getSupervisorBindings(this))
                .workArrangement(consultantService.getWorkArrangement(this))
                .consultTotalTime(this.totalHelpTime)
                .averageRank(this.rating.intValue())
                .phone(this.getPhone())
                .build();
    }
}