package com.closer.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Producer</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-11 16:59
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        Response req = new Response();
        req.setCode(200);
        req.setBody(new Person(20, "wushuai", "@163.com", 360.0));
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(req);
        System.out.println(body);


        // 1. 创建一个链接工厂，并进行配置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.98.52.193");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("rabbit");
        factory.setPassword("123456");

        // 2. 通过连接工厂创建连接
        Connection connection = factory.newConnection();

        // 3.通过connection创建一个Channel
        Channel channel = connection.createChannel();

        // 4. 通过channel发送数据
        /*
         *  basicPublish(exchange, routingKey, basicProperties, body)
         *  exchange  指定交换机
         *  routingKey  路由线索 发送到routingKey名称对应的队列
         *  basicP 消息信息
         *  body   消息体(发送信息)
         */
        channel.basicPublish("", "test_queue", null, body.getBytes());

        // 5. 关闭连接
        channel.close();
        connection.close();
    }
}

