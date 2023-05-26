package com.example.heart_field.dto.consultant.comment;

import lombok.Data;

import java.util.List;

@Data
public class CommentsDto {
    Integer pageNum;
    List<CommentDto> commentsDto;
}
