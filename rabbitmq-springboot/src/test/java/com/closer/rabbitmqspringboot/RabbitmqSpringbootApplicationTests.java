package com.closer.rabbitmqspringboot;

import com.closer.rabbitmqspringboot.producer.RabbitSender;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class RabbitmqSpringbootApplicationTests {

    @Test
    void contextLoads() {
    }


    @Autowired
    private RabbitSender rabbitSender;

    // SimpleDateFormat 有线程安全问题
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS");

    @Test
    public void testSender() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("number", "123456");
        properties.put("send_time", format.format(new Date()));
        rabbitSender.send("hello rabbitmq springboot", properties);
    }

}
