package com.example.heart_field;

import com.alibaba.fastjson.JSON;
import com.example.heart_field.dto.WxUserInfo;
import com.example.heart_field.entity.Help;
import com.example.heart_field.entity.Record;
import com.example.heart_field.mapper.RecordMapper;
import com.example.heart_field.service.HelpService;
import com.example.heart_field.service.MsgService;
import com.example.heart_field.service.RecordService;
import com.example.heart_field.service.impl.MsgServiceImpl;
import com.example.heart_field.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class HeartFieldApplicationTests {
    @Autowired
    private MsgService msgService;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private RecordService recordService;
    @Autowired
    private HelpService helpService;

    @Test
    void testTimeConvert(){
        String dateStr = "2021-08-19";
        LocalDateTime date2 = LocalDateTime.parse(dateStr+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(date2);

    }

    @Test
    void testJson(){
        String json="{\"nickName\":\"ZHY'\",\"gender\":0,\"language\":\"zh_CN\",\"city\":\"\",\"province\":\"\",\"country\":\"\",\"avatarUrl\":\"https://thirdwx.qlogo.cn/mmopen/vi_32/6X3UWePYiaUzbaGXplicYh5gQkElq3RYZgsCNtterro8V5V6o0tSsQYHH1S7Z5loe59zX7uWyf74PDS6FULWnCcw/132\",\"watermark\":{\"timestamp\":1685671920,\"appid\":\"wxb9f9c2c27af57638\"}}";
        WxUserInfo wxUserInfo = JSON.parseObject(json,WxUserInfo.class);
        System.out.println("用户信息：{}"+wxUserInfo);
    }

    @Test
    void testDuration(){
//        List<Record> records = recordService.list();
//        System.out.println("records = " + records);
//        for(Record record : records){
//            Duration duration = Duration.between(record.getStartTime(),record.getEndTime());
//            System.out.println("start="+record.getStartTime()+"  duration = " + duration+"  duration.getSeconds() = " + duration.getSeconds());
//            record.setDuration((int) duration.getSeconds());
//            recordService.updateById(record);
//        }

        List<Help> helps = helpService.list();
        System.out.println("help " + helps);
        for(Help help : helps){
            Duration duration = Duration.between(help.getStartTime(),help.getEndTime());
           // System.out.println("start="+record.getStartTime()+"  duration = " + duration+"  duration.getSeconds() = " + duration.getSeconds());
            help.setDuration((int) duration.getSeconds());
            helpService.updateById(help);
        }
    }

    @Test
    void testTime(){
        LocalDateTime time = LocalDateTime.now();
        System.out.println(time.getYear());
        System.out.println(time.getMonth());
        System.out.println(time.getDayOfMonth());
        System.out.println(time.getDayOfYear());
        System.out.println(time.getDayOfWeek());
    }


    //test for redis

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 向redis数据库中存储哈希类型的数据
     */
    @Test
    void testRedisSet() throws Exception {
        HashOperations ops=stringRedisTemplate.opsForHash();
        ops.put("info","a","aa");
    }

    @Test
    void testRedisGet() throws Exception {
        HashOperations ops=stringRedisTemplate.opsForHash();
        System.out.println(ops.get("info","a"));
    }

    @Test
    void contextLoads() {
        msgService.sendMsg("19933153268");
    }

    @Test
    void getCode(){
        String tempCode = (String) redisUtil.get(MsgServiceImpl.PREFIX_REDIS_KEY + "13391355527");
        System.out.println(tempCode);
    }
}
