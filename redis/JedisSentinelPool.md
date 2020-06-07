## Jedis根据哨兵获得主节点和辅节点信息

1. 应单例模式创建JedisSentinelPool对象

2. 主节点：

   jedisSentinelPool调用getCurrentHostMaster()得到当前主节点信息

3. 父节点：

   注意到Jedis对象有一个方法sentinelSlaves(String masterName)

   分析可得，由Sentinel创建的Jedis对象可以通过此方法获得辅节点信息

   返回值是List\<Map\<String, String>>;

   > 相当于通过命令行
   >
   > ```shell
   > redis-cli -p 26379
   > >>> sentinel master mymaster
   > >>> sentinel slaves mymaster
   > ```
   >
   > 获得到的信息
   >
   > [格式详见](https://javadoc.io/doc/redis.clients/jedis/latest/redis/clients/jedis/Jedis.html)



## 坑位

每次获取辅节点得到的是127.0.0.1的地址，并且程序执行后，主节点就down了。

经排查，是sentinel.conf的配置问题，官方配置全而细，一不小心就出错。[直接创建最纯净的配置](https://redis.io/topics/sentinel)

```SHELL
port 26379
sentinel monitor mymaster <公网IP> 6379 2
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 60000
sentinel parallel-syncs mymaster 1
```

然后把redis.conf中的所有ip都改为**公网ip**。问题解决



## 代码

```java
package com.closer.redis;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

/**
 * <p>JedisSentinelPoolUtil</p>
 * <p>description</p>
 *
 * @author ws
 * @version 1.0.0
 * @date 2020-06-06 15:09
 */
public class JedisSentinelPoolUtil {

    private static volatile JedisSentinelPool jedisSentinelPool = null;
    private static Set<String> sentinels = new HashSet<>(Arrays.asList(
            "<IP>:26379",
            "<IP>:26380",
            "<IP>:26381"
    ));
    private static Logger logger = LoggerFactory.getLogger(JedisSentinelPoolUtil.class);


    private JedisSentinelPoolUtil() {
    }

    /**
     * maxActive: 链接池中最大连接数,默认为8.
     * maxIdle: 链接池中最大空闲的连接数,默认为8.
     * minIdle: 连接池中最少空闲的连接数,默认为0.
     * maxWait: 当连接池资源耗尽时，调用者最大阻塞的时间，超时将跑出异常。单位，毫秒数;默认为-1.表示永不超时.
     *
     * @return
     */
    public static JedisSentinelPool getJedisSentinelPool(String masterName) {
        if (jedisSentinelPool == null) {
            synchronized (JedisSentinelPoolUtil.class) {
                if (jedisSentinelPool == null) {
                    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
                    poolConfig.setMaxIdle(32);
                    poolConfig.setMaxWaitMillis(100 * 1000);
                    poolConfig.setMaxTotal(1000);
                    jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, poolConfig);
                }
            }
        }
        return jedisSentinelPool;
    }

    /**
     * 根据Sentinel的节点信息创建Jedis对象，并获得主节点信息sentinelGetMasterAddrByName(String name)
     * @param masterName
     * @return
     */
    public static HostAndPort getMasterRaw(String masterName) {
        HostAndPort master = null;
        boolean sentinelAvailable = false;
        for (String sentinelString : sentinels) {
            HostAndPort sentinelHostAndPort = HostAndPort.parseString(sentinelString);
            try (Jedis sentinel = new Jedis(sentinelHostAndPort.getHost(), sentinelHostAndPort.getPort())) {
//                sentinel.subscribe(new JedisPubSub() {
//                    @Override
//                    public void onMessage(String channel, String message) {
//                        super.onMessage(channel, message);
//                    }
//                }, "SwitchMaster");
                List<String> masters = sentinel.sentinelGetMasterAddrByName(masterName);
                sentinelAvailable = true;
                if (masters == null || masters.size() != 2) {
                    logger.warn("Can not get master addr, master name: " + masterName + ". Sentinel: " + sentinelHostAndPort
                            + ".");
                    continue;
                }
                master = HostAndPort.parseString(masters.get(0) + ":" + masters.get(1));
                logger.info("Found Redis master at " + master);
                break;
            } catch (JedisException e) {
                logger.warn(sentinelHostAndPort + " can't get master");
            }
        }
        //如果全部哨兵都获取不到主节点信息则抛出异常
        if (master == null) {
            //可以连接到哨兵，但是查询不到主节点信息
            if (sentinelAvailable) {
                // can connect to sentinel, but master name seems to not
                // monitored
                throw new JedisException("Can connect to sentinel, but " + masterName
                        + " seems to be not monitored...");
            } else {
                //连接不到哨兵 有可能哨兵全部挂了
                throw new JedisConnectionException("All sentinels down, cannot determine where is "
                        + masterName + " master is running...");
            }
        }
        logger.info("Redis master running at " + master + ", starting Sentinel listeners...");
        //返回主节点信息
        return master;
    }

    public static HostAndPort getMaster(String masterName) {
        HostAndPort master = null;
        try {
            // jedisSentinelPool调用方法获得主节点
            master = jedisSentinelPool.getCurrentHostMaster();
            Jedis jedisMaster = new Jedis(master.getHost(), master.getPort());
            jedisMaster.auth("123456");
            Set<String> keys = jedisMaster.keys("*");
            System.out.println(keys);
        } catch (JedisException e) {
            e.printStackTrace();
        }
        return master;
    }

    public static HostAndPort getOneSlave(String masterName) {
        HostAndPort slave = null;
        try {
            for (String sentinelString : sentinels) {
                HostAndPort sentinelHostAndPort = HostAndPort.parseString(sentinelString);
                Jedis sentinel = null;
                try {
                   sentinel  = new Jedis(sentinelHostAndPort.getHost(), sentinelHostAndPort.getPort());
                } catch (JedisException e) {
                    e.printStackTrace();
                }
                if (sentinel == null) {
                    continue;
                }
                // https://javadoc.io/doc/redis.clients/jedis/latest/redis/clients/jedis/Jedis.html
                // 从sentinel的jedis对象来获取slaves，而不是根据master的jedis对象来获得
                List<Map<String, String>> slaves = sentinel.sentinelSlaves(masterName);
                Random random = new Random(1L);
                int i = random.nextInt(slaves.size());
                slave = new HostAndPort(slaves.get(i).get("ip"), Integer.parseInt(slaves.get(i).get("port")));
                break;
            }

        } catch (JedisException e) {
            e.printStackTrace();
        }
        return slave;
    }


    public static void release() {
        if (jedisSentinelPool != null) {
            jedisSentinelPool.destroy();
        }
    }
}

```

