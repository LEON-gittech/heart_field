package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class User {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private Integer userId; //id为id
    private String password;
    private Integer type; //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
}
