package com.example.heart_field.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.ScheduleDTO;
import com.example.heart_field.entity.Schedule;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:32 AM
 */
public interface ScheduleService extends IService<Schedule> {
    ResultInfo<ScheduleDTO> getAllSchedules();
}
