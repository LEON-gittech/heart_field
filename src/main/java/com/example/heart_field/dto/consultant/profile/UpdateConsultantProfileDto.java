package com.example.heart_field.dto.consultant.profile;

import com.example.heart_field.dto.consultant.ExpertiseTag;
import lombok.Data;

import java.util.List;

/**
 * /consultants/{consultantId}/profile
 */
@Data
public class UpdateConsultantProfileDto {
    private String name;
    private String briefIntro;
    private String detailedIntro;
    private List<ExpertiseTag> expertiseTag;
    private Integer gender;
    private String phone;
    private String cardId;
    private String password;
    private String workplace;
    private String title;
    private String id;
    private Integer age;
    private String avatar;
}
