package com.orangesoftware.back.service.imply;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.entity.Submit;
import com.orangesoftware.back.entity.SubmitVO;
import com.orangesoftware.back.mapper.SubmitMapper;
import com.orangesoftware.back.service.SubmitService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SubmitServiceImply implements SubmitService {

    @Resource
    private SubmitMapper submitMapper;

    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_ALL = "submit:all";
    private static final String CACHE_USER_PREFIX = "submit:user:";
    private static final String CACHE_TASK_PREFIX = "submit:task:";
    private static final String CACHE_ID_PREFIX = "submit:id:";
    private static final long CACHE_TTL_MINUTES = 30;

    @Override
    public void saveSubmit(Submit submit) {
        int rows = submitMapper.insert(submit);
        if (rows > 0) {
            log.info("【SubmitService】提交记录保存成功，id={}", submit.getId());
            // 清除相关缓存
            evictCache(submit.getUserId(), submit.getTaskId());
        } else {
            log.error("【SubmitService】提交记录保存失败，userId={}, taskId={}", submit.getUserId(), submit.getTaskId());
            throw new RuntimeException("提交记录保存失败");
        }
    }

    @Override
    public Result<List<SubmitVO>> getAllSubmits() {
        try {
            // 查缓存
            String cached = redisTemplate.opsForValue().get(CACHE_ALL);
            if (cached != null && !cached.isEmpty()) {
                log.info("【Submit缓存】getAllSubmits 命中缓存");
                List<SubmitVO> list = objectMapper.readValue(cached, new TypeReference<List<SubmitVO>>() {});
                return Result.success(list);
            }
            // 查数据库
            log.info("【Submit缓存】getAllSubmits 缓存未命中，查询数据库");
            List<SubmitVO> list = submitMapper.findAll();
            // 写缓存
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(CACHE_ALL, json, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("【Submit缓存】getAllSubmits 写入缓存, key={}, size={}", CACHE_ALL, list.size());
            return Result.success(list);
        } catch (Exception e) {
            log.error("【Submit缓存】getAllSubmits 异常", e);
            return Result.error("获取提交列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SubmitVO>> getSubmitsByUserId(Integer userId) {
        String key = CACHE_USER_PREFIX + userId;
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null && !cached.isEmpty()) {
                log.info("【Submit缓存】getSubmitsByUserId 命中缓存, userId={}", userId);
                List<SubmitVO> list = objectMapper.readValue(cached, new TypeReference<List<SubmitVO>>() {});
                return Result.success(list);
            }
            log.info("【Submit缓存】getSubmitsByUserId 缓存未命中，查询数据库, userId={}", userId);
            List<SubmitVO> list = submitMapper.findByUserId(userId);
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("【Submit缓存】getSubmitsByUserId 写入缓存, key={}, size={}", key, list.size());
            return Result.success(list);
        } catch (Exception e) {
            log.error("【Submit缓存】getSubmitsByUserId 异常, userId={}", userId, e);
            return Result.error("获取用户提交记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SubmitVO>> getSubmitsByTaskId(Integer taskId) {
        String key = CACHE_TASK_PREFIX + taskId;
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null && !cached.isEmpty()) {
                log.info("【Submit缓存】getSubmitsByTaskId 命中缓存, taskId={}", taskId);
                List<SubmitVO> list = objectMapper.readValue(cached, new TypeReference<List<SubmitVO>>() {});
                return Result.success(list);
            }
            log.info("【Submit缓存】getSubmitsByTaskId 缓存未命中，查询数据库, taskId={}", taskId);
            List<SubmitVO> list = submitMapper.findByTaskId(taskId);
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("【Submit缓存】getSubmitsByTaskId 写入缓存, key={}, size={}", key, list.size());
            return Result.success(list);
        } catch (Exception e) {
            log.error("【Submit缓存】getSubmitsByTaskId 异常, taskId={}", taskId, e);
            return Result.error("获取任务提交记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SubmitVO> getSubmitById(Integer id) {
        String key = CACHE_ID_PREFIX + id;
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null && !cached.isEmpty()) {
                log.info("【Submit缓存】getSubmitById 命中缓存, id={}", id);
                SubmitVO submitVO = objectMapper.readValue(cached, SubmitVO.class);
                return Result.success(submitVO);
            }
            log.info("【Submit缓存】getSubmitById 缓存未命中，查询数据库, id={}", id);
            SubmitVO submitVO = submitMapper.findById(id);
            if (submitVO != null) {
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(submitVO), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                log.info("【Submit缓存】getSubmitById 写入缓存, key={}", key);
                return Result.success(submitVO);
            } else {
                return Result.error("提交记录不存在");
            }
        } catch (Exception e) {
            log.error("【Submit缓存】getSubmitById 异常, id={}", id, e);
            return Result.error("获取提交详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> updateSubmitStatus(Integer id, Integer status) {
        log.info("【SubmitService】更新提交状态，id={}, status={}", id, status);
        try {
            SubmitVO submitVO = submitMapper.findById(id);
            if (submitVO == null) {
                return Result.error("提交记录不存在");
            }
            int rows = submitMapper.updateStatus(id, status);
            if (rows > 0) {
                log.info("【SubmitService】状态更新成功，id={}, status={}", id, status);
                // 清除相关缓存
                evictCache(submitVO.getUserId(), submitVO.getTaskId());
                redisTemplate.delete(CACHE_ID_PREFIX + id);
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            log.error("【SubmitService】状态更新异常", e);
            return Result.error("状态更新失败: " + e.getMessage());
        }
    }

    /**
     * 清除全量、按用户、按任务的缓存
     */
    private void evictCache(Integer userId, Integer taskId) {
        log.info("【Submit缓存】清除缓存, userId={}, taskId={}", userId, taskId);
        redisTemplate.delete(CACHE_ALL);
        redisTemplate.delete(CACHE_USER_PREFIX + userId);
        redisTemplate.delete(CACHE_TASK_PREFIX + taskId);
    }
}
