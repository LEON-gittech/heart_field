package com.example.heart_field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author albac0020@gmail.com
 * data 2023/5/17 7:16 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    private Integer type;
    //type为0是Visitor，1是Consultant，2是Admin，3是Supervisor

    private String token;

    private Integer id;

}
