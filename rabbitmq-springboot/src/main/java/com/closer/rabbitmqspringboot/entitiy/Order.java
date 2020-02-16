package com.closer.rabbitmqspringboot.entitiy;


import lombok.*;

import java.io.Serializable;

/**
 * <p>Order</p>
 * <p>
 *     SimpleMessageConverter only supports String, byte[] and Serializable payloads,
 * </p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-16 10:39
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Order implements Serializable {
    private String id;
    private String name;

}

