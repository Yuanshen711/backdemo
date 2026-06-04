package com.orangesoftware.back.service.imply;

import cn.hutool.core.util.BooleanUtil;
import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.mapper.TaskMapper;
import com.orangesoftware.back.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TaskServiceImply implements TaskService {

    @Resource
    private TaskMapper taskMapper;

    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate  redisTemplate;

    @Override
    public Result<Object> getAllTasks() {
        try {
            return Result.success(taskMapper.findAllTasks());
        } catch (Exception e) {
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Object> getTasksByUserId(Integer userId) {
        try {
            return Result.success(taskMapper.findTasksByUserId(userId));
        } catch (Exception e) {
            return Result.error("获取用户任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Object> getTasksByStatus(Integer status) {
        try {
            return Result.success(taskMapper.findTasksByStatus(status));
        } catch (Exception e) {
            return Result.error("获取状态任务列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> getTaskDetailById(Integer taskId) {

        String cacheKey = "task:description:" + taskId;
        try {
            // 查redis缓存
            String tempDescription = redisTemplate.opsForValue().get(cacheKey);
            // 缓存中存在，直接返回
            if (tempDescription != null && !tempDescription.isEmpty()) {
                return Result.success(tempDescription);
            }

            // 缓存中不存在，使用分布式锁防止缓存击穿(缓存击穿是指当一个热点数据在缓存中过期时，
            // 大量请求同时访问该数据，导致所有请求都打到数据库上，造成数据库压力过大)
            String lockKey = "lock:task:detail:" + taskId;
            if (tryLock(lockKey)) {
                try {
                    // 双重检查，防止其他线程已经更新了缓存
                    tempDescription = redisTemplate.opsForValue().get(cacheKey);
                    if (tempDescription != null && !tempDescription.isEmpty()) {
                        return Result.success(tempDescription);
                    }
                    
                    // 查询数据库
                    String description = taskMapper.findTaskDetailById(taskId);
                    if (description != null) {
                        // 写入缓存，设置较长过期时间
                        redisTemplate.opsForValue().set(cacheKey, description, 3600, TimeUnit.SECONDS);
                        //模拟重建缓存延时
                        Thread.sleep(200);
                        return Result.success(description);
                    } else {
                        // 防止缓存穿透，存储空值但设置较短过期时间
                        redisTemplate.opsForValue().set(cacheKey, "", 60, TimeUnit.SECONDS);
                        return Result.success("");
                    }
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            } else {
                // 获取锁失败，等待一段时间后重试
                Thread.sleep(50);
                return getTaskDetailById(taskId); // 递归重试
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.error("获取任务详情被中断: " + e.getMessage());
        } catch (Exception e) {
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

    private boolean tryLock(String key) {
        Boolean flag= redisTemplate.opsForValue().setIfAbsent(key, "1",10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    private void unlock(String key) {
        redisTemplate.delete(key);
    }

}
