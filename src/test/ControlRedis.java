package test;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.*;

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

    //保存当前的userName
    public static void RemeberUserName(String userName)
    {
        InitialJedis();

        // 将当前userName记录下来
        jedis.hset("user","userName",userName);

        CloseJedis();
    }

    //获取当前的userName
    public static String GetUserName()
    {
        InitialJedis();

        String userName = jedis.hget("user","userName");

        CloseJedis();
        return userName;
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
    public static void ClearJedis()
    {
        InitialJedis();
        jedis.flushAll();
        CloseJedis();
    }

    //endregion

    //region 注册与登录

    //买家进行账户和密码的存储 (注册)
    public static void CustomerSignIn(String userName,String password)
    {
        InitialJedis();

        // 所有的买家账户信息都存储在 customer 哈希表中
        jedis.hset("customer",userName,password);
        jedis.hset(userName,"sum", "0");
        jedis.hset(userName,"Money", "0");
        CloseJedis();
    }

    //卖家进行账户和密码的存储 (注册)
    public static void SellerSignIn(String userName,String password)
    {
        InitialJedis();

        // 所有的卖家账户信息都存储在 seller 哈希表中
        jedis.hset("seller",userName,password);

        CloseJedis();
    }

    //判断当前账户是否存在(登录)   type=“seller”  => 卖家  type=“customer”  => 买家
    public static boolean UserExist(String userName,String type)
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

    //判断当前账户密码是否匹配(登录)  type=“seller”  => 卖家  type=“customer”  => 买家
    public static boolean IsRightPassword(String userName,String password,String type)
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

    // 增加一个图书商品  (用户名，书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)  其中 bookNum 不允许相同
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
                CloseJedis();
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
        map.put("userName",userName);

        // 由于 不同的卖家 对应不同的 出售商品列表   需要一个集合 userName 来存储 它的所有商品
        // 每个商品对应 一个哈希表 userName + bookNum 来保存当前商品的所有信息
        jedis.hmset(userName + bookNum,map);
        jedis.sadd(userName,userName + bookNum);
        jedis.sadd(bookType,userName + bookNum);
        jedis.sadd("allgoods",userName + bookNum);

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
        jedis.sadd("allgoods",userName + map.get("bookNum"));

        CloseJedis();
        return true;
    }

    // TODO 一个图书商品 remainNum - 1  (用户名,书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)
    // TODO 其中 同一商家的 bookNum 不允许相同  任意一本书的 ISBN 不允许相同
    public static boolean RemoveOneGood(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                CloseJedis();
                return false;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            CloseJedis();
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

    // TODO 一个图书商品 remainNum + 1 (用户名,书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量)
    public static boolean AddOneGood(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                CloseJedis();
                return false;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            CloseJedis();
            return false;
        }

        //删除  如果remainNum 变为0 那么全部都没了
        Map<String,String> oneItem = GetOneGood(userName,bookNum);
        String remainNum = oneItem.get("remainNum");
        int num = Integer.parseInt(remainNum);
        num = num + 1;
        oneItem.put("remainNum",String.valueOf(num));
        StoreOneGood(userName,oneItem);

        CloseJedis();
        return true;
    }

    // TODO 修改一个图书商品 remainNum = currentNum
    public static boolean ChangeOneGoodNum(String userName,String bookNum,String currentNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                CloseJedis();
                return false;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            CloseJedis();
            return false;
        }

        //删除  如果remainNum 变为0 那么全部都没了
        Map<String,String> oneItem = GetOneGood(userName,bookNum);
        if (Integer.parseInt(currentNum) > 0)
        {
            oneItem.put("remainNum",String.valueOf(currentNum));
            StoreOneGood(userName,oneItem);
        }
        else if (Integer.parseInt(currentNum) == 0)
        {
            DeleteAllGood(userName,bookNum);
        }
        else{
            return false;
        }

        CloseJedis();
        return true;
    }

    // TODO 通过ISBN  返回出售该书商家的 userName
    public static String GetGoodOwnerUserName(String ISBN)
    {
        InitialJedis();

        Set<String> bookSet = jedis.smembers("allgoods");
        Set<Map<String,String>> books = new HashSet<>();
        String UserName = "";

        for (String str : bookSet)
        {
            books.add(jedis.hgetAll(str));
        }

        for (Map<String,String> map : books)
        {
            if (map.get("ISBN").equals(ISBN))
            {
                UserName = map.get("userName");
            }
        }

        CloseJedis();
        return UserName;
    }

    //直接删除当前商品
    public static boolean DeleteAllGood(String userName,String bookNum)
    {
        InitialJedis();

        // 如果当前存在 userName 这个集合 那么就检查一下有没有当前商品
        if(jedis.exists(userName))
        {
            // 不存在当前bookNum   操作失败
            if(!jedis.sismember(userName,userName + bookNum))
            {
                CloseJedis();
                return false;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            CloseJedis();
            return false;
        }
        String bookType = GetBookType(userName,bookNum);

        InitialJedis();
        jedis.srem(bookType,userName + bookNum);
        jedis.srem(userName,userName + bookNum);
        jedis.srem("allgoods",userName + bookNum);
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
                CloseJedis();
                return "";
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            CloseJedis();
            return "";
        }

        String bookType = GetOneGood(userName,bookNum).get("bookType");

        CloseJedis();
        return bookType;
    }

    //展示当前商家的所有出售的商品
    public static Set<Map<String,String>> ShowAllGoodsByUser(String userName)
    {
        InitialJedis();

        Set<String> bookSet = jedis.smembers(userName);
        Set<Map<String,String>> books = new HashSet<>();

        for (String str : bookSet)
        {
            books.add(jedis.hgetAll(str));
        }

        CloseJedis();
        return books;
    }

    //TODO 展示所有出售的商品
    public static Set<Map<String,String>> ShowAllGoods()
    {
        InitialJedis();

        Set<String> bookSet = jedis.smembers("allgoods");
        Set<Map<String,String>> books = new HashSet<>();

        for (String str : bookSet)
        {
            books.add(jedis.hgetAll(str));
        }

        CloseJedis();
        return books;
    }

    // TODO 展示一个图书商品  (用户名,书名，编号，类型，价格，ISBN，摘要，出售种类，剩余数量,本地图片地址)
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
                CloseJedis();
                return null;
            }
        }
        else {
            //如果当前不存在 userName 这个集合   操作失败
            CloseJedis();
            return null;
        }

        Map<String,String> map = jedis.hgetAll(userName + bookNum);

        CloseJedis();
        return map;
    }

    //endregion

    //region 买家管理购物车及购买商品（默认展示的商品都可购买）

    //TODO 根据图书的唯一标识：UserName+bookNum => BookID将一定数量的图书加入购物车(买家用户名，图书标识，购买数量，库存数量)
    public static boolean AddShoppingCart(String Customer,String BookID,String Price,String BuyNum,String remainNum)
    {
        InitialJedis();

        //如果购买数量≤库存,即可加入购物车
        if((Integer.parseInt(BuyNum)<=Integer.parseInt(remainNum))){

            Map <String ,String> map = new HashMap<>();
            map.put("BookID",BookID);
            map.put("Price",Price);
            map.put("BuyNum",BuyNum);

            double price = Double.parseDouble(Price)*Integer.parseInt(BuyNum);

            //将该图书加入用户的购物车
            jedis.rpush(Customer,BookID);
            jedis.hmset(Customer,map);
            jedis.hincrByFloat(Customer,"sum",price);

            CloseJedis();
            return true;
        }
        //否则失败
        else {
            CloseJedis();
            return false;
        }
    }

    //TODO 修改商品数量(买家用户名，图书标识，修改后数量)
    public  static boolean changegoods(String Customer,String BookID,String BuyNum)
    {
        InitialJedis();

        String price = jedis.hget(BookID,"Price");
        Double DPrice = Double.parseDouble(price);
        DPrice = DPrice * Double.parseDouble(BuyNum);
        jedis.hset(BookID,"BuyNum",BuyNum);
        jedis.hincrByFloat(Customer,"sum",DPrice);
        CloseJedis();

        return true;
    }

    //TODO 结算购物车
    public static boolean BuyGoods(String Customer)
    {
        InitialJedis();

        //判断购物车是否为空
        if(jedis.llen(Customer) < 1){
            CloseJedis();
            return false;
        }
        else {
            /*
            //判断余额是否足以支付
            if(Double.parseDouble(jedis.hget(Customer,"Money"))>=Double.parseDouble(jedis.hget(Customer,"sum"))){
                double payment = Double.parseDouble(jedis.hget(Customer,"sum"))-Double.parseDouble(jedis.hget(Customer,"Money"));
                jedis.hincrByFloat(Customer,"Money",payment);
                //减少库存数(还没写)
                CloseJedis();
                return true;
             */

            //成功支付，并将库存减少，清空购物车
            for(int i = 0;i<=jedis.llen(Customer);i++){
                String BookID = jedis.rpop(Customer);
                jedis.hincrByFloat(BookID,"remainNum", -Double.parseDouble(jedis.hget(BookID,"BuyNum")));
            }

            CloseJedis();
            return true;

        }

    }

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
        }

        CloseJedis();
        return books;
    }

    //endregion

    //测试 暂时没有网页所以就用main函数测试函数功能
    public static void main(String[] args) throws IOException {
        ClearJedis();
    }
}