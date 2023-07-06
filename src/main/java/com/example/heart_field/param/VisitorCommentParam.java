package com.example.heart_field.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author albac0020@gmail.com
 * data 2023/6/7 2:27 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitorCommentParam {
    private String comment;
    private Integer rank;

}
