package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 8:54 AM
 */

@Data
public class Schedule {
    /**
     * 主键，用于唯一标识每条值班安排
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer staffType;
    private Integer staffId;
    private Integer workday;
    private LocalDateTime createTime;
}
