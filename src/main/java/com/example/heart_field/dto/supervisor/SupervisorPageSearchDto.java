package com.example.heart_field.dto.supervisor;

import com.example.heart_field.dto.supervisor.SupervisorComDto;
import lombok.Data;

import java.util.List;

@Data
public class SupervisorPageSearchDto {
    private List<SupervisorComDto> supervisors;
    private Integer pageNum;
}
