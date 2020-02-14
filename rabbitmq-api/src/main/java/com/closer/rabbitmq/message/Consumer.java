package com.closer.rabbitmq.message;


import com.closer.rabbitmq.Person;
import com.closer.rabbitmq.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * <p>Consumer</p>
 * <p>
 *     P 和 C 通过properties传递信息
 *     Message如下
 *
 *     virtual host 用于逻辑隔离，最上层的路由
 *     一个virtual host 里可有多个Exchange和Queue
 *     同一个virtual host里面不能有相同的Exchange和Queue
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-11 16:59
 */
public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ObjectMapper mapper = new ObjectMapper();


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

        // 4.声明（创建）队列
        /*
         * channel.queueDeclare(quene, durable, exclusive, autoDelete, arguments);
         * queue 队列
         * durable 是否持续，即mq重启后是否还在
         * exclusive 是否独占队列
         * autoDelete 队列脱离交换机后，自动删除
         * arguments 其他参数
         */
        channel.queueDeclare("test_queue", true, false, false, null);

        // 5. 创建消费者
        /*
         *  channel.basicConsume(queueName, autoAck, Consumer)
         *  队列名
         *  是否自动签收
         */
        channel.basicConsume("test_queue", true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(
                    String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties,
                    byte[] body)
                    throws IOException {
                String routingKey = envelope.getRoutingKey();


                System.out.println(properties.getExpiration());
                System.out.println(properties.getHeaders());
                System.out.println(properties.getContentEncoding());



                long deliveryTag = envelope.getDeliveryTag();
                channel.basicAck(deliveryTag, true);
                Response response = mapper.readValue(new String(body), Response.class);
//                {"code":200,"body":{"age":20,"name":"wushuai","email":"@163.com","score":360.0}}
                System.out.println(new String(body));
                String json = mapper.writeValueAsString(response.getBody());
                Person p = mapper.readValue(json, Person.class);
//                Response{code=200, body={age=20, name=wushuai, email=@163.com, score=360.0}}
                System.out.println(response);
//                Person{age=20, name='wushuai', email='@163.com', score=360.0}
                System.out.println(p);
            }
        });

    }
}

