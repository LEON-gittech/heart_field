package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.HelpDTO;
import com.example.heart_field.dto.consultant.record.RecordDTO;
import com.example.heart_field.entity.*;
import com.example.heart_field.entity.Record;
import com.example.heart_field.mapper.*;
import com.example.heart_field.service.HelpService;
import com.example.heart_field.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据不同角色返回求助记录
 * 督导-与自己相关的求助记录
 * 管理员：所有的求助记录
 * @author albac0020@gmail.com
 * data 2023/5/15 9:22 AM
 */
@Service
@Slf4j
public class HelpServiceImpl extends ServiceImpl<HelpMapper, Help> implements HelpService {
    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private RecordMapper recordMapper;


    @Override
    public List<HelpDTO> queryRecords(String searchValue, int pageSize, int pageNum, String fromDate, String toDate) {
        LambdaQueryWrapper<Help> queryWrapper= Wrappers.lambdaQuery();
        User user = TokenUtil.getTokenUser();
        //if (user == null) { return new ArrayList<>();}
        switch (user.getType()){
            ////type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
            case 3:
                queryWrapper.eq(Help::getSupervisorId,user.getId());
                break;
            case 2:
                //管理员-全平台会话（即所有的咨询记录列表）
                break;
            default:
                return new ArrayList<>();
        }
        log.info("searchValue:{}",searchValue);
        if(fromDate!=null){
            LocalDateTime from = LocalDateTime.parse(fromDate+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Help::getStartTime,fromDate);
        }
        if(toDate!=null){
            LocalDateTime to = LocalDateTime.parse(toDate+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(Help::getEndTime,toDate);
        }
        queryWrapper.orderByDesc(Help::getCreateTime);
        List<Help> helps=this.baseMapper.selectList(queryWrapper);
        List<HelpDTO> helpDTOS=new ArrayList<>();
        for(Help h:helps){
            Consultant consultant = consultantMapper.selectById(h.getConsultantId());
            Supervisor supervisor = supervisorMapper.selectById(h.getSupervisorId());
            if(consultant==null||supervisor==null){
                continue;
            }

            if(searchValue==null ||
                    (searchValue!=null&&(consultant.getName().contains(searchValue)||supervisor.getName().contains(searchValue)))) {
                Duration duration = Duration.between(h.getStartTime(), h.getEndTime());
                HelpDTO helpDTO = HelpDTO.builder()
                        .id(h.getId())

                        .consultantId(consultant.getId())
                        .consultantName(consultant.getName())
                        .consultantAvatar(consultant.getAvatar())

                        .supervisorId(supervisor.getId())
                        .supervisorName(supervisor.getName())
                        .supervisorAvatar(supervisor.getAvatar())

                        .startTime(h.getStartTime())
                        .continueTime((int) duration.getSeconds())

                        .chatId(h.getChatId())
                        .build();
                helpDTOS.add(helpDTO);
            }
        }
        return helpDTOS;
    }

    @Override
    public ResultInfo addHelp(Integer chatId) {
        LambdaQueryWrapper<Help> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Help::getChatId,chatId);
        /**
         * 已经根据该chatId创建help，error
         */
        int count=this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            return ResultInfo.error("该chat已创建过help,id:"+this.baseMapper.selectOne(queryWrapper).getId());
        }
        /**
         * 查chat是否存在,是否是求助类型
         */
        Chat chat = chatMapper.selectById(chatId);
        if(chat==null||!chat.getType().equals( TypeConstant.HELP_CHAT)){
            return ResultInfo.error("该chat不存在,id:"+chatId);
        }
        /**
         * 根据chat查咨询师和督导
         * userA 咨询师
         * userB 督导
         */
        Consultant consultant = consultantMapper.selectById(chat.getUserA());
        if(consultant==null){
            return ResultInfo.error("该chat的咨询师不存在,id:"+chat.getUserA());
        }//封禁也返回

        Supervisor supervisor = supervisorMapper.selectById(chat.getUserB());
        if(supervisor==null){
            return ResultInfo.error("该chat的督导不存在,id:"+chat.getUserB());
        }//封禁也返回


        /**
         * 创建help
         */
        Duration duration = Duration.between(chat.getStartTime(), chat.getEndTime());
        Help help=Help.builder()
                .isDeleted(0)
                .consultantId(consultant.getId())
                .supervisorId(supervisor.getId())
                .startTime(chat.getStartTime())
                .endTime(chat.getEndTime())
                .chatId(chatId)
                .duration((int) duration.getSeconds())
                .build();
        this.baseMapper.insert(help);
        return ResultInfo.success(help.getId());
    }
}

