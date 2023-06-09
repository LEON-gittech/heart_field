package com.example.heart_field.service.impl;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.entity.Chat;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.entity.Visitor;
import com.example.heart_field.mapper.ChatMapper;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.mapper.VisitorMapper;
import com.example.heart_field.param.ChatParam;
import com.example.heart_field.service.ChatService;
import com.example.heart_field.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.heart_field.constant.ConsultantStatus.BUSY;
import static com.example.heart_field.constant.ConsultantStatus.FREE;

/**
 * 聊天记录表(Chat)表服务实现类
 *
 * @author makejava
 * @since 2023-05-15 16:53:39
 */
@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private ChatMapper chatMapper;


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
            if(chat.getEndTime()!=null&&chat.getEndTime().getDayOfYear()<LocalDateTime.now().getDayOfYear()){
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
            if(chat.getEndTime()!=null&&chat.getEndTime().getDayOfYear()<LocalDateTime.now().getDayOfYear()){
                continue;
            }
            LocalDateTime todayStart=(chat.getStartTime().getDayOfYear()==LocalDateTime.now().getDayOfYear())
                    ? chat.getStartTime()
                    : TimeUtil.getDayStart();
            LocalDateTime todayEnd=(chat.getEndTime()==null)
                    ? LocalDateTime.now()
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
        int size = LocalDateTime.now().getDayOfWeek().getValue();
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
        int size = LocalDateTime.now().getHour();//0-23
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


    @Override
    public ResultInfo createChat(ChatParam chat) {
        log.info("userA"+chat.getUserA()+"userB"+chat.getUserB());
        switch (chat.getType()) {
            case 0:
                return createCounselChat(chat.getUserA(),chat.getUserB());
            case 1:
                return createHelpChat(chat.getUserA(),chat.getUserB());
            default:
                return ResultInfo.error("参数错误");
        }
    }

    @Override
    public ResultInfo endChat(Integer chatId) {
        Chat chat = baseMapper.selectById(chatId);
        if(chat == null||chat.getEndTime()!=null){
            return ResultInfo.error("会话不存在或已结束");
        }
        chat.setEndTime(LocalDateTime.now());
        baseMapper.updateById(chat);
        Duration duration = Duration.between(chat.getStartTime(),chat.getEndTime());
        Long durationSeconds = duration.getSeconds();
        switch (chat.getType()){
            case 0:
                Consultant consultant = consultantMapper.selectById(chat.getUserB());
                if(consultant.getCurStatus()==1){
                    if(consultant.getCurrentSessionCount()-1<consultant.getMaxConcurrent()){
                        consultant.setCurStatus(0);
                    }
                }
                Integer newHelpNum = chatMapper.getHelpNum(chat.getUserB());
                consultant.setTodayTotalHelpTime((int) (consultant.getTodayTotalHelpTime()+durationSeconds));
                consultant.setTotalHelpTime((int) (consultant.getTotalHelpTime()+durationSeconds));
                consultant.setCurrentSessionCount(consultant.getCurrentSessionCount()-1);
                consultant.setHelpNum(newHelpNum);
                consultantMapper.updateById(consultant);
                break;
            case 1:
                Supervisor supervisor = supervisorMapper.selectById(chat.getUserB());
                supervisor.setTotalHelpTime((int) (supervisor.getTotalHelpTime()+durationSeconds));
                supervisor.setTotalHelpTime((int) (supervisor.getTotalHelpTime()+durationSeconds));
                supervisor.setConcurrentNum(supervisor.getConcurrentNum()-1);
                supervisorMapper.updateById(supervisor);
                break;
        }
        return ResultInfo.success(chat);
    }

    /**
     * 求助会话，userA为咨询师，userB为督导
     * @param userA
     * @param userB
     * @return
     * todo
     */
    private ResultInfo createHelpChat(Integer userA, Integer userB) {
        Consultant consultant = consultantMapper.selectById(userA);
        if(consultant == null||consultant.getIsValid()==0||consultant.getIsDisabled()==1) {
            return ResultInfo.error("咨询师不存在或已被封禁");
        }
        Supervisor supervisor = supervisorMapper.selectById(userB);
        if(supervisor == null||supervisor.getIsValid()==0||supervisor.getIsDisabled()==1) {
            return ResultInfo.error("督导不存在或已被封禁");
        }
        log.info("supervisor"+supervisor);
        int count = new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Chat::getType,TypeConstant.HELP_CHAT)
                .eq(Chat::getUserA,userA)
                .eq(Chat::getUserB,userB)
                .isNull(Chat::getEndTime)
                .count();
        List<Chat> chatList = new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Chat::getType,TypeConstant.HELP_CHAT)
                .eq(Chat::getUserA,userA)
                .eq(Chat::getUserB,userB)
                .isNull(Chat::getEndTime)
                .list();
        log.info(chatList.toString());
        if(count>0){
            return ResultInfo.error("双方存在未完成会话");
        }
        Chat chat = Chat.builder()
                .type(TypeConstant.HELP_CHAT)
                .startTime(LocalDateTime.now())
                .userA(consultant.getId())
                .userB(supervisor.getId())
                .build();
        baseMapper.insert(chat);
        log.info("Created chat"+chat);
        supervisor.setTodayTotalHelpCount(supervisor.getTodayTotalHelpCount()+1);
        supervisor.setHelpTotalNum(supervisor.getHelpTotalNum()+1);
        supervisor.setConcurrentNum(supervisor.getConcurrentNum()+1);
        supervisorMapper.updateById(supervisor);
        log.info("Updated supervisor"+supervisor);
        return ResultInfo.success(chat);
    }

    /**
     * 咨询会话，userA为访客，userB为咨询师
     * @param userA
     * @param userB
     * @return
     */
    private ResultInfo createCounselChat(Integer userA, Integer userB) {
        log.info("userA"+userA+"userB"+userB);
        Visitor visitor = visitorMapper.selectById(userA);
        log.info("visitor"+visitor);
        if(visitor == null||visitor.getIsDisabled() == 1) {
            return ResultInfo.error("访客不存在或已被封禁");
        }
        Consultant consultant = consultantMapper.selectById(userB);
        log.info("consultant"+consultant);
        if(consultant == null||consultant.getIsValid()==0||consultant.getIsDisabled()==1) {
            return ResultInfo.error("咨询师不存在或已被封禁");
        }
        if(consultant.getCurStatus()==BUSY){
            return ResultInfo.error("咨询师忙碌中");
        }
        int count = new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Chat::getType, TypeConstant.COUNSEL_CHAT)
                .eq(Chat::getUserA,userA)
                .eq(Chat::getUserB,userB)
                .isNull(Chat::getEndTime)
                .count();
        List<Chat> chats=new LambdaQueryChainWrapper<>(this.baseMapper)
                .eq(Chat::getType, TypeConstant.COUNSEL_CHAT)
                .eq(Chat::getUserA,userA)
                .eq(Chat::getUserB,userB)
                .isNull(Chat::getEndTime)
                .list();
        log.info("chats"+chats);
        if(count>0){
            return ResultInfo.error("双方存在未完成会话");
        }
        Chat chat = Chat.builder()
                .startTime(LocalDateTime.now())
                .type(TypeConstant.COUNSEL_CHAT)
                .userA(visitor.getId())
                .userB(consultant.getId())
                .build();
        baseMapper.insert(chat);
        consultant.setHelpNum(consultant.getHelpNum()+1);
        consultant.setCurrentSessionCount(consultant.getCurrentSessionCount()+1);
        consultant.setHelpNum(consultant.getHelpNum()+1);
        if(consultant.getCurrentSessionCount()+1<consultant.getMaxConcurrent()){
            consultant.setCurStatus(0);
        }else{
            consultant.setCurStatus(1);
        }
        consultant.setTodayTotalHelpCount(consultant.getTodayTotalHelpCount()+1);
        consultantMapper.updateById(consultant);
        return ResultInfo.success(chat);
    }
}

