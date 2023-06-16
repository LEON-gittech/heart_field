package com.example.heart_field.dto.record;

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

    private List<T> records;

    private Integer total;

    public RecordPage(List<T> resPage, int pages,int total) {
        this.pages = pages;
        this.records = resPage;
        this.total = total;
    }
}
