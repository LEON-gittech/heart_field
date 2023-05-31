package com.example.heart_field.dto;

import lombok.Data;

import java.util.List;

@Data
public class SupervisorPageSearchDto {
    private List<SupervisorComDto> supervisors;
    private Integer pageNum;
}
