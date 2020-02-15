package com.closer.rabbitmqspring.entity;


import lombok.*;

/**
 * <p>Package</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-15 17:39
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Package {
    private Integer id;
    private String name;
    private String desc;
}

