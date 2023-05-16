package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Consultant {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private boolean isOnline = false; // 默认值为false
    private boolean isDisabled = false; // 默认值为false
    private boolean isValid = false; // 默认值为false
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime ; // 默认值为当前时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime ; // 默认值为当前时间
    private String name;
    private Integer age;
    private Integer gender = 2; // 默认值为2
    private String password;
    private String avatar;
    private Integer maxConcurrent = 2; // 默认值为2
    private Integer maxNum = 20; // 默认值为20
    private Integer curStatus = 0; // 默认值为0
    private Integer helpNum = 0; // 默认值为0
    private Double rating = 0.0; // 默认值为0.0
    private String phone;
    private String cardId;
    private String detailedIntro;
    private String briefIntro;
    private String workplace;
    private String email;
    private String title;
    private String field;
    private String expertiseTag;
}