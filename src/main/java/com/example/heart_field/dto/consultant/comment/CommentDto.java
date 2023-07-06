package com.example.heart_field.dto.consultant.comment;

import lombok.Data;

@Data
public class CommentDto {
    private String consultantId="";
    private String visitorComment="";
    private Integer visitorScore=0;
    private String visitorId="";
    //需要查Visitor表
    private String userName="";
    private String userAvatar="";
    private String commentTime="";
}
