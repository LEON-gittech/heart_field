package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 8:45 AM
 */

@Data
public class Message {
    /**
     * 主键，用于唯一标识每条信息
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 所属的聊天id
     */
    private Integer chatId;

    /**
     * 该消息发送时间
     */
    private Timestamp sendTime;

    /**
     * 消息所有者类型，0:咨询师发送 1:访客发送 2:督导发送
     */
    private Byte owner;

    /**
     * 消息发送者id
     */
    private Integer senderId;

    /**
     * 消息接收者id
     */
    private Integer ReceiverId;

    /**
     * 消息类型，0:文字 1:图片 2:语音 3:表情 4:聊天记录 5:无法识别
     */
    private Byte type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 如是聊天记录类型的消息，表示与其相关的chatId
     */
    private Integer relatedChat;

    /**
     * 消息创建时间
     */
    private Timestamp createTime;

    /**
     * 消息更新时间
     */
    private Timestamp updateTime;

    /**
     * 该消息是否被删除
     */
    private Boolean isDeleted;
}
