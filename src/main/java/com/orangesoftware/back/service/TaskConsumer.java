package com.orangesoftware.back.service;

import cn.hutool.json.JSONUtil;
import com.orangesoftware.back.DTO.TaskSubmitDTO;
import com.orangesoftware.back.config.RabbitMQConfig;
import com.orangesoftware.back.entity.Submit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 消息消费者：从 RabbitMQ 队列中异步消费任务
 * 使用 @Async 将消费逻辑提交到线程池执行
 */
@Slf4j
@Service
public class TaskConsumer {

    @Resource
    private SubmitService submitService;

    /**
     * 监听队列消息，异步处理任务
     *
     * @param message 队列中的 JSON 消息字符串
     */
    @Async("taskExecutor")
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeTask(String message) {
        try {
            TaskSubmitDTO dto = JSONUtil.toBean(message, TaskSubmitDTO.class);
            log.info("【消费者】开始处理任务，taskid={}, userId={}", dto.getTaskId(), dto.getUserId());

            processTask(dto);
        } catch (Exception e) {
            log.error("【消费者】任务处理异常，message={}", message, e);
        }
    }

    /**
     * 实际任务处理逻辑（示例：模拟耗时操作）
     * 在此方法中替换为真实的业务处理代码
     */
    private void processTask(TaskSubmitDTO dto) {
        try {
            // 将提交内容存入数据库
            Submit submit = new Submit();
            submit.setUserId(dto.getUserId());
            submit.setTaskId(dto.getTaskId());
            submit.setContent(dto.getContent());
            submit.setStatus(1);
            submitService.saveSubmit(submit);
            Thread.sleep(3000);
        } catch (Exception e) {
            log.error("任务处理异常：{}", e.getMessage());
        }
    }
}
