package com.closer.redis;

import org.junit.Test;
import redis.clients.jedis.HostAndPort;

/**
 * <p>JavaSentinelPoolTest</p>
 * <p>description</p>
 *
 * @author wushuai
 * @version 1.0.0
 * @date 2020-06-06 16:12
 */
public class JavaSentinelPoolTest {

    @Test
    public void test() {
        JavaSentinelPoolTest o = new JavaSentinelPoolTest();
        JedisSentinelPoolUtil.getJedisSentinelPool("mymaster");
//        HostAndPort master = JedisSentinelPoolUtil.getMaster("mymaster");
//        System.out.println(master);
        HostAndPort slave = JedisSentinelPoolUtil.getOneSlave("mymaster");
        System.out.println(slave);

    }
}
