package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import com.example.heart_field.entity.*;
import com.example.heart_field.entity.Record;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.RecordMapper;
import com.example.heart_field.mapper.SupervisorMapper;
import com.example.heart_field.mapper.VisitorMapper;
import com.example.heart_field.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:31 AM
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Autowired
    private VisitorMapper VisitorMapper;

    @Autowired
    private ConsultantMapper ConsultantMapper;

    @Autowired
    private SupervisorMapper SupervisorMapper;

    @Override
    public ResultInfo<List<RecordListDTO>> getRecords(String visitorId, String state) {
        LambdaQueryWrapper<Record> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Record::getVisitorId,visitorId);
        //Integer count=this.baseMapper.selectCount(queryWrapper);
        if(state!=null&&state.equals("end")){
            queryWrapper.eq(Record::getIsCompleted,1);
        }
        if(state!=null&&state.equals("ing")){
            queryWrapper.eq(Record::getIsCompleted,0);
        }

        List<Record> records=this.baseMapper.selectList(queryWrapper);
        List<RecordListDTO> recordListDTOS=new ArrayList<>();
        if(records==null){
            return ResultInfo.success(recordListDTOS);
        }
        for(Record r:records){
            RecordListDTO rlDTO=r.convert2ListDTO();
            //Integer visId=rlDTO.getVisitorId();
            Visitor visitor=VisitorMapper.selectById(rlDTO.getVisitorId());
            rlDTO.setVisitorName(visitor.getName());
            rlDTO.setVisitorAvatar(visitor.getAvatar());

            Consultant consultant= ConsultantMapper.selectById(rlDTO.getConsultantId());
            rlDTO.setConsultantName(consultant.getName());
            rlDTO.setConsultantAvatar(consultant.getAvatar());

            Integer supervisorId=rlDTO.getSupervisorId();
            if(supervisorId==null){
                rlDTO.setSupervisorName("暂无");
                rlDTO.setSupervisorAvatar("暂无");
                recordListDTOS.add(rlDTO);
                continue;
            }
            Supervisor supervisor= SupervisorMapper.selectById(rlDTO.getSupervisorId());
            if(supervisor==null){
                rlDTO.setSupervisorName("暂无");
                rlDTO.setSupervisorAvatar("暂无");
            }
            else{
                rlDTO.setSupervisorName(supervisor.getName());
                rlDTO.setSupervisorAvatar(supervisor.getAvatar());
            }
            recordListDTOS.add(rlDTO);
        }
        return ResultInfo.success(recordListDTOS);
    }
}
