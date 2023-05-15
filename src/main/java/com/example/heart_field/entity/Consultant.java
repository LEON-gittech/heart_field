package com.example.heart_field.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Consultant {
    private Integer id;
    private boolean online;
    private boolean disabled;
    private boolean valid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String name;
    private int age;
    private byte gender;
    private String password;
    private String avatar;
    private int maxConcurrent;
    private int maxNum;
    private int curStatus;
    private int helpNum;
    private double rating;
    private String phone;
    private String cardId;
    private String detailedIntro;
    private String briefIntro;
    private String workplace;
    private String email;
    private String title;
    private String field;
}