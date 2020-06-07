package com.closer.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>ProducerDirectExchange</p>
 * <p>
 *     消息发送direct模式
 *     接收消息，并根据路由键（routingKey）转发消息所绑定的队列
 *
 *     auto delete 属性，当最后一个绑定到Exchange上的队列删除后，自动删除该Exchange
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-12 16:40
 */
public class ProducerDirectExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.98.52.193");
        factory.setUsername("rabbit");
        factory.setVirtualHost("/");
        factory.setPort(5672);
        factory.setPassword("123456");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";

        String msg = "test babababababab";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        // 这条信息将发送不过去，因为指定的routingKey错误
        channel.basicPublish(exchangeName, "test_direct_queue", null, "hello".getBytes());
        channel.close();
        connection.close();
    }
}

