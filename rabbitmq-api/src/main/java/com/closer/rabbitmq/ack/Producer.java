package com.closer.rabbitmq.ack;

import com.rabbitmq.client.*;

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

        //消 息传递失败返回机制
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(
                    int replyCode,  // 响应码
                    String replyText,  // 响应文本
                    String exchange,
                    String routingKey,
                    AMQP.BasicProperties basicProperties,
                    byte[] body) throws IOException {

                // 第一条消息没有return，因为找到响应exchange
                // 第二条消息有return
                System.out.println("return");
                System.out.println(replyCode);  // 312
                System.out.println(replyText);  // NO_ROUTE
                System.out.println(routingKey); //
                System.out.println(basicProperties); // ...
                System.out.println(new String(body));
            }
        });

        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.save";
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("nums", i);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2) // deliveryMode 2 持久化消息
                    .contentEncoding("UTF-8")
                    .headers(map)
                    .build();
            channel.basicPublish(exchangeName, routingKey, true, properties, ("msg " + i + " ").getBytes());
        }
//        channel.close();
//        connection.close();
    }
}


