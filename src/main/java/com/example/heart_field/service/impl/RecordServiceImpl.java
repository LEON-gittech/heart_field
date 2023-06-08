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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public List<RecordListDTO> getRecords(Integer visitorId,Integer pageSize, Integer pageNum) {
        LambdaQueryWrapper<Record> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(Record::getVisitorId,visitorId);
        //Integer count=this.baseMapper.selectCount(queryWrapper);

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
    public List<RecordDTO> queryRecords(String searchValue, int pageSize, int pageNum, String fromDate, String toDate) {

        LambdaQueryWrapper<Record> queryWrapper = Wrappers.lambdaQuery();
        User user = TokenUtil.getTokenUser();
        //if (user == null) { return new ArrayList<>();}
        switch (user.getType()) {
            ////type为0是Visitor，1是Consultant，2是Admin，3是Supervisor
            case 0:
                return new ArrayList<>();
            case 1:
                //咨询师-自己负责的咨询会话
                queryWrapper.eq(Record::getConsultantId, user.getId());
                break;
            default:
                //督导/管理员-全平台会话（即所有的咨询记录列表）
                //log.info("咨询师或督导");
                break;
        }
        log.info("searchValue:{}", searchValue);
        if (fromDate != null) {
            LocalDateTime from = LocalDateTime.parse(fromDate+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("from:{}", from);
            queryWrapper.ge(Record::getCreateTime, from);
        }
        if (toDate != null) {
            LocalDateTime to = LocalDateTime.parse(toDate+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("to:{}", to);
            queryWrapper.le(Record::getCreateTime, to);
        }

        queryWrapper.orderByDesc(Record::getCreateTime);
        List<Record> records = this.baseMapper.selectList(queryWrapper);
        List<RecordDTO> recordDTOS = new ArrayList<>();
        for (Record r : records) {
            Visitor visitor = visitorMapper.selectById(r.getVisitorId());
            Consultant consultant = consultantMapper.selectById(r.getConsultantId());
            if(consultant==null||visitor==null){
                continue;
            }
            if (searchValue == null
                    || (searchValue != null && (visitor.getName().contains(searchValue) || visitor.getUsername().contains(searchValue)
                             || consultant.getName().contains(searchValue)))) {
                log.info("有符合的记录");
                RecordDTO recordDTO = RecordDTO.builder()
                        .id(r.getId())
                        .visitorId(visitor.getId())
                        .visitorName(visitor.getName())
                        .visitorAvatar(visitor.getAvatar())

                        .consultantId(consultant.getId())
                        .consultantName(consultant.getName())
                        .consultantAvatar(consultant.getAvatar())

                        .consultRank(r.getVisitorScore())
                        .consultComment(r.getVisitorComment())

                        .startTime(r.getStartTime())
                        .continueTime(r.getDuration())

                        .chatId(r.getChatId())
                        .build();
                recordDTOS.add(recordDTO);
                log.info("recordDTO:{}", recordDTO);
            }
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
        if(chat.getEndTime()==null){
            return ResultInfo.error("该chat还未结束,id:"+chatId);
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

        Duration duration = Duration.between(chat.getStartTime(), chat.getEndTime());
        Record record=Record.builder()
                .chatId(chatId)
                .isCompleted(0)//未填写评价、评分，未完成
                .visitorId(visitor.getId())
                .consultantId(consultant.getId())
                .startTime(chat.getStartTime())
                .endTime(chat.getEndTime())
                .duration((int) duration.getSeconds())
                .build();
        this.baseMapper.insert(record);
        return ResultInfo.success(record);

    }

    @Override
    public ResultInfo addComment(Integer recordId, String comment, Integer score) {
        Record record = this.baseMapper.selectById(recordId);
        if(record==null){
            return ResultInfo.error("该record不存在,id:"+recordId);
        }
        if(record.getIsCompleted()==1){
            return ResultInfo.error("该record已经完成评价，请勿重复评价，id:"+recordId);
        }
        if(record.getVisitorId()!=TokenUtil.getTokenUser().getUserId()){
            return ResultInfo.error("该record不属于当前访客，id:"+recordId);
        }
        record.setVisitorComment(comment);
        record.setVisitorScore(score);
        record.setIsCompleted(1);
        this.baseMapper.updateById(record);
        return ResultInfo.success();
    }
}
