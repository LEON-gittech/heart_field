package com.example.heart_field.dto;

import lombok.Data;

@Data
public class CommentDto {
    private String consultantId;
    private String visitorComment;
    private Integer visitorScore;
    private String visitorId;
    //需要查Visitor表
    private String userName;
    private String userAvatar;
}
