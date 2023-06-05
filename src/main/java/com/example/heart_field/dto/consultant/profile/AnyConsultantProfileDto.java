package com.example.heart_field.dto.consultant.profile;

import com.example.heart_field.dto.consultant.ExpertiseTag;
import com.example.heart_field.dto.consultant.binding.SupervisorBinding;
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
    private String briefIntro;
    private String detailIntro;
    private Integer state;
    private List<Integer> workArrangement;
    private List<SupervisorBinding> supervisorBind;
    private Integer helpCount;
    private Integer consultTotalCount;
    private String consultTotalTime;
    private Double averageRank;
    private List<Comment> comments;
    private List<ExpertiseTag> expertiseTag;
    private Integer gender;
    private String phone;
    private String cardId;
    private String email;
    private String workplace;
    private String title;
    private String id;
    private Integer age;
}
