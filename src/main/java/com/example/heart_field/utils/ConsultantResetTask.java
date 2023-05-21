package com.example.heart_field.utils;

import com.example.heart_field.service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConsultantResetTask {
    @Autowired
    private ConsultantService consultantService;
    //每天0点执行一次
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyProperties(){
        consultantService.resetDailyProperties();
    }
}
