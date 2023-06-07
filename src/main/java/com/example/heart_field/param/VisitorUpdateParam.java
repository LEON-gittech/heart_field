package com.example.heart_field.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author albac0020@gmail.com
 * data 2023/6/2 9:45 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorUpdateParam {
    private String name;
    private String username;
    private String emergencyName;
    private String emergencyPhone;
    private Integer gender;
}
