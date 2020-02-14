package com.closer.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * <p>Producer</p>
 * <p>消息传递失败返回机制</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-13 17:29
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("rabbit");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setHost("47.98.52.193");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_consumer_exchange";
        String routingKey = "consumer.save";

        channel.basicPublish(exchangeName, routingKey, true, null, " mess".getBytes());
        channel.basicPublish(exchangeName, routingKey, true, null, "1 mess".getBytes());

    }
}

