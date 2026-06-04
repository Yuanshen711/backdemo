package com.orangesoftware.back;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class BackApplicationTests {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {

    }

}
