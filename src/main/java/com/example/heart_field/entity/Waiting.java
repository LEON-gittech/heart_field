package com.example.heart_field.entity;

import lombok.Data;
import java.util.Timestamp;

@Data
public class Waiting {
    /**
     * 排队记录ID
     */
    private Integer id;
    /**
     * 访客ID
     */
    private Integer visitorId;
    /**
     * 咨询师ID
     */
    private Integer consultantId;
    /**
     * 排队序号，每个咨询师是独立的
     */
    private Integer number;
    /**
     * 排队状态，0:排队中，1:已放弃，2:排队完成进入咨询
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 更新时间
     */
    private Timestamp updateTime;

}

