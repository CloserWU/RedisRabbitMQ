package com.closer.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>ConsumerTopicExchange</p>
 * <p>
 *     所有发送到Topic Exchange的消息被转发到所有关心RoutingKey中指定的Topic的Queue上
 *     Exchange将RoutingKey和某Topic进行模糊匹配(可以使用通配符)，此时队列需要绑定一个Topic
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-12 17:06
 */
public class ConsumerTopicExchange {
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
        String exchangeType = "topic";
        String queueName = "test_topic_queue";
        // # 代表1个或多个
        String routingKey = "user.#";
        // * 代表1个
        String routingKey1 = "user.*";

        channel.exchangeDeclare(exchangeName,exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
//        channel.queueUnbind()
        /*
         * 本测试中 当此一次用 rK1，第二次用rk时，还是能全部显示msg
         * 因为一个exchange会绑定多个routingKey
         */
        channel.queueBind(queueName, exchangeName, routingKey);


        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, false);
                System.out.println(new String(body));
            }
        };
        channel.basicConsume(queueName, true, consumer);




    }
}

