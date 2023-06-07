package com.example.heart_field.controller;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.consultant.record.RecordDTO;
import com.example.heart_field.dto.consultant.record.RecordListDTO;
import com.example.heart_field.dto.consultant.record.RecordPage;
import com.example.heart_field.param.VisitorCommentParam;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.tokens.StaffToken;
import com.example.heart_field.tokens.UserLoginToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     *
        根据不同角色返回对话列表（即咨询记录列表）
     * 咨询师-自己负责的咨询会话
     * 督导/管理员-全平台会话（即所有的咨询记录列表）
     * @return
     */
    //@StaffToken
    @GetMapping("/records/consult")
    public R getConsultRecords(@RequestParam(value = "searchValue", required = false) String searchValue,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                               @RequestParam(value = "pageNum", required = false,defaultValue = "1") int pageNum,
                               @RequestParam(value = "fromDate", required = false) String fromDate,
                               @RequestParam(value = "toDate", required = false) String toDate){

        List<RecordDTO> resultInfo = recordService.queryRecords(searchValue, pageSize, pageNum, fromDate, toDate);
        log.info("从controller中："+resultInfo);
        int pages = PageUtil.totalPage(resultInfo.size(), pageSize);
        int total = resultInfo.size();
        log.info("总页数："+pages);
        Page<RecordDTO> resPage = new Page<RecordDTO>(pageNum,pageSize,total).setRecords(resultInfo);
        log.info("当前页："+resPage.getRecords());
        //current – 当前页 ,size – 每页显示条数
        RecordPage<RecordDTO> res = new RecordPage<RecordDTO>(resPage,pages,total);
        log.info("返回结果："+res);
        return R.success(resPage);
    }

    @PostMapping("/records/consult")
    public R addConsultRecord(@RequestParam(value = "chatId", required = true) Integer chatId){
        ResultInfo<String> resultInfo = recordService.addRecordByChatId(chatId);
        return resultInfo.isRight()
                 ?R.success(resultInfo.getData())
                 :R.error(resultInfo.getMessage());
    }

    /**
     * 访客评价咨询师
     */
    @PostMapping("/visitors/{record-id}/comment")
    public R addComment(@PathVariable(value = "record_id", required=true)Integer recordId,
                        @RequestBody VisitorCommentParam commentParam
                        ){
        ResultInfo<String> resultInfo = recordService.addComment(recordId,commentParam.getComment(),commentParam.getScore());
        return resultInfo.isRight()
                ?R.success(resultInfo.getData())
                :R.error(resultInfo.getMessage());
    }
}
