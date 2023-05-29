package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.entity.Chat;
import com.example.heart_field.mapper.ChatMapper;
import com.example.heart_field.service.ChatService;
import com.example.heart_field.utils.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天记录表(Chat)表服务实现类
 *
 * @author makejava
 * @since 2023-05-15 16:53:39
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    LocalDateTime now = LocalDateTime.now();

    /**
     * 今日咨询总数:
     *      已结束的：endTime-startTime
     *      未结束的：当前时间-startTime
 *      如果startTime不是今天，startTime设为今日最小
     * todo
     * @return
     */
    @Override
    public Integer getTotalCounselToday() {
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getType, TypeConstant.COUNSEL_CHAT);
        List<Chat> allChats=baseMapper.selectList(queryWrapper);
        int totalCount=0;
        for (Chat chat : allChats) {
            if(chat.getEndTime()!=null&&chat.getEndTime().getDayOfYear()<now.getDayOfYear()){
                continue;
            }
            else{
                totalCount++;
            }


        }
        return totalCount++;
    }

    /**
     * 今日咨询总时长 s为单位
     * 根据chat表查询，如当前会话未结束，则计算当前时间与会话开始时间的差值
*                   如会话已结束则计算会话结束时间与会话开始时间的差值

     * @return
     */
    @Override
    public Integer getTotalDurationToday() {
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getType, TypeConstant.COUNSEL_CHAT);
        List<Chat> allChats=baseMapper.selectList(queryWrapper);
        int totalDuration=0;
        for (Chat chat : allChats) {
            if(chat.getEndTime()!=null&&chat.getEndTime().getDayOfYear()<now.getDayOfYear()){
                continue;
            }
            LocalDateTime todayStart=(chat.getStartTime().getDayOfYear()==now.getDayOfYear())
                    ? chat.getStartTime()
                    : TimeUtil.getDayStart();
            LocalDateTime todayEnd=(chat.getEndTime()==null)
                    ? now
                    : chat.getEndTime();
            totalDuration += todayEnd.getSecond()-todayStart.getSecond();


        }
        return totalDuration;
    }

    /**
     * 正在进行的咨询师会话数
     * @return
     */
    @Override
    public Integer getActiveCounselCount() {
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getType, TypeConstant.COUNSEL_CHAT).isNotNull(Chat::getEndTime);
        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * 正在进行的督导会话数
     * @return
     */
    @Override
    public Integer getActiveAssistanceCount() {
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getType, TypeConstant.HELP_CHAT).isNotNull(Chat::getEndTime);
        return baseMapper.selectCount(queryWrapper);
    }

    /*
    本周情况:今日咨询总数
    //todo:本周/前七日
     */
    @Override
    public List<Integer> getWeekCounsels() {
        List<Integer> result = new ArrayList<>();
        int size = now.getDayOfWeek().getValue();
        for(int i=1;i<=size;i++){
            LocalDateTime start = TimeUtil.getWeekStart().plusDays(i);
            LocalDateTime end = TimeUtil.getWeekStart().plusDays(i+1);
            LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Chat::getType, TypeConstant.COUNSEL_CHAT)
                    .or(wrapper->{
                        wrapper.eq(Chat::getEndTime,null).le(Chat::getStartTime,start);
                        //会话进行中，且开始时间在start及以前
                    })
                    .or(wrapper->{
                        wrapper.isNotNull(Chat::getEndTime).gt(Chat::getEndTime,end).le(Chat::getStartTime,start);
                        //会话结束，结束时间比end晚，开始时间比start早
                    });

            List<Chat> todayChats=baseMapper.selectList(queryWrapper);
            result.add(todayChats.size());
        }
        return result;
    }

    /**
     * 获取今日分时咨询情况-指咨询时间是在本小时内开始的咨询数
     * @return
     */
    @Override
    public List<Integer> getTodayCounsels() {
        List<Integer> result = new ArrayList<>();
        int size = now.getHour();//0-23
        for(int i=0;i<=size;i++){
            LocalDateTime start = TimeUtil.getDayStart().plusHours(i);
            LocalDateTime end = TimeUtil.getDayStart().plusHours(i+1);
            LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
            //开始时间在这个小时之前，且当前没结束 ；或者结束时间在当前这个小时之后、开始时间在这个小时之前


            queryWrapper.eq(Chat::getType, TypeConstant.COUNSEL_CHAT)
                    .or(wrapper->{
                        wrapper.isNull(Chat::getEndTime).le(Chat::getStartTime,start);
                        //会话进行中，且开始时间在start及以前
                    })
                    .or(wrapper->{
                        wrapper.isNotNull(Chat::getEndTime).gt(Chat::getEndTime,end).le(Chat::getStartTime,start);
                        //会话结束，结束时间比end晚，开始时间比start早
                    });

            List<Chat> todayChats=baseMapper.selectList(queryWrapper);
            result.add(todayChats.size());
        }
        return result;

    }
}

