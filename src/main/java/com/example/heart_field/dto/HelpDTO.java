package com.example.heart_field.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/28 12:07 PM
 */
@Data
@Builder
public class HelpDTO {
    private Integer id;

    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;

    private Integer supervisorId;
    private String supervisorName;
    private String supervisorAvatar;

    private Integer continueTime;
    private LocalDateTime startTime;

    private Integer chatId;
    private Integer recordId;


}
