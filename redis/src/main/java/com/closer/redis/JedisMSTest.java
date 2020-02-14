package com.closer.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * <p>JedisMSTest</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-10 19:34
 */
public class JedisMSTest {
    @Test
    public void test1() {
        Jedis jedisM = new Jedis("47.98.52.193", 6379);
        jedisM.auth("123456");
        Jedis jedisS = new Jedis("47.98.52.193", 6380);
        jedisS.auth("123456");


        // 会出现 写后读 冲突  WAR
        jedisS.slaveof("47.98.52.193", 6379);
        jedisM.set("master", "write");

        System.out.println(jedisS.get("master"));

        jedisM.close();
        jedisS.close();
    }
}

