package com.example.heart_field.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.Consultant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConsultantMapper extends BaseMapper<Consultant> {
    @Select("select sum(current_session_count) from consultant;")
    Integer selectActiveChatCount();
}
