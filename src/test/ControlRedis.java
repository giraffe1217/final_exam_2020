package test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ControlRedis {
    private static Jedis jedis;

    //创建连接
    private static void InitialJedis()
    {
        jedis = new Jedis("127.0.0.1",6379);
    }

    //关闭连接
    private static void CloseJedis()
    {
        jedis.close();
        jedis = null;
    }

    //测试 helloworld
    public static String SayHelloWorld() throws Exception
    {
        InitialJedis();

        jedis.set("test", "HelloWorld!");
        String value = jedis.get("test");

        jedis.del("test");

        CloseJedis();
        return value;
    }
}
