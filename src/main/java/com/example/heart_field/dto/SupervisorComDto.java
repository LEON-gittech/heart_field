package com.example.heart_field.dto;

import lombok.Data;

import java.util.List;

@Data
public class SupervisorComDto {
    public String id;
    public String supervisorAvatar;
    public String supervisorName;
    public List<ConsultantEasyDto> supervisorBind;

    public String phoneNum;
    public Integer consultTotalCount;
    public Long consultTotalTime;
    public List<Integer> workArrange;

}
