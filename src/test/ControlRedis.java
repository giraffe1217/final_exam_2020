package test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
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

    //清空数据库中的所有内容
    private static void ClearJedis()
    {
        InitialJedis();
        jedis.flushAll();
        CloseJedis();
    }

    //TODO 买家进行账户和密码的存储 (注册)
    public static void CustomerSignIn(String userName,String password) throws Exception
    {
        InitialJedis();

        // 所有的买家账户信息都存储在 customer 哈希表中
        jedis.hset("customer",userName,password);

        CloseJedis();
    }

    //TODO 卖家进行账户和密码的存储 (注册)
    public static void SellerSignIn(String userName,String password) throws Exception
    {
        InitialJedis();

        // 所有的卖家账户信息都存储在 seller 哈希表中
        jedis.hset("seller",userName,password);

        CloseJedis();
    }

    //TODO 判断当前账户是否存在(登录)   type=“seller”  => 卖家  type=“customer”  => 买家
    public static boolean UserExist(String userName,String type) throws Exception
    {
        InitialJedis();
        boolean flag = false;

        if(type.equals("seller"))
        {
            flag = jedis.hexists("seller",userName);
        }
        else if (type.equals("customer"))
        {
            flag = jedis.hexists("customer",userName);
        }


        CloseJedis();
        return flag;
    }

    //TODO 判断当前账户密码是否匹配(登录)  type=“seller”  => 卖家  type=“customer”  => 买家
    public static boolean IsRightPassword(String userName,String password,String type) throws Exception
    {
        InitialJedis();
        boolean flag = false;

        if(type.equals("seller") && jedis.hexists("seller",userName))
        {
            if (jedis.hget("seller",userName).equals(password))
            {
                flag = true;
            }
        }
        else if (type.equals("customer") && jedis.hexists("customer",userName))
        {
            if (jedis.hget("customer",userName).equals(password))
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
    public static void main(String[] args) throws Exception
    {
        ClearJedis();

    }
}
