package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.consultant.binding.SupervisorBinding;
import com.example.heart_field.dto.consultant.comment.CommentDto;
import com.example.heart_field.dto.consultant.comment.CommentsDto;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.TokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultantServiceImpl extends ServiceImpl<ConsultantMapper, Consultant> implements ConsultantService {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private BindingService bindingService;
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private TokenService tokenService;

    //重置Consultant的每日属性
    public void resetDailyProperties() {
        List<Consultant> consultants = this.list();
        for(Consultant consultant : consultants) {
            consultant.setTodayTotalHelpCount(0);
            consultant.setTodayTotalHelpTime(0);
            this.updateById(consultant);
        }
    }

    @Override
    public List<Integer> getWorkArrangement(Consultant consultant) {
        LambdaQueryWrapper<Schedule> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Schedule::getStaffId,consultant.getId());
        queryWrapper1.eq(Schedule::getStaffType, TypeConstant.CONSULTANT);
        List<Schedule> schedules = scheduleService.list(queryWrapper1);
        List<Integer> workArrangement = new ArrayList<>();
        for(Schedule schedule : schedules){
            workArrangement.add(schedule.getWorkday());
        }
        return workArrangement;
    }

    @Override
    public List<SupervisorBinding> getSupervisorBindings(Consultant consultant) {
        List<Binding> bindings = bindingService.list(new LambdaQueryWrapper<Binding>().eq(Binding::getConsultantId,consultant.getId()));
        List<SupervisorBinding> supervisorBindings = new ArrayList<>();
        for(Binding binding : bindings){
            SupervisorBinding supervisorBinding = new SupervisorBinding();
            //获取督导姓名
            String supervisorName = supervisorService.getById(binding.getSupervisorId()).getName();
            supervisorBinding.setSupervisorName(supervisorName);
            supervisorBinding.setId(String.valueOf(binding.getSupervisorId()));
            supervisorBindings.add(supervisorBinding);
        }
        return supervisorBindings;
    }

    @Override
    public List<CommentDto> getCommentDto(Integer consultantId, Integer page, Integer pageSize, Integer pageNum) {
        Page<Record> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Record::getConsultantId,consultantId);
        recordService.page(pageInfo,queryWrapper);
        //修改pageNum
        pageNum = Math.toIntExact(pageInfo.getPages());
        //转为返回给前端的CommentDto
        Page<CommentDto> commentDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,commentDtoPage,"records");
        List<Record> records = pageInfo.getRecords();
        List<CommentDto> list = records.stream().map((item) ->{
            CommentDto commentDto = new CommentDto();
            BeanUtils.copyProperties(item,commentDto);
            //consultantId
            commentDto.setConsultantId(item.getConsultantId().toString());
            //visitorScore
            commentDto.setVisitorScore(item.getVisitorScore());
            //visitorId
            commentDto.setVisitorId(item.getVisitorId().toString());
            Integer visitorId = item.getVisitorId();
            //根据id获取userName和userAvator
            Visitor visitor = visitorService.getById(visitorId);
            if(visitor != null){
                String userName = visitor.getName();
                String userAvator = visitor.getAvatar();
                commentDto.setUserName(userName);
                commentDto.setUserAvatar(userAvator);
            }
            //commentTime
            commentDto.setCommentTime(item.getEndTime().toString());
            return commentDto;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public CommentsDto getCommentsDto(CommentsDto commentsDto,Integer consultantId, Integer page, Integer pageSize) {
        Integer pageNum = 1;
        List<CommentDto> commentDtos = getCommentDto(consultantId,page,pageSize,pageNum);
        commentsDto.setPageNum(pageNum);
        commentsDto.setCommentsDto(commentDtos);
        return commentsDto;
    }



}
