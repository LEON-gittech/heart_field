package com.example.heart_field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author albac0020@gmail.com
 * data 2023/6/13 3:47 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDTO {
    private Integer chatId;
    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;
    private String startTime;
}
