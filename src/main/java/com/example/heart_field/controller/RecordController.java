package com.example.heart_field.controller;

import cn.hutool.core.util.PageUtil;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.record.RecordDTO;
import com.example.heart_field.dto.record.RecordPage;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.mapper.RecordMapper;
import com.example.heart_field.param.AddRecordParam;
import com.example.heart_field.param.VisitorCommentParam;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.tokens.StaffToken;
import com.example.heart_field.tokens.UserLoginToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/28 11:13 AM
 */
@Slf4j
@RestController
@UserLoginToken
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    ConsultantService consultantService;

    @Autowired
    ConsultantMapper consultantMapper;

    /**
     *
        根据不同角色返回对话列表（即咨询记录列表）
     * 咨询师-自己负责的咨询会话
     * 督导/管理员-全平台会话（即所有的咨询记录列表）
     * @return
     */
    @StaffToken
    @GetMapping("/records/consult")
    public R getConsultRecords(@RequestParam(value = "searchValue", required = false) String searchValue,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                               @RequestParam(value = "pageNum", required = false,defaultValue = "1") int pageNum,
                               @RequestParam(value = "fromDate", required = false) String fromDate,
                               @RequestParam(value = "toDate", required = false) String toDate){
        List<RecordDTO> resultInfo = recordService.queryRecords(searchValue, pageSize, pageNum, fromDate, toDate);
        int total = resultInfo.size();
        if(total==0) return R.success(new RecordPage<RecordDTO>(new ArrayList<>(), 0, 0));
        int pages = PageUtil.totalPage(total, pageSize);
        int fromIndex = (pageNum-1)*pageSize;
        int toIndex = pageNum*pageSize>total?total:pageNum*pageSize;
        if(pageNum>pages){
            return R.success(new RecordPage<RecordDTO>(new ArrayList<>(), pages, total));
        }
        List<RecordDTO> subList = resultInfo.subList(fromIndex, toIndex);
        RecordPage<RecordDTO> resPage = new RecordPage<RecordDTO>(subList, pages, total);
        return R.success(resPage);
    }

//    /*
//        根据chatId添加咨询记录
//     */
//    @UserLoginToken
//    @PostMapping("/records/consult")
//    public R addConsultRecord(@RequestBody AddRecordParam param){
//        ResultInfo<String> resultInfo = recordService.addRecordByChatId(param.getChatId());
//        return resultInfo.isRight()
//                 ?R.success(resultInfo.getData())
//                 :R.error(resultInfo.getMessage());
//    }

    /**
     * 访客评价咨询师
     */
    @PostMapping("/visitors/{record_id}/comment")
    @Transactional
    public R addComment(@PathVariable(value = "record_id", required=true)Integer recordId,
                        @RequestBody VisitorCommentParam commentParam
                        ){
        ResultInfo<String> resultInfo = recordService.addComment(recordId,commentParam.getComment(),commentParam.getRank());
        return resultInfo.isRight()
                ?R.success(resultInfo.getData())
                :R.error(resultInfo.getMessage());
    }

    @GetMapping("/test")
    @Transactional
    public void testAddComment(){
        List<Consultant> consultantList = consultantService.list();
        System.out.println("consultantList = " + consultantList);
        for(Consultant consultant:consultantList){
            List<Integer> scores = recordMapper.selectScoresByConsultantId(consultant.getId());
            int num=scores.size();
            if(num==0){
                consultant.setRating(-1.0);
                consultantMapper.updateById(consultant);
                continue;
            }
            int sum=0;
            for(Integer s:scores){
                sum+=s;
            }
            double average = (double)sum/num;
            consultant.setRating(average);
            consultantMapper.updateById(consultant);
        }
    }
}
