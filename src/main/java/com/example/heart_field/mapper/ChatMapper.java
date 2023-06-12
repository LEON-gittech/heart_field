package com.example.heart_field.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.Chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChatMapper extends BaseMapper<Chat> {


    @Select("select count(distinct user_a) from chat where type=0 and user_b=#{userB};")
    Integer getHelpNum(Integer userB);
}
