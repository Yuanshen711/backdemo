package com.orangesoftware.back.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String QUEUE_NAME = "task.queue";
    // 交换机名称
    public static final String EXCHANGE_NAME = "task.exchange";
    // 路由键
    public static final String ROUTING_KEY = "task.routing.key";

    /**
     * 创建队列（持久化）
     */
    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    /**
     * 创建直连交换机（持久化）
     */
    @Bean
    public DirectExchange taskExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    /**
     * 将队列绑定到交换机
     */
    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange).with(ROUTING_KEY);
    }
}
