package com.closer.rabbitmqspring;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    }

}
