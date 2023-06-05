package com.example.heart_field.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author albac0020@gmail.com
 * data 2023/6/4 11:02 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatParam {
    private Integer type;

    private Integer userA;

    private Integer userB;
}
