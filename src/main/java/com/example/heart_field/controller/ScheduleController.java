package com.example.heart_field.controller;

import com.example.heart_field.common.R;
import com.example.heart_field.common.result.BaseResult;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.ScheduleDTO;
import com.example.heart_field.entity.Schedule;
import com.example.heart_field.service.ScheduleService;
import com.example.heart_field.tokens.AdminToken;
import com.example.heart_field.tokens.UserLoginToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 5:54 PM
 */
@Slf4j
@RestController
//@UserLoginToken
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/all-schedules")
    //@AdminToken
    public R<ScheduleDTO> getAllSchedules(){
        ResultInfo<ScheduleDTO> scheduleResult = scheduleService.getAllSchedules();
        return scheduleResult.isRight()
                ? R.success(scheduleResult.getData())
                : R.error(scheduleResult.getMessage());
    }



}
