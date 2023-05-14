package com.example.heart_field.entity;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class Admin {
    private int id;
    private String username;
    private String phone;
    private String password;
    private String avatar;
    private Timestamp createTime;
    private Timestamp updateTime;
    private boolean disabled;
}