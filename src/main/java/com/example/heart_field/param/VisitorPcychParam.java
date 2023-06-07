package com.example.heart_field.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/6/4 7:50 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorPcychParam {
    private String direction;

    private String puzzle;

    private String history;

    private List<Integer> question;
}
