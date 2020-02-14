package com.closer.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>ProducerTopicExchange</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-12 17:11
 */
public class ProducerTopicExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.98.52.193");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("rabbit");
        factory.setPassword("123456");

        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(3000);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String exchangeName = "test_topic_exchange";
        String routingKey1 = "user.select";
        String routingKey2 = "user.update.obj";
        String routingKey3 = "user.delete.id";
        String routingKey4 = "user.insert.obj";


        String msg1 = "select";
        String msg2 = "update";
        String msg3 = "delete";
        String msg4 = "insert";

        channel.basicPublish(exchangeName, routingKey1, null, msg1.getBytes());
        channel.basicPublish(exchangeName, routingKey2, null, msg2.getBytes());
        channel.basicPublish(exchangeName, routingKey3, null, msg3.getBytes());
        channel.basicPublish(exchangeName, routingKey4, null, msg4.getBytes());

        channel.close();
        connection.close();



    }
}

