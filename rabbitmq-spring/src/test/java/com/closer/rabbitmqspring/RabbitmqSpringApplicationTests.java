package com.closer.rabbitmqspring;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class RabbitmqSpringApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testAdmin() throws Exception {
        rabbitAdmin.declareExchange(new DirectExchange("spring.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("spring.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("spring.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("spring.fanout.queue", false));
        rabbitAdmin.declareQueue(new Queue("spring.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("spring.direct.queue", false));

        /*
         * Binding(
         *      String destination,
         *      Binding.DestinationType destinationType,
         *      String exchange,
         *      String routingKey,
         *      Map<String, Object> arguments)
         */
        rabbitAdmin.declareBinding(
                new Binding("spring.direct.queue",
                        Binding.DestinationType.QUEUE,
                        "spring.direct",
                        "direct",
                        new HashMap<>()));
        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("spring.topic.queue", false))  // 直接创建队列
                        .to(new TopicExchange("spring.topic", false, false))  // 直接创建交换机 建立关联关系
                        .with("user.#")); // 指定路由key


        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("spring.fanout.queue", false))  // 直接创建队列
                        .to(new FanoutExchange("spring.fanout", false, false))); // 直接创建交换机 建立关联关系
                // 没有with fanout方式不走路由键

        rabbitAdmin.purgeQueue("spring.topic.queue", false);
    }


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述");
        messageProperties.getHeaders().put("type", "自定义信息类型");
        Message msg = new Message("Hello rabbit".getBytes(), messageProperties);
        /*
         *  void convertAndSend(
         *          String exchange,
         *          String routingKey,
         *          Object message,
         *          MessagePostProcessor messagePostProcessor
         *          ) throws AmqpException;
         */
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.out.println("添加额外注释");
                // 变量message即为上面的msg，这里可进一步对msg进行操作
                message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
                message.getMessageProperties().getHeaders().put("attr", "额外新加的信息描述");
                return message;
            }
        });
    }

    @Test
    public void testSendMessage_v2() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message msg = new Message("send msg v2".getBytes(), messageProperties);
        /*
         *  void convertAndSend(
         *          String exchange,
         *          String routingKey,
         *          Object message,  (Object类型)
         *          MessagePostProcessor messagePostProcessor
         *          ) throws AmqpException;
         */
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "send msg topic 001 v2");
        rabbitTemplate.convertAndSend("topic002", "rabbit.amqp", "send msg topic 002 v2");
        // send(String exchange, String routingKey, Message message) Message类型
        rabbitTemplate.send("topic002", "rabbit.amqp", msg);
    }
}
