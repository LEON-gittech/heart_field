package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class Visitor {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private boolean disabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
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