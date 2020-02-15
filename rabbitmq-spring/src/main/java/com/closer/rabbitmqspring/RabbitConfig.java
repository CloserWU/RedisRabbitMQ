package com.closer.rabbitmqspring;


import com.closer.rabbitmqspring.adapter.MessageDelegate;
import com.closer.rabbitmqspring.convert.ImageMessageConverter;
import com.closer.rabbitmqspring.convert.PDFMessageConverter;
import com.closer.rabbitmqspring.convert.TextMessageConverter;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>RabbitConfig</p>
 * <p>依赖注入</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-14 19:50
 */
@Configuration
@ComponentScan({"com.closer.*"})
public class RabbitConfig {
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("47.98.52.193");
        connectionFactory.setUsername("rabbit");
        connectionFactory.setPassword("123456");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // autoStartUp 必须设置为true， 否则spring容器不会加载RabbitAdmin类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }



    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true);
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002", true);
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003", true);
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queueImage() {
        return new Queue("image_queue", true);
    }

    @Bean
    public Queue queuePdf() {
        return new Queue("pdf_queue", true);
    }


    /**
     * 消息模板
     * RabbitTemplate
     * 生产者
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }


    /**
     * 消息容器
     * SimpleMessageListenerContainer
     * 消费者
     */
    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003(), queueImage(), queuePdf());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        // 重回队列 一般false4
        container.setDefaultRequeueRejected(false);
        // 自动签收
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + UUID.randomUUID().toString();
            }
        });
        /*
         container.setMessageListener(new ChannelAwareMessageListener() {
        @Override
        public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        System.err.println("----------消费者: " + msg);
        }
        });
         */

         // 1 适配器方式. 默认是有自己的方法名字的：handleMessage
         // 可以自己指定一个方法的名字: consumeMessage
         // 也可以添加一个转换器: 从字节数组转换为String
         //  -> testSendMessage ,testSendMessage_v2
         /*MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");
         adapter.setMessageConverter(new TextMessageConverter());
         container.setMessageListener(adapter);*/

         // 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
         // -> testSendMessage ,testSendMessage_v2
         /*MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setMessageConverter(new TextMessageConverter());
         Map<String, String> queueOrTagToMethodName = new HashMap<>();
         queueOrTagToMethodName.put("queue001", "method1");
         queueOrTagToMethodName.put("queue002", "method2");
         adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
         container.setMessageListener(adapter);*/


        // 1.1 支持json格式的转换器   -> testSendJsonMessage
         /*MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         adapter.setMessageConverter(jackson2JsonMessageConverter);

         container.setMessageListener(adapter);*/


        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换  -> testSendJavaMessage
        /* MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");

         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

         // 消息消费者能将信息包装成entity实体类
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);*/


        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换  -> testSendMappingMessage
         /*MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
         adapter.setDefaultListenerMethod("consumeMessage");
         Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
         DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();

         // __TypeId__ 自定义标签 对应实体类
         Map<String, Class<?>> idClassMapping = new HashMap<>();
         idClassMapping.put("order", com.closer.rabbitmqspring.entity.Order.class);
         idClassMapping.put("packaged", com.closer.rabbitmqspring.entity.Packaged.class);

         javaTypeMapper.setIdClassMapping(idClassMapping);

         jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
         adapter.setMessageConverter(jackson2JsonMessageConverter);
         container.setMessageListener(adapter);*/

        //1.4 ext convert  对应全部Test

        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        //全局的转换器:
        ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConvert = new TextMessageConverter();
        convert.addDelegate("text", textConvert);
        convert.addDelegate("html/text", textConvert);
        convert.addDelegate("xml/text", textConvert);
        convert.addDelegate("text/plain", textConvert);

        Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("order", com.closer.rabbitmqspring.entity.Order.class);
        idClassMapping.put("packaged", com.closer.rabbitmqspring.entity.Packaged.class);
        javaTypeMapper.setIdClassMapping(idClassMapping);
        jsonConvert.setJavaTypeMapper(javaTypeMapper);

        convert.addDelegate("json", jsonConvert);
        convert.addDelegate("application/json", jsonConvert);

        ImageMessageConverter imageConverter = new ImageMessageConverter();
        convert.addDelegate("image/png", imageConverter);
        convert.addDelegate("image", imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        convert.addDelegate("application/pdf", pdfConverter);


        adapter.setMessageConverter(convert);
        container.setMessageListener(adapter);

        return container;

    }



}

