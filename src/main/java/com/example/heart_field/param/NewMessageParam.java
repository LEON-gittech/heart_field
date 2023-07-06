package com.example.heart_field.param;

import lombok.Data;

@Data
public class NewMessageParam {
    private Integer chatId;
    private String messageType;
    private String content;
    private Integer senderId;
    private String senderType;
}
