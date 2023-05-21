package com.example.heart_field.entity;

import lombok.Data;

@Data
public class User {
    private Integer id; //id为id
    private String password;
    private Integer type; //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
}
