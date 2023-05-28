package com.example.heart_field.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/28 10:47 AM
 */
@Builder
@Data
public class RecordPageDTO {
    private Integer pages;
    private List<RecordListDTO> records;
}

