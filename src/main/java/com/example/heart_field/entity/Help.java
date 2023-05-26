package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 8:33 AM
 */
@Data
public class Help {
    /**
     * 主键，用于唯一标识每个求助记录
     */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 该记录是否被删除
     */
    private Boolean isDeleted;

    /**
     * 该记录创建时间，时间戳类型
     */
    private LocalDateTime createTime;

    /**
     * 该记录更新时间，时间戳类型
     */
    private LocalDateTime updateTime;

    /**
     * 该记录对应的咨询师id
     */
    private Integer consultantId;

    /**
     * 该记录对应的督导id
     */
    private Integer supervisorId;

    /**
     * 求助开始时间
     */
    private LocalDateTime startTime;

    /**
     * 求助结束时间
     */
    private LocalDateTime endTime;

}
