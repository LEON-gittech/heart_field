package com.example.heart_field.dto;

import lombok.Data;

import java.util.List;

/**
 * /consultants/{consultant-id}/profile
 */
@Data
public class AnyConsultantProfileDto {
    @Data
    public static class Comment{
        private String user;
        private String comment;
        private Integer range;
    }
    private String consultantName;
    private String avatar;
    private String briefIntroduction;
    private String detailIntroduction;
    private Integer state;
    private List<Integer> workArrangement;
    private List<SupervisorBinding> supervisorBind;
    private Integer helpCount;
    private Integer consultTotalCount;
    private String consultTotalTime;
    private Double averageRank;
    private List<Comment> comments;
}
