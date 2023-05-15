package com.example.heart_field.entity;

import lombok.Data;

import java.sql.Timestamp;


@Data
public class Visitor {
    private Integer id;
    private boolean disabled;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String username;
    private String name;
    private String phone;
    private String emergencyName;
    private String emergencyPhone;
    private String avatar;
    private String direction;
    private String puzzle;
    private String history;
    private String question;
}