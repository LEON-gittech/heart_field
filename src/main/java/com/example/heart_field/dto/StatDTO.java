package com.example.heart_field.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 9:48 PM
 */
@Data
@Builder
public class StatDTO {
    private Integer todayTotalCounsel;

    private Integer todayTotalDuration;

    private Integer activeCounselCount;

    private Integer activeAssistanceCount;

    private List<Integer> weekCounsels;//七日咨询数量

    private List<Integer> todayCounsels;//今日各小时咨询数

}
