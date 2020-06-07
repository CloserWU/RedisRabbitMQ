package com.closer.rabbitmq.limit;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * <p>Producer</p>
 * <p>消息限流机制</p>
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
                System.out.println(new String(body));  //
            }
        });
        String exchangeName = "test_qos_exchange";
        String routingKey = "qos.save";
        // 第三个参数mandatory true，则需要设置return监听器  监听器会接收到路由不后可达的消息，然进行后续处理。
        // 若为false， 那么broker端将自动删除该消息，将没有返回
        for (int i = 0; i < 5; i++) {
            channel.basicPublish(exchangeName, routingKey, true, null, " mess".getBytes());
        }
//        channel.close();
//        connection.close();
    }
}


