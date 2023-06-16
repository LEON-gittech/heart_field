package com.example.heart_field.dto.supervisor;

import lombok.Data;

@Data
public class SupervisorProfileDto {
    private Integer currentSessionCount;
    private String id;
    private Integer maxConcurrentCount;
    private Integer maxCount;
    private String name;
    private Integer todayTotalCount;
    private long todayTotalTime;
    private Integer type;
    private Integer state;
}
