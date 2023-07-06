package com.example.heart_field.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.heart_field.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:16 AM
 */

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
    @Select("select visitor_score from record where consultant_id=#{id} and visitor_score is not null")
    List<Integer> selectScoresByConsultantId(Integer id);
}
