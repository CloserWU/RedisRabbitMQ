package com.closer.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <p>JedisPoolUtil</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-10 19:43
 */
public class JedisPoolUtil {
    private static volatile JedisPool jedisPool = null;

    private JedisPoolUtil() {
    }

    public static JedisPool getJedisPool() {
        if (jedisPool == null) {
            synchronized (JedisPoolUtil.class) {
                if (jedisPool == null) {
                    JedisPoolConfig config = new JedisPoolConfig();
                    config.setMaxIdle(32);
                    config.setMaxWaitMillis(100*1000);
                    config.setTestOnBorrow(true);
                    config.setMaxTotal(1000);
                    jedisPool = new JedisPool(config, "47.98.52.193", 6379);
                }
            }
        }
        return jedisPool;
    }

    public static void release(Jedis jedis) {
       if (jedis != null) {
           jedis.close();
       }
    }
}

