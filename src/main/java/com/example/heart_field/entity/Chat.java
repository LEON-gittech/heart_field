package com.example.heart_field.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * 聊天记录表(Chat)表实体类
 *
 * @author makejava
 * @since 2023-05-15 16:53:23
 */
@Data
public class Chat {
    //主键，自增长
    private Integer id;
    //发起聊天的用户类型，0表示访客，1表示咨询师，2表示督导
    private Integer fromType;
    //发起聊天的用户id，是一个外键，引用自对应的访客、咨询师或督导表的id列
    private Integer fromId;
    //接收聊天的用户类型，0表示访客，1表示咨询师，2表示督导
    private Integer toType;
    //接收聊天的用户id，是一个外键，引用自对应的访客、咨询师或督导表的id列
    private Integer toId;
    //聊天开始时间
    private LocalDateTime startTime;
    //聊天结束时间
    private LocalDateTime endTime;
    public Integer getId() {
        return id;
    }

    }

