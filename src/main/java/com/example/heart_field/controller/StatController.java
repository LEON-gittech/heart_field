package com.example.heart_field.controller;

import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.StatDTO;
import com.example.heart_field.service.ChatService;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.tokens.AdminToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/27 9:46 PM
 */
@Slf4j
@RestController
@AdminToken
public class StatController {

    @Autowired
    private ChatService chatService;


    @GetMapping("/data-statistics")
    public R<StatDTO> getDataStatistics(){
        Integer todayTotalCounsel = chatService.getTotalCounselToday();
        Integer todayTotalDuration = chatService.getTotalDurationToday();
        Integer activeCounselCount = chatService.getActiveCounselCount();
        Integer activeAssistanceCount = chatService.getActiveAssistanceCount();
        List<Integer> weekCounsels = chatService.getWeekCounsels();
        List<Integer> todayCounsels = chatService.getTodayCounsels();

        StatDTO statDTO = StatDTO.builder()
                .todayTotalCounsel(todayTotalCounsel)
                .todayTotalDuration(todayTotalDuration)
                .activeCounselCount(activeCounselCount)
                .activeAssistanceCount(activeAssistanceCount)
                .weekCounsels(weekCounsels)
                .todayCounsels(todayCounsels)
                .build();
        return R.success(statDTO);

    }
}
