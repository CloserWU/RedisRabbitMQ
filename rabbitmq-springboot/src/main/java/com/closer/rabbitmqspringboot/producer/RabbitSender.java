package com.closer.rabbitmqspringboot.producer;


import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * <p>RabbitSender</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-15 20:22
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ConfirmCallback  confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         * confirm
         * @param correlationData 相关参数
         * @param ack 返回
         * @param cause 异常信息
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println(correlationData);
            System.out.println(ack);
            if (!ack) {
                System.out.println("异常处理，补偿机制");
            } else {
                // 更新数据库
            }
        }
    };


    private final ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {

        /**
         * returnedMessage
         * @param message 信息
         * @param replyCode 错误码
         * @param replyText 重试提示
         * @param exchange 交换机
         * @param routingKey 路由
         */
        @Override
        public void returnedMessage(
                org.springframework.amqp.core.Message message,
                int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("exchange: " + exchange + "。 routingKey: " + routingKey);
            System.out.println("replyCode:" + replyCode + "。 replyText: " + replyText);
        }
    };


    public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders mhs = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, mhs);
//        new CorrelationData(string id)全局唯一id
        CorrelationData data = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        rabbitTemplate.convertAndSend("exchange-1", "springboot.hello", msg, data);
    }

}

