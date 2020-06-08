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
            value = @Queue(value = "queue-1", durable = "true",
                    arguments = {@Argument(name = "x-dead-letter-exchange", value = "dlx.exchange")}),
            exchange = @Exchange(value = "exchange-1", durable = "true", type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "springboot.*")
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        // payload为object类型
        System.out.println("============queue1=============");
        System.out.println("onMessage1" + message.getPayload());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);
        System.out.println();
        System.out.println("==========queue1 end===========");
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
     *
     *  注意与exchange相连的routingkey，对于topic下共同匹配的key，注意onMessage中的接收类型，若直接用@Payload接收，对于指定的Order类型，可能会出现错误
     *  例如，发送"hello"，在exchange-1中，路由是spring.hello.abc，则路由到queue-2，但接收时直接用Order接收，报错
     *  建议用Object接收，再做类型检查 (实际证明，不能用Object，只要类型Object，@Payload不起作用，直接将Message类型传入)
     */
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
//                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
//            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
//                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
//                    type = "${spring.rabbitmq.listener.order.exchange.type}",
//                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
//            key = "${spring.rabbitmq.listener.order.key}")
//    )
//    @RabbitHandler
//    public void onMessage(@Payload Order order, Channel channel, @Headers Map<String, Object> headers) throws Exception {
//        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
//        //若order类型为Object，则输出 class org.springframework.amqp.core.Message
//        System.out.println(order.getClass());
//        System.out.println(order);
//        if (order.getClass() == Order.class) {
//            // payload为object类型
//            System.err.println("onMessage2" + order);
//            channel.basicAck(deliveryTag, false);
//            System.out.println();
//        } else {
////            basicNack(deliveryType, multiple, requeue)
//            channel.basicNack(deliveryTag, false, false);
//            System.out.println("wrong object type, reject to accept");
//        }
//
//    }


    /**
     * 此方法能妥善解决以上问题
     * 并且加入了notack回归死信队列机制
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}",
                    arguments = {@Argument(name = "x-dead-letter-exchange", value = "dlx.exchange")}
            ),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationExceptions}"),
            key = "${spring.rabbitmq.listener.order.key}")
    )
    @RabbitHandler
    public void onMessageV2(Message message, Channel channel) throws Exception {
        System.out.println("============queue2=============");
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //若order类型为Object，则输出 class org.springframework.amqp.core.Message
        Object payload = message.getPayload();
        if (payload.getClass() == Order.class) {
            // payload为object类型
            System.err.println("onMessage2" + payload);
            channel.basicAck(deliveryTag, false);
            System.out.println();
        } else {
//            basicNack(deliveryType, multiple, requeue)
            channel.basicNack(deliveryTag, false, false);
            System.out.println("wrong object type, reject to accept");
        }
        System.out.println("==========queue2 end===========");

    }
}

