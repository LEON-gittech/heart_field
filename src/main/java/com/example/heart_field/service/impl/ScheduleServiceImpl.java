package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.schedule.ConsultantScheDTO;
import com.example.heart_field.dto.ScheduleDTO;
import com.example.heart_field.dto.supervisor.SupervisorDTO;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Schedule;
import com.example.heart_field.entity.Supervisor;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.ScheduleMapper;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**todo:clm - 对于信息不完善、被封禁的用户是否展示？
     *          - 默认表中已去重
     */

    //todo:clm - 修改时避免重复添加
    @Override
    public ResultInfo<List<ScheduleDTO>> getAllSchedules() {
        List<ScheduleDTO> schedules = new ArrayList<>();
        for(int today=1;today<=31;today++){
            //咨询师
            LambdaQueryWrapper<Schedule> queryWrapper_consultant= Wrappers.lambdaQuery();
            queryWrapper_consultant.eq(Schedule::getStaffType, TypeConstant.CONSULTANT).eq(Schedule::getWorkday,today);
            //Integer count_consultant=this.baseMapper.selectCount(queryWrapper_consultant);
            //根据schedule表中的consultant id ，去consultant表中查询consultant的信息
            List<ConsultantScheDTO> consultantDTOList = this.baseMapper.selectList(queryWrapper_consultant).stream()
//                    .filter(
//                            schedule -> {
//                                Integer consultantId = schedule.getStaffId();
//                                LambdaQueryWrapper<Consultant> queryWrapper_consultantDTO = Wrappers.lambdaQuery();
//                                Consultant consultant=consultantMapper.selectOne(queryWrapper_consultantDTO.eq(Consultant::getId,consultantId));
//                                if(consultant.getIsDisabled()==0&&consultant.getIsValid()==1){
//                                    return true;
//                                }else{
//                                    return false;
//                                }
//                            }
//                    )
                    .map(schedule -> {
                Integer consultantId = schedule.getStaffId();
                LambdaQueryWrapper<Consultant> queryWrapper_consultantDTO = Wrappers.lambdaQuery();
                Consultant consultant=consultantMapper.selectOne(queryWrapper_consultantDTO.eq(Consultant::getId,consultantId));

                ConsultantScheDTO consultantDTO = ConsultantScheDTO.builder()
                        .consultantId(consultant.getId())
                        .consultantName(consultant.getName())
                        .consultantAvatar(consultant.getAvatar())
                        .isDisabled(consultant.getIsDisabled())
                        .isValid(consultant.getIsValid())
                        .build();
                return consultantDTO;

            }).collect(Collectors.toList());
            //督导
            LambdaQueryWrapper<Schedule> queryWrapper_supervisor= Wrappers.lambdaQuery();
            queryWrapper_supervisor.eq(Schedule::getStaffType, TypeConstant.SUPERVISOR).eq(Schedule::getWorkday,today);
            //Integer count_supervisor=this.baseMapper.selectCount(queryWrapper_supervisor);
            //根据schedule表中的supervisor id ，去supervisor表中查询supervisor的信息
            List<SupervisorDTO> supervisorDTOList = this.baseMapper.selectList(queryWrapper_supervisor).stream()
//                    .filter(
//                            schedule -> {
//                                Integer supervisorId = schedule.getStaffId();
//                                LambdaQueryWrapper<Supervisor> queryWrapper_supervisorDTO = Wrappers.lambdaQuery();
//                                Supervisor supervisor=supervisorMapper.selectOne(queryWrapper_supervisorDTO.eq(Supervisor::getId,supervisorId));
//                                if(supervisor.getIsDisabled()==0&&supervisor.getIsValid()==1){
//                                    return true;
//                                }else{
//                                    return false;
//                                }
//                            }
//                    )
                    .map(schedule -> {
                Integer supervisorId = schedule.getStaffId();
                LambdaQueryWrapper<Supervisor> queryWrapper_supervisorDTO = Wrappers.lambdaQuery();
                Supervisor supervisor=supervisorMapper.selectOne(queryWrapper_supervisorDTO.eq(Supervisor::getId,supervisorId));
                SupervisorDTO supervisorDTO = SupervisorDTO.builder()
                        .id(supervisor.getId())
                        .supervisorName(supervisor.getName())
                        .avatar(supervisor.getAvatar())
                        .isValid(supervisor.getIsValid())
                        .isDisabled(supervisor.getIsDisabled())
                        .build();
                return supervisorDTO;
            }).collect(Collectors.toList());

            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .consultantCount(consultantDTOList.size())
                    .supervisorCount(supervisorDTOList.size())
                    .consultantList(consultantDTOList)
                    .supervisorList(supervisorDTOList)
                    .date(today)
                    .build();
            schedules.add(scheduleDTO);
        }
        return ResultInfo.success(schedules);
        }
}
