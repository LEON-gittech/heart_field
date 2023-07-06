package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.binding.SupervisorBinding;
import com.example.heart_field.dto.consultant.ConsultantsDto;
import com.example.heart_field.dto.consultant.comment.CommentDto;
import com.example.heart_field.dto.consultant.comment.CommentsDto;
import com.example.heart_field.entity.*;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.TokenService;
import com.example.heart_field.utils.TokenUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
            consultant.setIsOnline(0);
            consultant.setCurrentSessionCount(0);
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
    public Comments getCommentDto(Integer consultantId, Integer page, Integer pageSize) {
        Page<Record> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Record::getConsultantId,consultantId);
        recordService.page(pageInfo,queryWrapper);
        Comments comments = new Comments();
        //修改pageNum
        comments.setPageNum( ((Long)pageInfo.getPages()).intValue());
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
        comments.setCommentDtos(list);
        return comments;
    }
    @Data
    public class Comments{
        List<CommentDto> commentDtos;
        Integer pageNum;
    }
    @Override
    public CommentsDto getCommentsDto(CommentsDto commentsDto,Integer consultantId, Integer page, Integer pageSize) {
        Comments comments = getCommentDto(consultantId,page,pageSize);
        commentsDto.setPageNum(comments.getPageNum());
        commentsDto.setCommentsDto(comments.getCommentDtos());
        return commentsDto;
    }

    @Override
    public List<Consultant> getConsultants(String searchValue, Integer sort, Integer sortType, Integer page, Integer pageSize, ConsultantsDto consultantsDto) {
        //构造分页构造器
        Page<Consultant> pageinfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Consultant> queryWrapper = new LambdaQueryWrapper<>();

        //根据searchValue对姓名，简介，详细介绍，标签进行模糊查询
        if(!(searchValue == null ||searchValue.equals(""))){
            queryWrapper.and(wrapper -> wrapper
                    .like(Consultant::getName, searchValue)
                    .or()
                    .like(Consultant::getBriefIntro, searchValue)
                    .or()
                    .like(Consultant::getDetailedIntro, searchValue)
                    .or()
                    .like(Consultant::getExpertiseTag, searchValue)
            );
        }
        //JSON_CONTAINS函数用于判断json数组中是否包含某个元素
        ;

        //按照sortType进行排序，并根据sort确认是升序还是降序
        if(sort == 0){
            switch (sortType){
                case 0:
                    queryWrapper.orderByDesc(Consultant::getRating);
                    break;
                case 1:
                    queryWrapper.orderByDesc(Consultant::getHelpNum);
                    break;
                case 2:
                    //这里因为空闲是0
                    queryWrapper.eq(Consultant::getCurStatus,0);
                    break;
                default:
                    break;
            }
        }
        else{
            switch (sortType){
                case 0:
                    queryWrapper.orderByAsc(Consultant::getRating);
                    break;
                case 1:
                    queryWrapper.orderByAsc(Consultant::getHelpNum);
                    break;
                case 2:
                    queryWrapper.eq(Consultant::getCurStatus,1);
                    break;
                default:
                    break;
            }
        }
        //角色是访客进行的筛选
        User user = TokenUtil.getTokenUser();
        Set<Integer> hasBindingConsultants;
        if(Objects.equals(user.getType(), TypeConstant.VISITOR)){
            //筛选在线的咨询师
            queryWrapper.eq(Consultant::getIsOnline,1);
            //筛选今天有绑定督导的咨询师
            List<Binding> bindings = bindingService.list();
            hasBindingConsultants = new HashSet<>();
            for(Binding binding : bindings){
                hasBindingConsultants.add(binding.getConsultantId());
            }
        } else {
            hasBindingConsultants = new HashSet<>();
        }
        //执行查询
        this.page(pageinfo,queryWrapper);
        List<Consultant> consultants = pageinfo.getRecords();
        //角色为访客
        if(Objects.equals(user.getType(), TypeConstant.VISITOR)){
            //筛选有绑定督导的咨询师
            consultants.removeIf(consultant -> !hasBindingConsultants.contains(consultant.getId()));
        }
        Integer pageNum = Math.toIntExact(pageinfo.getPages());
        consultantsDto.setPageNum(pageNum);
        return consultants;
    }
}
