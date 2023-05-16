package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String phone;
    private String password;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean isDisabled;
}