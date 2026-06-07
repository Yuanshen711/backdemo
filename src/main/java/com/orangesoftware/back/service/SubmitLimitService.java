package com.orangesoftware.back.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class SubmitLimitService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private DefaultRedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/submit_once.lua")));
        redisScript.setResultType(Long.class);
    }

    /**
     * 尝试提交，返回 true 表示允许提交（首次），false 表示重复提交
     */
    public boolean trySubmit(Integer userId, Integer taskId) {
        String key = "submit:once:" + userId + ":" + taskId;
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key));
        boolean allowed = Long.valueOf(1).equals(result);
        log.info("【提交限制】userId={}, taskId={}, key={}, allowed={}", userId, taskId, key, allowed);
        return allowed;
    }
}
