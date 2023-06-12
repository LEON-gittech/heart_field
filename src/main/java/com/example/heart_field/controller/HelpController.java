package com.example.heart_field.controller;

import cn.hutool.core.util.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.HelpDTO;
import com.example.heart_field.dto.consultant.record.RecordDTO;
import com.example.heart_field.dto.consultant.record.RecordPage;
import com.example.heart_field.param.AddHelpParam;
import com.example.heart_field.service.HelpService;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.tokens.AdminOrSupervisorToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author albac0020@gmail.com
 * data 2023/5/28 12:02 PM
 */

@Slf4j
@RestController
@AdminOrSupervisorToken
public class HelpController {

    @Autowired
    private HelpService helpService;

    /**
     *
     根据不同角色返回对话列表（即咨询记录列表）
     * 咨询师-自己负责的咨询会话
     * 督导/管理员-全平台会话（即所有的咨询记录列表）
     * @return
     */
    @GetMapping("/records/assistance")
    public R getConsultRecords(@RequestParam(value = "searchValue", required = false) String searchValue,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                               @RequestParam(value = "pageNum", required = false,defaultValue = "1") int pageNum,
                               @RequestParam(value = "fromDate", required = false) String fromDate,
                               @RequestParam(value = "toDate", required = false) String toDate){
        List<HelpDTO> resultInfo = helpService.queryRecords(searchValue, pageSize, pageNum, fromDate, toDate);
        int total = resultInfo.size();
        int pages = PageUtil.totalPage(total, pageSize);
        int fromIndex = (pageNum-1)*pageSize;
        int toIndex = pageNum*pageSize>total?total:pageNum*pageSize;
        if(pageNum>pages){
            return R.success(new RecordPage<RecordDTO>(new ArrayList<>(), pages, total));
        }
        List<HelpDTO> subList = resultInfo.subList(fromIndex, toIndex);
        RecordPage<HelpDTO> resPage = new RecordPage<HelpDTO>(subList, pages, total);
        return R.success(resPage);
    }

    @PostMapping("/records/supervisor")
    public R addConsultRecord(@RequestBody AddHelpParam param){
        ResultInfo resultInfo = helpService.addHelp(param.getChatId());
        return resultInfo.isRight()
                 ?R.success(resultInfo.getData())
                :R.error(resultInfo.getMessage());
    }

}
