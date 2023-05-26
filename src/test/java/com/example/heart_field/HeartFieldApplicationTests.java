package com.example.heart_field;

import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
class HeartFieldApplicationTests {
    @Autowired
    private ConsultantMapper consultantMapper;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserUtils userUtils;

    @Test
    void contextLoads() {

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
    void mapperTest(){
        Consultant consultant = consultantMapper.selectById(2);
        log.info(consultant.toString());
    }
    @Test
    void newAdmin(){
        Admin admin = new Admin();
        admin.setPassword("password123");
        admin.setPhone("13391355527");
        admin.setUsername("xiba");
        adminService.save(admin);
        userUtils.saveUser(admin);
    }
}
