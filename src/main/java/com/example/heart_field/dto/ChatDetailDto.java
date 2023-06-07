package com.example.heart_field.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatDetailDto {
    @Data
    public static class Message{
        private String time;
        private String senderName;
        private String type;
        private String content;
    }
    private String evaluation;
    private String consultType;
    private List<Message> messages;
}
