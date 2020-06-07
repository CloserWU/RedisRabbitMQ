package com.closer.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>ConsumerDirectExchange</p>
 * <p>
 *     消息发送direct模式
 *     接收消息，并根据路由键（routingKey）转发消息所绑定的队列
 *
 *     auto delete 属性，当最后一个绑定到Exchange上的队列删除后，自动删除该Exchange
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-12 16:35
 */
public class ConsumerDirectExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.98.52.193");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("rabbit");
        factory.setPassword("123456");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";
        String queueName = "test_direct_queue";
        String routingKey = "test.direct";

//        #
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        // 指明了只有在指定的exchange上，并且是指定的routingKey的消息才会被接收；其他情况一概不接收
        channel.queueBind(queueName, exchangeName, routingKey);
//       #

        channel.basicConsume(queueName, false, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String routingKey = envelope.getRoutingKey();
                System.out.println(routingKey);
                String contentType = properties.getContentType();
                System.out.println(contentType);
                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, false);
                System.out.println(new String(body));
            }
        });



    }
}

