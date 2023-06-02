package com.example.heart_field;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SpringBootTest
class HeartFieldApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    void testTimeConvert(){
        String dateStr = "2021-08-19";
        LocalDateTime date2 = LocalDateTime.parse(dateStr+" 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(date2);

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




}
