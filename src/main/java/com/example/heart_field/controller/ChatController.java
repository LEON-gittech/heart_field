package com.example.heart_field.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.ChatDetailDto;
import com.example.heart_field.entity.Chat;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Message;
import com.example.heart_field.entity.Record;
import com.example.heart_field.param.ChatEndParam;
import com.example.heart_field.param.ChatParam;
import com.example.heart_field.service.ChatService;
import com.example.heart_field.service.ConsultantService;
import com.example.heart_field.service.MessageService;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.tokens.UserLoginToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private MessageService messageService;
    @Autowired
    private RecordService recordService;

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
                ? R.success(resultInfo.getData())
                : R.error(resultInfo.getMessage());
    }

    @GetMapping("/detail")
    public R<ChatDetailDto> getChatDetail(HttpServletRequest request){
        Integer recordId = Integer.parseInt(request.getParameter("recordId"));
        Integer type = Integer.valueOf(request.getParameter("type"));
        //获取 chatId
        LambdaQueryWrapper<Record> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Record::getId, recordId);
        Record record = recordService.getOne(queryWrapper2);
        Integer chatId = record.getChatId();
        //获取对应 type,id 的 chat
        LambdaQueryWrapper<Chat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chat::getType, type);
        queryWrapper.eq(Chat::getId, chatId);
        Chat chat = chatService.getOne(queryWrapper);
        //获取对应 chat 的所有 message
        LambdaQueryWrapper<Message> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Message::getChatId, chat.getId());
        List<Message> messages = messageService.list(queryWrapper1);
        //构造 DTO
        ChatDetailDto chatDetailDto = new ChatDetailDto();
        chatDetailDto.setEvaluation(record.getEvaluation());
        chatDetailDto.setConsultType(record.getConsultType());
        //遍历 messages
        List<ChatDetailDto.Message> messages2 = new ArrayList<>();
        for (Message message : messages) {
            ChatDetailDto.Message message1 = new ChatDetailDto.Message();
            message1.setTime(String.valueOf(message.getSendTime()));
            message1.setSenderName(message.getSenderName());
            message1.setType(String.valueOf(message.getType()));
            message1.setContent(message.getContent());
            messages2.add(message1);
        }
        chatDetailDto.setMessages(messages2);
        return R.success(chatDetailDto,"获取聊天记录详情成功");
    }
}
