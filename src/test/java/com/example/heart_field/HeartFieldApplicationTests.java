package com.example.heart_field;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

@SpringBootTest
class HeartFieldApplicationTests {

    @Test
    void contextLoads() {
        long currMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currMillis);
        System.out.println(timestamp);
    }

}
