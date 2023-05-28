package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.mapper.VisitorMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 8:51 AM
 */

@Data
@Builder
public class Record {
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;
    /**
     * 主键，用于唯一标识每个咨询记录
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 该记录是否被删除
     */
    private Integer isDeleted;//0有效1无效

    /**
     * 该记录创建时间，时间戳类型
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 该记录更新时间，时间戳类型
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

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
    private LocalDateTime startTime;

    /**
     * 该咨询的结束时间
     */
    private LocalDateTime endTime;

    /**
     * 该咨询的督导介入时间
     */
    private LocalDateTime involveTime;

    /**
     * 访客给咨询师评分，1-5的整数
     */
    private Integer visitorScore;

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
    private Integer chatId;

    /**
     * 该咨询记录对应的求助会话id
     */
    private Integer helpId;

    private Integer isCompleted;//0未完成1已完成




    public RecordListDTO convert2ListDTO(){
        return RecordListDTO.builder()
                .consultantId(this.consultantId)
                .visitorId(this.visitorId)
                .supervisorId(this.supervisorId)
                .id(this.id)
                .isCompleted((this.endTime==null?0:1))
                .startTime(this.startTime)
                .endTime(this.endTime)
                .visitorComment(this.visitorComment)
                .visitorScore(this.visitorScore)
                .chatId(this.chatId)
                .helpId(this.helpId)
                .build();

    }




    public String getVisitorName() {
        return visitorMapper.selectById(this.visitorId).getName();
    }

    public String getVisitorUsername() {
        return visitorMapper.selectById(this.visitorId).getUsername();
    }

    public String getConsultantName() {
        return consultantMapper.selectById(this.consultantId).getName();
    }

    public String getSupervisorName() {
        return supervisorMapper.selectById(this.supervisorId).getName();
    }


    public String getConsultantAvatar() {
        return consultantMapper.selectById(this.consultantId).getAvatar();
    }

    public String getVisitorAvatar() {
        return visitorMapper.selectById(this.visitorId).getAvatar();
    }
}
