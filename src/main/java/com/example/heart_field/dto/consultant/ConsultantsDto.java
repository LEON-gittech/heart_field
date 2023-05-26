package com.example.heart_field.dto.consultant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * /consultants接口返回的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultantsDto{
    //定义结构
    private List<ConsultantDto> consultants;
    private Integer pageNum;
}
