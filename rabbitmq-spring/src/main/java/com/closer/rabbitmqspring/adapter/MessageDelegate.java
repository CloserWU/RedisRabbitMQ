package com.closer.rabbitmqspring.adapter;

/**
 * <p>MessageDelegate</p>
 * <p>
 *     自定义适配器
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-15 16:50
 */
public class MessageDelegate {
    public void handleMessage(String s) {
        System.out.println("默认方法，消息体：" + s);
    }
    public void handleMessage(byte[] messageBody) {
        System.err.println("默认方法, 消息内容:" + new String(messageBody));
    }

    public void consumeMessage(byte[] messageBody) {
        System.err.println("consume字节数组方法, 消息内容:" + new String(messageBody));
    }

    public void consumeMessage(String s) {
        System.out.println("consume方法，消息体：" + s);
    }

    public void method1(String messageBody) {
        System.err.println("method1 收到消息内容:" + messageBody);
    }

    public void method2(String messageBody) {
        System.err.println("method2 收到消息内容:" + messageBody);
    }
}

