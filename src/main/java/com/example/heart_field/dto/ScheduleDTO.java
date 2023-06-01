package com.example.heart_field.dto;

import com.example.heart_field.dto.consultant.ConsultantDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 6:10 PM
 */
@Data
@Builder
public class ScheduleDTO {
    private Integer date;//当日的日期

    private Integer consultantCount;

    private Integer supervisorCount;

    private List<ConsultantScheDTO> consultantList;

    private List<SupervisorDTO> supervisorList;
}
