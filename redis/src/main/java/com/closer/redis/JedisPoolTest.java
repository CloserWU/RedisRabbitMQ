package com.closer.redis;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <p>JedisPoolTest</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-10 19:38
 */
public class JedisPoolTest {
    @Test
    public void test1() {
        JedisPool pool = JedisPoolUtil.getJedisPool();
        JedisPool poolT = JedisPoolUtil.getJedisPool();
        Jedis jedis = null;
        if (pool != poolT) {
            return;
        }
        jedis = pool.getResource();
        try {
            jedis.set("w", "1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolUtil.release(jedis);
        }
    }
}

