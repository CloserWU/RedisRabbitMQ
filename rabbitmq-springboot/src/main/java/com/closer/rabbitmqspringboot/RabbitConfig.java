package com.closer.rabbitmqspringboot;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>RabbitConfig</p>
 * <p>
 *     消费端
 *
 *     * 实际情况中，生产端要与消费隔离开来，即分为两个工程或服务
 *     * producer中的RabbitSender既可作为生产端
 *     * 此配置既可作为消费端
 *     * 具体见 rabbitmq/rabbitmq-springboot-producer
 *              rabbitmq/rabbitmq-springboot-consumer
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-15 20:20
 */
@Configuration
@ComponentScan({"com.closer.*"})
public class RabbitConfig {

}

