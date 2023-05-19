package com.example.heart_field.dto;

import lombok.Data;

import java.util.List;

/**
 * /consultants接口返回的数据
 */
@Data
public class ConsultantDto{
    //定义结构
    //属性
    private List<ExpertiseTag> expertiseTag;
    private String consultantAvatar;
    private String id;
    private String briefIntroduction;
    private String consultantName;
    private Integer consultState;
    private Integer helpCount;
    private Integer consultTotalCount;
    private List<SupervisorBinding> supervisorBindings;
    List<Integer> workArrangement;
    Integer consultTotalTime;
    Integer averageRank;
}
