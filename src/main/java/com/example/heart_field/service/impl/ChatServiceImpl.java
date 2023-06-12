package com.example.heart_field.service.impl;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.*;
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

    @Autowired
    private RecordMapper recordMapper;


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
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        List<Record> allRecords=recordMapper.selectList(queryWrapper);
        int totalCount=0;
        for (Record record : allRecords) {
            if(record.getEndTime()!=null&&record.getEndTime().getDayOfYear()<LocalDateTime.now().getDayOfYear()){
                continue;//不是今天结束的，表示是在今天之前结束的
            }
            else{
                totalCount++;//结束时间在今天
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
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        List<Record> allRecords=recordMapper.selectList(queryWrapper);
        int totalDuration=0;
        for (Record record:allRecords) {
            if(record.getEndTime()!=null && record.getEndTime().getDayOfYear()<LocalDateTime.now().getDayOfYear()){
                continue;//不是今天结束的，表示是在今天之前结束的
            }
            //如果是今天开始的，开始时间为今天具体时间；如果不是今天开始的，开始时间为今天最小时间
            LocalDateTime todayStart=(record.getStartTime().getDayOfYear()==LocalDateTime.now().getDayOfYear())
                    ? record.getStartTime()
                    : TimeUtil.getDayStart();
            //如果是今天结束的，结束时间是今天；如果结束时间不是今天的，不计算；如果还没结束，结束时间是现在
            LocalDateTime todayEnd=(record.getEndTime()==null)
                    ? LocalDateTime.now()
                    : record.getEndTime();
            Duration duration = Duration.between(todayStart,todayEnd);
            log.info("todayStart:"+todayStart+",todayEnd:"+todayEnd+",duration"+duration.getSeconds());
            totalDuration += duration.getSeconds();
        }
        return totalDuration;
    }





    /*
    本周情况:今日咨询总数
    //todo:本周/前七日
     */
    @Override
    public List<Integer> getWeekCounsels() {
        List<Integer> result = new ArrayList<>();
       // int size = LocalDateTime.now().getDayOfWeek().getValue();
        for(int i=0;i<=6;i++){
            LocalDateTime start = TimeUtil.getDayStart(LocalDateTime.now().minusDays(i));
            LocalDateTime end = TimeUtil.getDayEnd(LocalDateTime.now().minusDays(i));
            log.info("start:"+start+",end:"+end);

            List<Chat> todayChats=chatMapper.getTodayChats(start,end);

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
            List<Chat> todayChats=chatMapper.getTodayChats(start,end);
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
                supervisor.setTodayTotalHelpTime((int) (supervisor.getTodayTotalHelpTime()+durationSeconds));
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
        if(consultant.getCurStatus()==1){
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

