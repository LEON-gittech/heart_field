package com.example.heart_field.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Schedule;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.service.ScheduleService;
import com.example.heart_field.service.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResetTask {
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private ScheduleService scheduleService;
    //每天0点执行一次
    //咨询师的daily 属性重置
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyProperties(){
        consultantService.resetDailyProperties();
    }
    //计算当天存在排班的雇员
    @Scheduled(cron = "0 0 0 * * ?")
    public void isOnline(){
        List<Schedule> schedules = new ArrayList<>();
        LambdaQueryWrapper<Schedule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        LocalDate currentDate = LocalDate.now();
        Integer day = currentDate.getDayOfMonth();
        lambdaQueryWrapper.eq(Schedule::getWorkday,day);
        schedules = scheduleService.list(lambdaQueryWrapper);
        //便利 schedule
        for(Schedule schedule:schedules){
            //根据 staffType 判断角色
            Integer type = schedule.getStaffType();
            //咨询师
            if(type==1){
                Consultant consultant = consultantService.getById(schedule.getStaffId());
                consultant.setIsOnline(1);
                consultantService.updateById(consultant);
            }
            //督导
            if(type==3){
                Supervisor supervisor =supervisorService.getById(schedule.getId());
                supervisor.setIsOnline(1);
                supervisorService.updateById(supervisor);
            }
        }
    }
}
