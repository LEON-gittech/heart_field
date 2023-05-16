package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 8:51 AM
 */

@Data
public class Record {
    /**
     * 主键，用于唯一标识每个咨询记录
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
    private Timestamp createTime;

    /**
     * 该记录更新时间，时间戳类型
     */
    private Timestamp updateTime;

    /**
     * 该记录对应的咨询师id
     */
    private Integer consultantId;

    /**
     * 该记录对应的访客id
     */
    private Integer visitorId;

    /**
     * 该记录对应的督导id
     */
    private Integer supervisorId;



    /**
     * 该咨询的开始时间
     */
    private Timestamp startTime;

    /**
     * 该咨询的结束时间
     */
    private Timestamp endTime;

    /**
     * 该咨询的督导介入时间
     */
    private Timestamp involveTime;

    /**
     * 访客给咨询师评分，1-5的整数
     */
    private Byte visitorScore;

    /**
     * 访客对咨询师的评价
     */
    private String visitorComment;


    /**
     * 咨询师对访客病情的评价
     */
    private String evaluation;

    /**
     * 咨询师-判定访客咨询类型，字符串列表
     */
    private String consultType;

    /**
     * 该咨询记录对应的咨询会话id
     */
    private Integer consultId;

    /**
     * 该咨询记录对应的求助会话id
     */
    private Integer helpId;
}
