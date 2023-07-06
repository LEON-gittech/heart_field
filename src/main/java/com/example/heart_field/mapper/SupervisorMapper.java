package com.example.heart_field.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.Supervisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SupervisorMapper extends BaseMapper<Supervisor> {
    @Select("select sum(concurrent_num) from supervisor")
    Integer selectActiveChatCount();
}
