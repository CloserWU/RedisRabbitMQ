package com.closer.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOException;

/**
 * <p>JedisTestMulti</p>
 * <p>description</p>
 *
 * @author closer
 * @version 1.0.0
 * @date 2020-02-10 17:45
 */
public class JedisTestMulti {
    @Test
    public void test1() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person(22, "wushuai", "wushuai@163.com", 360.0);
        String jsonPerson = mapper.writeValueAsString(person);
        System.out.println(jsonPerson);

        Jedis jedis = new Jedis("47.98.52.193", 6379);
        jedis.auth("123456");

        Transaction transaction = jedis.multi();
        transaction.set("t1", "v1");
        transaction.mset("code", "200", "response", jsonPerson);
        transaction.exec();
//        transaction.discard();
        jedis.close();
    }

    @Test
    public void test2() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Jedis jedis = new Jedis("47.98.52.193", 6379);
        jedis.auth("123456");

        String response = jedis.get("response");
        Person responsePerson = mapper.readValue(response, Person.class);
        System.out.println(responsePerson);
        jedis.close();
    }


    /**
     * 通俗点讲，watch命令就是标记一个键，如果标记了一个键，
     * 在提交事务前如果该键被别人修改过，那事务就会失败，这种情况通常可以在程序中重新再尝试一次。
     * 首先标记了键balance，然后检查余额是否足够，不足就取消标记，并不做扣减； 足够的话，就启动事务进行更新操作，
     * 如果在此期间键balance被其它人修改， 那在提交事务（执行exec）时就会报错，
     * 程序中通常可以捕获这类错误再重新执行一次，直到成功。
     *
     *
     * WATCH命令可以监控一个或多个键，一旦其中有一个键被修改（或删除），之后的事务就不会执行。
     * 监控一直持续到EXEC命令（事务中的命令是在EXEC之后才执行的，
     * 所以在MULTI命令后可以修改WATCH监控的键值）
     */
    @Test
    public void test3() throws InterruptedException {
        Jedis jedis = new Jedis("47.98.52.193", 6379);
        jedis.auth("123456");
        int balance; // 可用余额
        int debt; // 欠额
        int amtToSubtract = 10; // 实刷额度
        jedis.set("balance", "20");
        jedis.set("debt", "0");

        jedis.watch("balance");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Jedis jedis = new Jedis("47.98.52.193", 6379);
                jedis.auth("123456");
                jedis.set("balance", "110");
                jedis.close();
            }
        }).start();
        Thread.sleep(3000);  // 在7s内，假如balance被修改，若一个事务中有对balance的set，则这个事务将不会被执行



        Transaction transaction = jedis.multi();
        transaction.decrBy("balance", amtToSubtract);
        transaction.incrBy("debt", amtToSubtract);
        transaction.exec();

        balance = Integer.parseInt(jedis.get("balance"));
        debt = Integer.parseInt(jedis.get("debt"));
        System.out.println("*******" + balance);
        System.out.println("*******" + debt);
        jedis.unwatch();
        jedis.close();
    }


}

