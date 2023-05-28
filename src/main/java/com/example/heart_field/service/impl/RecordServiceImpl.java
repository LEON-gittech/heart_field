package com.example.heart_field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.constant.TypeConstant;
import com.example.heart_field.dto.consultant.record.RecordDTO;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import com.example.heart_field.entity.*;
import com.example.heart_field.entity.Record;
import com.example.heart_field.mapper.*;
import com.example.heart_field.service.ChatService;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/15 9:31 AM
 */
@Service
@Slf4j
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ConsultantMapper consultantMapper;

    @Autowired
    private SupervisorMapper supervisorMapper;

    @Autowired
    private ChatMapper chatMapper;



    @Override
    public List<RecordListDTO> getRecords(String visitorId, String state,Integer pageSize, Integer pageNum) {
        LambdaQueryWrapper<Record> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Record::getVisitorId,visitorId);
        //Integer count=this.baseMapper.selectCount(queryWrapper);
        if(state!=null&&state.equals("end")){
            queryWrapper.eq(Record::getIsCompleted,1);
        }
        if(state!=null&&state.equals("ing")){
            queryWrapper.eq(Record::getIsCompleted,0);
        }
        queryWrapper.orderByDesc(Record::getCreateTime);
        List<Record> records=this.baseMapper.selectList(queryWrapper);
        List<RecordListDTO> recordListDTOS=new ArrayList<>();
        if(records==null){
            return recordListDTOS;
        }
        for(Record r:records){
            RecordListDTO rlDTO=r.convert2ListDTO();
            //Integer visId=rlDTO.getVisitorId();
            Visitor visitor=visitorMapper.selectById(rlDTO.getVisitorId());
            rlDTO.setVisitorName(visitor.getName());
            rlDTO.setVisitorAvatar(visitor.getAvatar());

            Consultant consultant= consultantMapper.selectById(rlDTO.getConsultantId());
            rlDTO.setConsultantName(consultant.getName());
            rlDTO.setConsultantAvatar(consultant.getAvatar());

            Integer supervisorId=rlDTO.getSupervisorId();
            if(supervisorId==null){
                rlDTO.setSupervisorName("暂无");
                rlDTO.setSupervisorAvatar("暂无");
                recordListDTOS.add(rlDTO);
                continue;
            }
            Supervisor supervisor= supervisorMapper.selectById(rlDTO.getSupervisorId());
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
        return recordListDTOS;
    }

    /**
     * 根据不同角色返回对话列表（即咨询记录列表）
     *      * 咨询师-自己负责的咨询会话
     *      * 督导/管理员-全平台会话（即所有的咨询记录列表）
     * @param searchValue
     * @param pageSize
     * @param pageNum
     * @param fromDate
     * @param toDate
     * @return
     */
    @Override
    public List<RecordDTO> queryRecords(String searchValue, int pageSize, int pageNum, LocalDateTime fromDate, LocalDateTime toDate) {
        LambdaQueryWrapper<Record> queryWrapper= Wrappers.lambdaQuery();
        User user = TokenUtil.getTokenUser();
        //if (user == null) { return new ArrayList<>();}
        switch (user.getType()){
            ////type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
            case 0:
                return new ArrayList<>();
            case 1:
                //咨询师-自己负责的咨询会话
                queryWrapper.eq(Record::getConsultantId,user.getId());
                break;
            default:
                //督导/管理员-全平台会话（即所有的咨询记录列表）
                break;
        }
        log.info("searchValue:{}",searchValue);
        if(fromDate!=null){
            queryWrapper.ge(Record::getCreateTime,fromDate);
        }
        if(toDate!=null){
            queryWrapper.le(Record::getCreateTime,toDate);
        }
        if(searchValue!=null){
            queryWrapper
                    .or(wrapper->{
                        wrapper.like(Record::getVisitorName,searchValue);
                    })
                    .or(wrapper->{
                        wrapper.like(Record::getVisitorUsername,searchValue);
                    })
                    .or(wrapper->{
                        wrapper.like(Record::getConsultantName,searchValue);
                    })
                    .or(wrapper->{
                        wrapper.like(Record::getSupervisorName,searchValue);
                    });
        }
        queryWrapper.orderByDesc(Record::getCreateTime);
        List<Record> records=this.baseMapper.selectList(queryWrapper);
        List<RecordDTO> recordDTOS=new ArrayList<>();
        for(Record r:records){
            RecordDTO recordDTO=RecordDTO.builder()
                    .id(r.getId())
                    .visitorId(r.getVisitorId())
                    .visitorName(r.getVisitorName())
                    .visitorAvatar(r.getVisitorAvatar())

                    .consultantId(r.getConsultantId())
                    .consultantName(r.getConsultantName())
                    .consultantAvatar(r.getConsultantAvatar())

                    .consultRank(r.getVisitorScore())
                    .consultComment(r.getVisitorComment())

                    .startTime(r.getCreateTime())
                    .continueTime(r.getEndTime().getSecond()-r.getCreateTime().getSecond())

                    .chatId(r.getChatId())
                    .build();
           recordDTOS.add(recordDTO);
        }
        return recordDTOS;
    }

    /**
     * 根据chatId生成当前会话的咨询记录
     * @param chatId
     * @return
     */
    @Override
    public ResultInfo addRecordByChatId(Integer chatId) {
        LambdaQueryWrapper<Record> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Record::getChatId,chatId);
        /**
         * 已经根据该chatId创建record，error
         */
        int count=this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            return ResultInfo.error("该chat已创建过record,id:"+this.baseMapper.selectOne(queryWrapper).getId());
        }
        /**
         * 查chat是否存在,是否是咨询类型
         */
        Chat chat = chatMapper.selectById(chatId);
        if(chat==null||chat.getType()!=TypeConstant.COUNSEL_CHAT){
            return ResultInfo.error("该chat不存在,id:"+chatId);
        }
        /**
         * 根据chat查咨询师和访客
         *
         *     // 咨询会话-访客；求助会话-咨询师
         *     private Integer userA;
         *
         *     //聊天的接受者
         *     //咨询会话-咨询师，求助会话-督导
         *     private Integer userB;
         */
        Visitor visitor = visitorMapper.selectById(chat.getUserA());
        if(visitor==null){
            return ResultInfo.error("该chat的访客不存在,id:"+chat.getUserA());
        }//即使封禁也正常返回

        Consultant consultant = consultantMapper.selectById(chat.getUserB());
        if(consultant==null){
            return ResultInfo.error("该chat的咨询师不存在,id:"+chat.getUserB());
        }//即使封禁也正常返回


        Record record=Record.builder()
                .chatId(chatId)
                .isCompleted(0)//未填写评价、评分，未完成
                .visitorId(visitor.getId())
                .consultantId(consultant.getId())
                .isDeleted(1)
                .startTime(chat.getStartTime())
                .endTime(chat.getEndTime())
                .build();
        this.baseMapper.insert(record);
        return ResultInfo.success(record.getId());

    }
}
