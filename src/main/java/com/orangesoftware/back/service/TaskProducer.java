package com.orangesoftware.back.service;

import cn.hutool.json.JSONUtil;
import com.orangesoftware.back.DTO.TaskSubmitDTO;
import com.orangesoftware.back.config.RabbitMQConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息生产者：将任务发送到 RabbitMQ 队列
 */
@Slf4j
@Service
public class TaskProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送任务消息到队列
     *
     * @param dto 任务提交参数
     */
    public void sendTask(TaskSubmitDTO dto) {
        String message = JSONUtil.toJsonStr(dto);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        log.info("【生产者】任务已发送到队列，taskName={}", dto.getTaskId());
    }
}
