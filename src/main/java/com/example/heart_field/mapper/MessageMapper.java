package com.example.heart_field.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:16 AM
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
