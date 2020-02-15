package com.closer.rabbitmqspring;

import com.closer.rabbitmqspring.entity.Order;
import com.closer.rabbitmqspring.entity.Packaged;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

@SpringBootTest
class RabbitmqSpringApplicationTests {
    private Logger logger = LoggerFactory.getLogger(RabbitmqSpringApplicationTests.class);

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
        logger.info("yeah");
    }

    /**
     * 将对象转为json字符串作为信息传递给消费者
     * 消费者用Map接收
     * @throws Exception
     */
    @Test
    public void testSendJsonMessage() throws Exception {

        Order order = new Order();
        order.setId(1);
        order.setName("消息订单");
        order.setContent("描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }



    /**
     * 将对象转为json字符串作为信息传递给消费者
     * 消费者用entity实体类型接收，注意header中的__TypeId__ 为entity类的全类名，不能写错
     * @throws Exception
     */
    @Test
    public void testSendJavaMessage() throws Exception {

        Order order = new Order();
        order.setId(1);
        order.setName("订单消息");
        order.setContent("订单描述信息");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json);

        MessageProperties messageProperties = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.closer.rabbitmqspring.entity.Order");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }


    /**
     * 将对象转为json字符串作为信息传递给消费者
     * 消费者用entity实体类型接收，
     * header中的__TypeId__ 标签可以写为自定义标签，
     * 这个参数在javaTypeMapper中，在adapter中设置，一个自定义标签代表一个实体类
     * @throws Exception
     */
    @Test
    public void testSendMappingMessage() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        Order order = new Order();
        order.setId(1);
        order.setName("订单消息");
        order.setContent("订单描述信息");

        String json1 = mapper.writeValueAsString(order);
        System.err.println("order 4 json: " + json1);

        MessageProperties messageProperties1 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties1.setContentType("application/json");
        messageProperties1.getHeaders().put("__TypeId__", "order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("topic001", "spring.order", message1);

        Packaged pack = new Packaged();
        pack.setId(2);
        pack.setName("包裹消息");
        pack.setDesc("包裹描述信息");

        String json2 = mapper.writeValueAsString(pack);
        System.err.println("pack 4 json: " + json2);

        MessageProperties messageProperties2 = new MessageProperties();
        //这里注意一定要修改contentType为 application/json
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__", "packaged");
        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("topic001", "spring.pack", message2);
    }

    @Test
    public void testSendExtConverterMessage() throws Exception {
//			byte[] body = Files.readAllBytes(Paths.get("D:\\IDEA\\rabbitmq-redis\\rabbitmq-spring\\src\\main\\resources", "pic.jpg"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("image/png");
//			messageProperties.getHeaders().put("extName", "png");
//			Message message = new Message(body, messageProperties);
//			// exchange为空走default exchange， 这时queue对routingKey全匹配
//			rabbitTemplate.send("", "image_queue", message);

        byte[] body = Files.readAllBytes(Paths.get("D:\\IDEA\\rabbitmq-redis\\rabbitmq-spring\\src\\main\\resources", "testpdf.pdf"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");
        Message message = new Message(body, messageProperties);
        rabbitTemplate.send("", "pdf_queue", message);
    }
}
