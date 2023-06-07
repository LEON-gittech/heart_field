package com.example.heart_field.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/6/4 7:53 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorPcychDTO {
    private Integer id;

    private String direction;

    private String puzzle;

    private String history;

    private List<Integer> question;
}
