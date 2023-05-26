package com.example.heart_field;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.heart_field.entity.Admin;
import com.example.heart_field.entity.Consultant;
import com.example.heart_field.entity.User;
import com.example.heart_field.mapper.ConsultantMapper;
import com.example.heart_field.service.AdminService;
import com.example.heart_field.utils.TencentCloudImUtil;
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
    @Autowired
    private TencentCloudImUtil tencentCloudImUtil;

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
        User user = userUtils.saveUser(admin);
        //导入账号到腾讯云IM
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Identifier",user.getType().toString()+"_"+user.getUserId().toString());
        jsonObject.put("Nick",admin.getUsername());
        jsonObject.put("FaceUrl",admin.getAvatar());
        String identifier = user.getType().toString()+"_"+user.getUserId().toString();
        boolean isSuccess = tencentCloudImUtil.accountImport(identifier);
        if(!isSuccess){
            adminService.remove(new LambdaQueryWrapper<Admin>().eq(Admin::getId,admin.getId()));
            userUtils.deleteUser(user);
            log.info("腾讯IM导入账号失败");
        }
        log.info("新增咨询师成功");
    }


}
