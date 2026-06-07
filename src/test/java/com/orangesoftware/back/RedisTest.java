package com.orangesoftware.back;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void test() throws Exception {
    String testKey = "test";
    String testValue = "testValue";



    stringRedisTemplate.opsForValue().set(testKey, testValue);
    String result=stringRedisTemplate.opsForValue().get(testKey);

        Assertions.assertEquals(testValue, result);
        System.out.println(result);


    }






}
