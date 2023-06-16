package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String phone;
    private String password;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Byte isDisabled=0;
}
