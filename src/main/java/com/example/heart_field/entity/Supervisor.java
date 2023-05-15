package com.example.heart_field.entity;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Supervisor {
    private Integer id;
    private boolean online;
    private boolean disabled;
    private boolean valid;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String name;
    private Integer age;
    private byte gender;
    private String password;
    private String avatar;
    private Integer maxConcurrent;
    private Integer maxNum;
    private String phone;
    private String cardId;
    private String email;
    private String workplace;
    private String title;
    private String qualification;
    private String qualificationId;
}
