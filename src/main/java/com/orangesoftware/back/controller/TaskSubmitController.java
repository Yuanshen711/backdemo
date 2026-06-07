package com.orangesoftware.back.controller;

import com.orangesoftware.back.DTO.TaskSubmitDTO;
import com.orangesoftware.back.entity.Result;
import com.orangesoftware.back.service.SubmitLimitService;
import com.orangesoftware.back.service.TaskProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 任务提交接口
 * 接收请求后立即返回，任务由 RabbitMQ 异步消费处理
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
public class TaskSubmitController {

    @Resource
    private TaskProducer taskProducer;

    @Resource
    private SubmitLimitService submitLimitService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 提交任务接口
     * 将任务扔进 RabbitMQ 队列，立即返回"提交成功，正在排队"
     *
     * @param dto 任务提交参数
     * @return 提交结果
     */
    @PostMapping("/submit")
    public Result<String> submitTask(@RequestBody TaskSubmitDTO dto) {
        log.info("【提交接口】收到任务提交请求，taskId={}, userId={}", dto.getTaskId(), dto.getUserId());

        // Redisson 分布式锁，防止重复提交
        String lockKey = "lock:submit:" + dto.getUserId() + ":" + dto.getTaskId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                return Result.error("操作太频繁，请稍后重试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.error("获取锁失败，请重试");
        }

        try {
             //Redis+Lua 限制每个账号对同一任务只能提交一次
            if (!submitLimitService.trySubmit(dto.getUserId(), dto.getTaskId())) {
                return Result.error("您已提交过该任务，不能重复提交");
            }

            taskProducer.sendTask(dto);
            return Result.success("任务ID：" + System.currentTimeMillis(), "提交成功，正在排队");
        } catch (Exception e) {
            return Result.error("提交失败：" + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}