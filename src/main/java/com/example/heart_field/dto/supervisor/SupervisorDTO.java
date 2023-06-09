package com.example.heart_field.dto.supervisor;

import lombok.Builder;
import lombok.Data;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 8:04 PM
 */
@Data
@Builder
public class SupervisorDTO {
    private Integer id;
    private String supervisorName;
    private String avatar;
    private Integer isValid;
    private Integer isDisabled;
}