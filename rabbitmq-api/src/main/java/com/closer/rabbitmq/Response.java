package com.closer.rabbitmq;

import lombok.*;

/**
 * <p>Response</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-11 17:45
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Response {
    private Integer code;
    private Object body;
}

