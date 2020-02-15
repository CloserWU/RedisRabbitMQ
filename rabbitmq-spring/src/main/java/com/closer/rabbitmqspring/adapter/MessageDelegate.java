package com.closer.rabbitmqspring.adapter;

import com.closer.rabbitmqspring.entity.Order;
import com.closer.rabbitmqspring.entity.Packaged;

import java.io.File;
import java.util.Map;

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

    public void consumeMessage(Map messageBody) {
        System.err.println("map方法, 消息内容:" + messageBody);
    }


    public void consumeMessage(Order order) {
        System.err.println("order对象, 消息内容, id: " + order.getId() +
                ", name: " + order.getName() +
                ", content: "+ order.getContent());
    }

    public void consumeMessage(Packaged pack) {
        System.err.println("package对象, 消息内容, id: " + pack.getId() +
                ", name: " + pack.getName() +
                ", content: "+ pack.getDesc());
    }

    public void consumeMessage(File file) {
        System.err.println("文件对象 方法, 消息内容:" + file.getName());
    }
}

