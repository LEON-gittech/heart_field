package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Supervisor {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private boolean isOnline;
    private boolean isDisabled;
    private boolean isValid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
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

    public Integer concurrentNum;
}
