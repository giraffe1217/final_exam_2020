package test;

import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlRedis {
    private static Jedis jedis;

    //region 初始化及测试

    //创建连接
    private static void InitialJedis()
    {
        jedis = new Jedis("127.0.0.1",6379);
    }

    //关闭连接
    private static void CloseJedis()
    {
        if (jedis!= null)
        {
            jedis.close();
            jedis = null;
        }
    }

    //测试 helloworld！
    public static String SayHelloWorld()
    {
        InitialJedis();

        jedis.set("test", "HelloWorld!");
        String value = jedis.get("test");

        jedis.del("test");

        CloseJedis();
        return value;
    }

    //清空数据库中的所有内容
    private static void ClearJedis()
    {
        InitialJedis();
        jedis.flushAll();
        CloseJedis();
    }

    //endregion

    //region 注册与登录

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

    //endregion

    //region 卖家管理商品

    // TODO 增加一个图书商品  (用户名，书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)  其中 bookNum 不允许相同
    public static boolean StoreOneGood(String userName, String bookName,String bookNum,String bookType,String price,
                                       String ISBN,String summary,String sellType,String remainNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // bookNum 重复   操作失败
            if(jedis.sismember(userName,userName + bookNum))
            {
                return false;
            }
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("bookName",bookName);
        map.put("bookNum",bookNum);
        map.put("bookType",bookType);
        map.put("price",price);
        map.put("ISBN",ISBN);
        map.put("summary",summary);
        map.put("sellType",sellType);
        map.put("remainNum",remainNum);

        // 由于 不同的卖家 对应不同的 出售商品列表   需要一个集合 userName 来存储 它的所有商品
        // 每个商品对应 一个哈希表 userName + bookNum 来保存当前商品的所有信息
        jedis.hmset(userName + bookNum,map);
        jedis.sadd(userName,userName + bookNum);
        jedis.sadd(bookType,userName + bookNum);

        CloseJedis();
        return true;
    }

    // TODO 增加一个图书商品   重载
    public static boolean StoreOneGood(String userName, Map<String,String> map)
    {
        InitialJedis();

        // 由于 不同的卖家 对应不同的 出售商品列表   需要一个集合 userName 来存储 它的所有商品
        // 每个商品对应 一个哈希表 userName + bookNum 来保存当前商品的所有信息
        jedis.hmset(userName + map.get("bookNum"), map);
        jedis.sadd(userName,userName + map.get("bookNum"));
        jedis.sadd(map.get("bookType"),userName + map.get("bookNum"));

        CloseJedis();
        return true;
    }

    // TODO 删除一个图书商品  (用户名,书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)   其中 bookNum 不允许相同
    public static boolean RemoveOneGood(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                return false;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            return false;
        }

        //删除  如果remainNum 变为0 那么全部都没了
        Map<String,String> oneItem = GetOneGood(userName,bookNum);
        String remainNum = oneItem.get("remainNum");
        int num = Integer.parseInt(remainNum);
        if (num > 1)
        {

            num = num - 1;
            oneItem.put("remainNum",String.valueOf(num));
            StoreOneGood(userName,oneItem);
        }
        else{
            DeleteAllGood(userName,bookNum);
        }

        CloseJedis();
        return true;
    }

    //TODO 直接删除当前商品
    public static boolean DeleteAllGood(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                return false;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            return false;
        }
        String bookType = GetBookType(userName,bookNum);

        InitialJedis();
        jedis.srem(bookType,userName + bookNum);
        jedis.srem(userName,userName + bookNum);
        jedis.del(userName + bookNum);

        CloseJedis();
        return true;
    }

    //TODO 返回一个图书商品的类别 bookType
    //TODO 书籍类型分为  politics  economics  literature  science  sport  military
    public static String GetBookType(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                return "";
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            return "";
        }

        String bookType = GetOneGood(userName,bookNum).get("bookType");

        CloseJedis();
        return bookType;
    }


    // TODO 展示一个图书商品  (用户名,书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)
    // TODO 返回值是一个 hashmap 需要通过 map.get() 获得相关 key 的 value
    public static Map<String,String> GetOneGood(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                return null;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            return null;
        }

        Map<String,String> map = jedis.hgetAll(userName + bookNum);

        CloseJedis();
        return map;
    }

    //endregion

    //region 买家管理购物车及购买商品！



    //endregion

    //region 商品搜索

    //TODO 根据类别 展示当前类别的商品   返回  Set<Map<String,String>>   就是 哈希表的集合
    public static Set<Map<String,String>> ShowAllGoodsByType(String bookType)
    {
        InitialJedis();

        Set<String> bookSet = jedis.smembers(bookType);
        Set<Map<String,String>> books = new HashSet<>();

        for (String str : bookSet)
        {
            books.add(jedis.hgetAll(str));
            System.out.println(str);
        }

        CloseJedis();
        return books;
    }

    //endregion

    //测试 暂时没有网页所以就用main函数测试函数功能
    public static void main(String[] args)
    {
        ClearJedis();
    }
}
