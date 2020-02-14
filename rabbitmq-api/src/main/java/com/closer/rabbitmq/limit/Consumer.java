package com.closer.rabbitmq.limit;

import com.closer.rabbitmq.consumer.MyConsumer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Consumer</p>
 * <p>消息限流机制</p>
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

        String exchangeName = "test_qos_exchange";
        String routingKey = "qos.#";
        String queueName = "test_qos_queue";


        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        /*
            RabbitMQ提供了一种qos(服务质量保证)功能，即在非自动确认消息的前提下，
            如果提供一定数目的消息(通过基于consume或者channel设置的Qos值)
            未被确认前，不进行新消息的确认
            所以basicConsume(queueName, autoAck, Consumer) autoAck参数一定要设为false

            void BasicQos(unit prefetchSize, ushort prefetchCount, bool global)
            prefetchSize 消息大小限制  0为不限制大小
            prefetchCount 一个消费者一次最多处理多少消息 一般为1，消费者处理ack一个消息增加一个
            global 是否全局应用 一般为false

            consumer中需要调用basicAck
         */
        channel.basicQos(0, 1, false);
        // queueName autoAck, Consumer
        // 关闭自动签收
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }
}



