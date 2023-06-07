package com.example.heart_field;

import com.alibaba.fastjson.JSON;
import com.example.heart_field.dto.WxUserInfo;
import com.example.heart_field.service.MsgService;
import com.example.heart_field.service.impl.MsgServiceImpl;
import com.example.heart_field.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class HeartFieldApplicationTests {
    @Autowired
    private MsgService msgService;
    @Resource
    private RedisUtil redisUtil;

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
