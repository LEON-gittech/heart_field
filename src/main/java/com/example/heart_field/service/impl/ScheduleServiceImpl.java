package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.ConsultantDTO;
import com.example.heart_field.dto.ScheduleDTO;
import com.example.heart_field.dto.SupervisorDTO;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Schedule;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.ScheduleMapper;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:33 AM
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    @Autowired
    private ConsultantMapper consultantMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    //todo:clm - 对于信息不完善、被封禁的用户是否展示？
    @Override
    public ResultInfo<ScheduleDTO> getAllSchedules() {
        Calendar calendar = Calendar.getInstance(); // get current instance of the calendar
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        //咨询师
        LambdaQueryWrapper<Schedule> queryWrapper_consultant= Wrappers.lambdaQuery();
        queryWrapper_consultant.eq(Schedule::getStaffType, TypeConstant.CONSULTANT).eq(Schedule::getWorkday,today);
        Integer count_consultant=this.baseMapper.selectCount(queryWrapper_consultant);
        //根据schedule表中的consultant id ，去consultant表中查询consultant的信息
        List<ConsultantDTO> consultantDTOList = this.baseMapper.selectList(queryWrapper_consultant).stream().map(schedule -> {
            Integer consultantId = schedule.getStaffId();
            LambdaQueryWrapper<Consultant> queryWrapper_consultantDTO = Wrappers.lambdaQuery();
            Consultant consultant=consultantMapper.selectOne(queryWrapper_consultantDTO.eq(Consultant::getId,consultantId));
            if(consultant.isDisabled()==true||consultant.isValid()==false){
                return null;
            }else{
                ConsultantDTO consultantDTO = ConsultantDTO.builder()
                        .id(consultant.getId())
                        .consultantName(consultant.getName())
                        .avatar(consultant.getAvatar())
                        .build();
                return consultantDTO;
            }
        }).toList();
        //督导
        LambdaQueryWrapper<Schedule> queryWrapper_supervisor= Wrappers.lambdaQuery();
        queryWrapper_supervisor.eq(Schedule::getStaffType, TypeConstant.SUPERVISOR).eq(Schedule::getWorkday,today);
        Integer count_supervisor=this.baseMapper.selectCount(queryWrapper_supervisor);
        //根据schedule表中的supervisor id ，去supervisor表中查询supervisor的信息
        List<SupervisorDTO> supervisorDTOList = this.baseMapper.selectList(queryWrapper_supervisor).stream().map(schedule -> {
            Integer supervisorId = schedule.getStaffId();
            LambdaQueryWrapper<Supervisor> queryWrapper_supervisorDTO = Wrappers.lambdaQuery();
            Supervisor supervisor=supervisorMapper.selectOne(queryWrapper_supervisorDTO.eq(Supervisor::getId,supervisorId));
            if(supervisor.isDisabled()==true||supervisor.isValid()==false){
                return null;
            }else{
                SupervisorDTO supervisorDTO = SupervisorDTO.builder()
                        .id(supervisor.getId())
                        .supervisorName(supervisor.getName())
                        .avatar(supervisor.getAvatar())
                        .build();
                return supervisorDTO;
            }
        }).toList();

        ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                .consultantCount(count_consultant)
                .supervisorCount(count_supervisor)
                .consultantList(consultantDTOList)
                .supervisorList(supervisorDTOList)
                .date(today)
                .build();
        return ResultInfo.success(scheduleDTO);
        }
}
