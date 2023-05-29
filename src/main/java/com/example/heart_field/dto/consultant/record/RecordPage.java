package com.example.heart_field.dto.consultant.record;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/28 11:03 AM
 */
@Data
@Builder
@AllArgsConstructor
public class RecordPage<T> {
    private Integer pages;

    private Page<T> records;

    public RecordPage(Page<T> resPage, int pages) {
    }
}
