package com.example.heart_field.dto.schedule;

import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/6/1 10:28 PM
 */
@Data
@Builder
public class ConsultantScheDTO {
    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;
    private Integer isValid;
    private Integer isDisabled;
}
