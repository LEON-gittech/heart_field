package com.example.heart_field.controller;

import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.param.ChatEndParam;
import com.example.heart_field.param.ChatParam;
import com.example.heart_field.service.ChatService;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.tokens.UserLoginToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chat")
@UserLoginToken
public class ChatController {
    @Autowired
    private ConsultantService consultantService;

    @Autowired
    private ChatService chatService;

    /**
     * 更新咨询师正在进行的会话数
     * @param consultantId
     * @param body
     * @return
     */
    @PutMapping("/{consultantId}/concurrent-count")
    public R<String> updateConcurrentCount(@PathVariable String consultantId, @RequestBody Map body) {
        Integer concurrentCount = (Integer) body.get("concurrentCount");
        Consultant consultant = consultantService.getById(consultantId);
        consultant.setCurrentSessionCount(concurrentCount);
        consultantService.updateById(consultant);
        return R.success("更新咨询师正在进行的会话数成功");
    }

    /**
     * 返回咨询师当前进行的会话数
     */
    @Data
    class ConcurrentCountDto{
        Integer concurrentCount;
    }
    @GetMapping("/{consultantId}/concurrent-count")
    public R<ConcurrentCountDto> getConcurrentCount(@PathVariable String consultantId) {
        Consultant consultant = consultantService.getById(consultantId);
        ConcurrentCountDto concurrentCountDto = new ConcurrentCountDto();
        concurrentCountDto.setConcurrentCount(consultant.getCurrentSessionCount());
        return R.success(concurrentCountDto);
    }


    @PostMapping
    public R createChat(@RequestBody ChatParam chat){
        ResultInfo resultInfo = chatService.createChat(chat);
        return resultInfo.isRight()
                ? R.success(resultInfo.getData())
                : R.error(resultInfo.getMessage());
    }

    @PutMapping
    public R endChat(@RequestBody ChatEndParam chat){
        ResultInfo resultInfo = chatService.endChat(chat.getChatId());
        return resultInfo.isRight()
                ? R.success("结束成功")
                : R.error(resultInfo.getMessage());
    }




}
