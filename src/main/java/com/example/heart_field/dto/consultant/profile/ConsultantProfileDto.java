package com.example.heart_field.dto.consultant.profile;

import lombok.Data;

/**
 * /consoultant/profile接口返回的数据
 */
@Data
public class ConsultantProfileDto {
    private String id;
    private String name;
    private Integer maxCount;
    private Integer maxConcurrentCount;
    private Integer currentSessionCount;
    private Integer todayTotalTime;
    private Integer todayTotalCount;
}
