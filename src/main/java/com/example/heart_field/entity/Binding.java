package com.example.heart_field.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 咨询师督导关系表(Binding)表实体类
 *
 * @author makejava
 * @since 2023-05-15 16:47:01
 */
@Data
public class Binding {
    //主键，用于唯一标识每个记录
    @TableId(value="id",type = IdType.AUTO)
    private Integer id;
    //咨询师id，是一个外键，引用自咨询师表的id列
    private Integer consultantId;
    //督导id，是一个外键，引用自督导表的id列
    private Integer supervisorId;
    //记录创建时间的时间戳，设置默认值为当前时间
    private LocalDateTime createTime;
    //记录最后一次被修改的时间的时间戳，可为空
    private LocalDateTime updateTime;
    //是否删除，0表示未删除，1表示已删除
    private Integer isDeleted;
}

