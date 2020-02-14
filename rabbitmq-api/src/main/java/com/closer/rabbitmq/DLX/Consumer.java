package com.closer.rabbitmq.DLX;

import com.closer.rabbitmq.consumer.MyConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * <p>Consumer</p>
 * <p>
 *     死信
 *          ttl到期
 *          消息被拒绝但是没有重回队列
 *          队列满，消息无法进入
 *     死信队列设置
 *          Exchange dlx.exchange
 *          Queue dlx.queue
 *          routingKey #
 *          队列加参数
 *              arguments.put("x-dead-letter-exhcange", "dlx,exchange")
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-13 17:28
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("rabbit");
        factory.setHost("47.98.52.193");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 普通叫交换机、队列、路由
        String exchangeName = "test_dlx_exchange";
        String routingKey = "dlx.#";
        String queueName = "test_dlx_queue";

        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "dlx.exchange");
        // arguments 要声明到queue上
        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.queueBind(queueName, exchangeName, routingKey);

        // 死信队列声明
        channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
        channel.queueDeclare("dlx.queue", true, false, false, null);
        channel.queueBind("dlx.queue", "dlx.exchange", "#");



        // queueName autoAck, Consumer
        // 关闭自动签收
        // 重回队列见MyConsumer
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }
}



