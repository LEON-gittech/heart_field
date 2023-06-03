package com.example.heart_field.dto.consultant.profile;

import com.example.heart_field.annotation.Id;
import com.example.heart_field.annotation.Name;
import com.example.heart_field.annotation.Password;
import com.example.heart_field.annotation.Phone;
import com.example.heart_field.dto.consultant.ExpertiseTag;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.List;

/**
 * /consultants/{consultantId}/profile
 */
@Data
public class UpdateConsultantProfileDto {
    @Name
    private String name;
    private String briefIntro;
    private String detailedIntro;
    private List<ExpertiseTag> expertiseTag;
    private Integer gender;
    @Phone
    private String phone;
    @Id
    private String cardId;
    @Email(message = "邮箱格式不正确")
    private String email;
    @Password
    private String password;
    private String workplace;
    private String title;
    private String id;
    private Integer age;
    private String avatar;
}
