package com.example.heart_field.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Admin {
    private Integer id;
    private String username;
    private String phone;
    private String password;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean disabled;
}