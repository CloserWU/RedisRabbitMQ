package com.closer.rabbitmqspringboot.consumer;


import com.closer.rabbitmqspringboot.entitiy.Order;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>RabbitReceiver</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-16 09:49
 */
@Component
public class RabbitReceiver {

    /**
     * 声明式配置
     * 若没有交换机或队列或绑定
     * 在声明式就会自动绑定
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue-1", durable = "true"),
            exchange = @Exchange(value = "exchange-1", durable = "true", type = "topic", ignoreDeclarationExceptions = "true"),
            key = "springboot.*")
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        // payload为object类型
        System.out.println("onMessage1" + message.getPayload());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
        System.out.println();
    }


    /**
     * 声明式配置，配置不写死，写到yml中
     *     order:
     *         queue:
     *           name: queue-2
     *           durable: true
     *         exchange:
     *           name: exchange-1
     *           durable: true
     *           type: topic
     *           ignoreDeclarationExceptions: true
     *         key: springboot.*
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}")
    )
    @RabbitHandler
    public void onMessage(@Payload Order order, Channel channel, @Headers Map<String, Object> headers) throws Exception {
        // payload为object类型
        System.err.println("onMessage2" + order);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
        System.out.println();
    }
}

