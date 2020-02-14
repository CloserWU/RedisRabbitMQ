package com.closer.rabbitmq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>ProducerFanoutExchange</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-12 17:30
 */
public class ProducerFanoutExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.98.52.193");
        factory.setUsername("rabbit");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setPassword("123456");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_fanout_exchange";

        String msg = "test fanout";
        for (int i = 0; i < 10; i++) {
            channel.basicPublish(exchangeName, "", null, msg.getBytes());
        }

        channel.close();
        connection.close();
    }
}

