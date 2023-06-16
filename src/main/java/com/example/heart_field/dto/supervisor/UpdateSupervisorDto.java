package com.example.heart_field.dto.supervisor;

import lombok.Data;

@Data
public class UpdateSupervisorDto {
    private String avatar;
    private String name;
    private String email;
    private String gender;
    private String cardId;
    private String phone;
    private String qualification;
    private String qualificationId;
    private String supervisorName;
    private String title;
    private String workplace;
    private Integer age;
}
