package com.example.heart_field.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
