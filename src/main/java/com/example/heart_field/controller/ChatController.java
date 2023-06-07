package com.example.heart_field.controller;

import com.example.heart_field.common.R;
import com.example.heart_field.common.result.ResultInfo;
import com.example.heart_field.dto.MessageIdDto;
import com.example.heart_field.entity.Chat;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.Message;
import com.example.heart_field.param.ChatEndParam;
import com.example.heart_field.param.ChatParam;
import com.example.heart_field.param.NewMessageParam;
import com.example.heart_field.service.*;
import com.example.heart_field.tokens.UserLoginToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private VisitorService visitorService;
    @Autowired
    private SupervisorService supervisorService;
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

    /**
     * 新增一条消息Message
     */
    @PostMapping("/message")
    public R<MessageIdDto> newMessage(@RequestBody NewMessageParam newMessage){
        log.info("进入新建---");
        log.info("传参：{}",newMessage);
        Message message = new Message();
        message.setChatId(newMessage.getChatId());
        log.info("chatId:{}",newMessage.getChatId());
        //先不考虑聊天记录转发，也就是消息类型不能为4
        if(newMessage.getMessageType()=="4"){
            return R.argument_error("消息类型暂不支持聊天记录");
        }
        message.setRelatedChat(0);
        message.setType(Byte.valueOf(newMessage.getMessageType()));
        message.setSendTime(Timestamp.valueOf(LocalDateTime.now()));
        message.setContent(newMessage.getContent());

        //message.setIsDeleted(false);
        //查找chat表，获取两天双方信息
        log.info("chat-------------");
        Chat chat = chatService.getById(newMessage.getChatId());
        log.info("chat:{}",chat);
        if(chat==null){
            return R.argument_error("chatId不存在");
        }
        log.info("会话类型：{}",chat.getType());
        if(chat.getType()==0){//访客与咨询师会话
            switch (newMessage.getSenderType()){
                case "2":
                    return R.auth_error("参数有误：会话类型与发送者类型矛盾");
                case "1":{
                    message.setSenderId(chat.getUserB());
                    message.setReceiverId(chat.getUserA());
                    String senderName = consultantService.getById(chat.getUserB()).getName();
                    message.setSenderName(senderName);
                    String receiverName = visitorService.getById(chat.getUserA()).getName();
                    message.setReceiverName(receiverName);
                    break;
                }
                case "0":{
                    message.setSenderId(chat.getUserA());
                    message.setReceiverId(chat.getUserB());
                    String receiverName = consultantService.getById(chat.getUserB()).getName();
                    String senderName = visitorService.getById(chat.getUserA()).getName();
                    message.setSenderName(senderName);
                    message.setReceiverName(receiverName);
                    break;
                }
            }
            message.setOwner(Byte.valueOf(newMessage.getSenderType()));

        } else if (chat.getType()==1) { //督导与咨询师会话
            switch (newMessage.getSenderType()){
                case "0":
                    return R.auth_error("参数有误：会话类型与发送者类型矛盾");
                case "1":{
                    message.setSenderId(chat.getUserA());
                    message.setReceiverId(chat.getUserB());
                    String senderName = consultantService.getById(chat.getUserA()).getName();
                    String receiverName = supervisorService.getById(chat.getUserB()).getName();
                    message.setSenderName(senderName);
                    message.setReceiverName(receiverName);
                    break;
                }
                case "2":{
                    message.setSenderId(chat.getUserB());
                    message.setReceiverId(chat.getUserA());
                    String receiverName = consultantService.getById(chat.getUserB()).getName();
                    String senderName = supervisorService.getById(chat.getUserA()).getName();
                    message.setSenderName(senderName);
                    message.setReceiverName(receiverName);
                    break;
                }
            }
            message.setOwner(Byte.valueOf(newMessage.getSenderType()));
        }
        messageService.save(message);
        MessageIdDto messageIdDto= new MessageIdDto();
        //log.info("--获取id--");
        Integer messageId = message.getId();
        messageIdDto.setId(String.valueOf(messageId));
        return R.success(messageIdDto);

    }



}
