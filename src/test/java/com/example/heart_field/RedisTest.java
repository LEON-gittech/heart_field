package com.example.heart_field;

//import org.junit.jupiter.api.Test;
import com.example.heart_field.utils.RedisUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author albac0020@gmail.com
 * data 2023/5/16 11:33 AM
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testStringSetKey() {
        stringRedisTemplate.opsForValue().set("name", "heart-field");
    }

    @Test
    public void testStringGetKey() {
        String value=stringRedisTemplate.opsForValue().get("name");
        if(value.equals("heart-field")){
            Assert.assertTrue(true);
        }else{
            Assert.assertTrue(false);
        }
    }

    @Resource
    private RedisUtils redisUtils;

    /**
     * 插入缓存数据
     */
    @Test
    public void set(){
        redisUtils.set("redis_key","redis_value");
    }

    @Test
    public void get() {
        String value = redisUtils.get("redis_key");
        System.out.println(value);
        if (value.equals("redis_value")) {
            Assert.assertTrue(true);
        }
    }

    //redis缓存测试
//
//    //有参数
//    @Cacheable(value="thisredis", key="'users_'+#id")
//    public User findUser(Integer id) {
//        User user = new User();
//        user.setUsername("hlhdidi");
//        user.setPassword("123");
//        user.setUid(id.longValue());
//        System.out.println("log4j2坏啦?");
//        logger.info("输入user,用户名:{},密码:{}",user.getUsername(),user.getPassword());
//        return user;
//    }
//
//    @CacheEvict(value="thisredis",   key="'users_'+#id",condition="#id!=1")
//    public void delUser(Integer id) {
//        // 删除user
//        System.out.println("user删除");
//    }
//
//    //无参数
//    @RequestMapping("/get")
//    @Cacheable(value="thisredis")
//    @ResponseBody
//    public List<User> xx(){
//        return userMapper.selectAll();
//    }
//
//    @RequestMapping("/get3")
//    @CacheEvict(value="thisredis")
//    @ResponseBody
//    public String xx3(){
//        return "ok";
//    }


}
