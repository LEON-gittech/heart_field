package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
public class Visitor {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private Byte isDisabled;
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
    private Byte gender;//0女1男2未知
    private String openId;





}