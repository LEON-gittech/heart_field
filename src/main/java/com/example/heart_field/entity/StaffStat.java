package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
@Data
public class StaffStat {
    /**
     * 主键，用于唯一标识每个督导
     */
    private Integer id;

    /**
     * 职员类型，0表示咨询师，1表示督导
     */
    private Integer staffType;

    /**
     * 职员id，是一个外键，引用自咨询师或督导表的id列
     */
    private Integer staffId;

    /**
     * 日期，格式为"yyyy-MM-dd"
     */
    private String date;

    /**
     * 是否是已完成数据，0表示否，1表示是
     */
    private Integer isComplete;

    /**
     * 今日咨询时长，单位为秒，使用varchar存储
     */
    private String totalTime;

    /**
     * 今日已咨询数，使用int存储
     */
    private Integer totalCount;

}