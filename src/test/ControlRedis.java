package test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

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

    //TODO 进行账户和密码的存储 (注册)     main函数测试通过
    public static void SignIn(String userName,String password) throws Exception
    {
        InitialJedis();

        jedis.set(userName,password);

        CloseJedis();
    }

    //TODO 判断当前账户是否存在(登录)     main函数测试通过
    public static boolean UserExist(String userName) throws Exception
    {
        InitialJedis();

        boolean flag = jedis.exists(userName);

        CloseJedis();
        return flag;
    }

    //TODO 判断当前账户密码是否匹配(登录)     main函数测试通过
    public static boolean IsRightPassword(String userName,String password) throws Exception
    {
        InitialJedis();
        boolean flag = false;

        if (jedis.exists(userName))
        {
            if (jedis.get(userName).equals(password))
            {
                flag = true;
            }
        }

        CloseJedis();
        return flag;
    }


    //测试 helloworld！
    public static String SayHelloWorld() throws Exception
    {
        InitialJedis();

        jedis.set("test", "HelloWorld!");
        String value = jedis.get("test");

        jedis.del("test");

        CloseJedis();
        return value;
    }

    //测试 暂时没有网页所以就用main函数测试函数功能
    public static void main(String[] args) throws Exception {

    }
}
