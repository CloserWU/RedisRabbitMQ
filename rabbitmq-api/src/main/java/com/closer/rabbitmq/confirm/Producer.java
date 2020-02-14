package com.closer.rabbitmq.confirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Producer</p>
 * <p>消息传递成功确认机制</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-13 17:04
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("rabbit");
        factory.setPassword("123456");
        factory.setVirtualHost("/");
        factory.setHost("47.98.52.193");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 指定消息的确认模式
        AMQP.Confirm.SelectOk select = channel.confirmSelect();

        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.save";
        channel.basicPublish(exchangeName, routingKey, null, "confirm mesage".getBytes());

        // 添加确认监听
        // 只要超过传递到mq的queue上，就会ack。consumer何时接收则不关心。
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("ack");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("no ack");
            }
        });

//        channel.close();
//        connection.close();
    }
}

