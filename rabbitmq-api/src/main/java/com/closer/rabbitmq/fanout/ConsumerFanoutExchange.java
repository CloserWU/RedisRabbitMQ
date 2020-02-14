package com.closer.rabbitmq.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>ConsumerFanoutExchange</p>
 * <p>
 *     不处理路由键，只需要简单的将队列绑定到交换机上
 *     发送到交换机的消息都会被转发到与该交换机绑定的所有队列上（广播）
 *     fanout交换机的速度是最快的
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-12 17:30
 */
public class ConsumerFanoutExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.98.52.193");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("rabbit");
        factory.setPassword("123456");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_fanout_exchange";
        String exchangeType = "fanout";
        String queueName = "test_fanout_queue";
        String routingKey = "";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        channel.basicConsume(queueName, true, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, false);
                System.out.println(new String(body));
            }
        });



    }
}

