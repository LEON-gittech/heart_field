package com.example.heart_field.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;

    //0咨询会话；1求助会话
    private Integer type;

    //聊天的发起者
    // 咨询会话-访客；求助会话-咨询师
    private Integer userA;

    //聊天的接受者
    //咨询会话-咨询师，求助会话-督导
    private Integer userB;

    //聊天开始时间
    private LocalDateTime startTime;

    //聊天结束时间
    private LocalDateTime endTime;

}

