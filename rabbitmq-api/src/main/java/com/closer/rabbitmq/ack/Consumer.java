package com.closer.rabbitmq.ack;

import com.closer.rabbitmq.consumer.MyConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Consumer</p>
 * <p>ack 与重回队列</p>
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

        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.#";
        String queueName = "test_ack_queue";


        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);


        // queueName autoAck, Consumer
        // 关闭自动签收
        // 重回队列见MyConsumer
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }
}



