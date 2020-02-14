package com.closer.rabbitmq.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;


/**
 * <p>Producer</p>
 * <p>ack 与重回队列</p>
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

        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.save";
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("nums", i);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(map)
                    .build();
            channel.basicPublish(exchangeName, routingKey, true, properties, ("msg " + i + " ").getBytes());
        }
        channel.close();
        connection.close();
    }
}


