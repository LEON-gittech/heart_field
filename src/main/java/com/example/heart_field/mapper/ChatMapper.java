package com.example.heart_field.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {


    @Select("select count(distinct user_a) from chat where type=0 and user_b=#{userB};")
    Integer getHelpNum(Integer userB);

    @Select("SELECT * FROM chat WHERE type = 0 AND  ( (end_time IS NULL AND start_time <= #{end_time}) OR (end_time IS NOT NULL AND end_time < #{end_time} AND end_time >= #{start_time} AND start_time <= #{end_time}) OR (end_time IS NOT NULL AND end_time > #{end_time}  AND start_time <= #{start_time}) OR (start_time>#{start_time} AND start_time < #{end_time}) )")
    List<Chat> getTodayChats(LocalDateTime start_time, LocalDateTime end_time);
    /*
    在今天之内有会话
    当前未结束，且已经开始（今天开始还是不是今天开始都不重要）
    或者
    当前已经结束，且结束时间在今天之内，且开始时间在今天之前
     */
}
