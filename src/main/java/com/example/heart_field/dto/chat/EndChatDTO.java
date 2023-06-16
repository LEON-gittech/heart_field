package com.example.heart_field.dto.chat;

import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/6/16 8:39 AM
 */
@Data
@Builder
public class EndChatDTO {
    private Integer type;
    private Integer chatId;
    private Integer endId;
}
