package com.closer.rabbitmq.DLX;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
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

        String exchangeName = "test_dlx_exchange";
        String routingKey = "dlx.save";
        for (int i = 0; i < 5; i++) {
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .expiration("10000")  // 10s还没ack的话，就转到死信队列上
                    .build();
            channel.basicPublish(exchangeName, routingKey, true, properties, ("dlx " + i + " ").getBytes());
        }
        channel.close();
        connection.close();
    }
}


