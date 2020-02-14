package com.closer.rabbitmq.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * <p>MyConsumer</p>
 * <p>自定义消费者</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-13 17:58
 */
public class MyConsumer extends DefaultConsumer {
    private Channel channel;

    public MyConsumer(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    /**
     * amq.ctag-XdaefSElmQ7P9BO0shxGZQ
     * Envelope(deliveryTag=1, redeliver=false, exchange=test_consumer_exchange, routingKey=consumer.save)
     * #contentHeader<basic>(content-type=null, content-encoding=null, headers=null, delivery-mode=null, priority=null, correlation-id=null, reply-to=null, expiration=null, message-id=null, timestamp=null, type=null, user-id=null, app-id=null, cluster-id=null)
     *  mess
     * @param consumerTag
     * @param envelope
     * @param properties
     * @param body
     * @throws IOException
     */
    @Override
    public void handleDelivery(
            String consumerTag,  // 消费标签
            Envelope envelope,   // 关键信息
            AMQP.BasicProperties properties,  // 属性
            byte[] body) throws IOException {
//        System.out.println(consumerTag);
//        System.out.println(envelope);
//        System.out.println(properties);
        System.out.println(new String(body));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ( properties.getHeaders() != null && (Integer) properties.getHeaders().get("nums") == 0) {
            // basicNack(deliveryType, multiple, requeue)
            // 失败消息重回队列
            channel.basicNack(envelope.getDeliveryTag(), false, true);
        } else {
            // basicAck(tag, 是否批量签收);
            channel.basicAck(envelope.getDeliveryTag(), false);
        }
    }
}

