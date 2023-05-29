package com.example.heart_field.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 8:04 PM
 */
@Data
@Builder
public class ConsultantDTO {
    private Integer id;

    private String consultantName;

    private String avatar;
}
