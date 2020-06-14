package com.pony.test.redis;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.pony.test.utils.SerializeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Redis {

    private static Logger log = LoggerFactory.getLogger(Redis.class);

    private static Lock lock = new ReentrantLock();

    private static JedisPool pool;

    @Value("${spring.redis.database}")
    private static Integer dbIndex;
    @Value("${spring.redis.host}")
    private static String host;
    @Value("${spring.redis.port}")
    private static Integer port;
    @Value("${spring.redis.password}")
    private static String password;
    @Value("${spring.redis.timeout}")
    private static Integer timeout;
    @Value("${spring.redis.maxActive}")
    private static Integer maxActive;
    @Value("${spring.redis.maxIdle}")
    private static Integer maxIdle;
    @Value("${spring.redis.maxWait}")
    private static Integer maxWait;
    @Value("${spring.redis.testOnBorrow}")
    private static Boolean testOnBorrow;

    private Redis() {
    }

    // 注意，这里没有final
    private static Redis redis = new Redis();

    public static Redis getInstance() {
        return redis;
    }

    /**
     * 初始化连接池
     */
    private static void initialPool() {
        init();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWait);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(true);// 当调用return Object方法时，是否进行有效性检查
        // 获取连接池
        pool = new JedisPool(config, host, port, timeout, password, dbIndex);
    }

    private static void init() {
        ResourceBundle rb = ResourceBundle.getBundle("application");
        dbIndex = Integer.valueOf(rb.getString("spring.redis.database").trim());
        host = rb.getString("spring.redis.host").trim();
        port = Integer.valueOf(rb.getString("spring.redis.port").trim());
        password = rb.getString("spring.redis.password").trim();
        timeout = Integer.valueOf(rb.getString("spring.redis.timeout").trim());
        maxActive = Integer.valueOf(rb.getString("spring.redis.maxActive").trim());
        maxIdle = Integer.valueOf(rb.getString("spring.redis.maxIdle").trim());
        maxWait = Integer.valueOf(rb.getString("spring.redis.maxWait").trim());
        testOnBorrow = Boolean.valueOf(rb.getString("spring.redis.testOnBorrow").trim());
        log.info("============redis--host" + host);
        log.info("============redis--port" + port);

    }

    /**
     * 获取jedis连接句柄
     *
     * @return Jedis对象
     */
    public static Jedis getJedis() {
        if (pool == null) {
            try {
                lock.lock();
                if (pool == null) {
                    initialPool();
                    log.info("Jedis pool init");
                }
            } catch (Exception e) {
                log.error("Jedis pool init exception", e);
            } finally {
                lock.unlock();
            }
        }
        try {
            if (pool != null) {
                // log.error("~~~~~Redis getJedis()");
                Jedis jedis = pool.getResource();
                return jedis;
            } else {
                log.error("Jedis is null");
            }
        } catch (JedisConnectionException e) {
            log.error("Redis connection exception", e);
            pool.destroy();
            pool = null;
            return getJedis();
        } catch (Exception e) {
            log.error("Failed to get the jedis object", e);
            log.error("获取Jedis对象失败，redis服务没有启动。");
        }
        return null;
    }

    /**
     * 释放资源
     */
    public static void returnResource(Jedis jedis) {
        if (null == jedis || null == pool) {
            return;
        }
        pool.returnResource(jedis);
    }

    /**
     * jedis报错的时候调用,释放错误的数据
     *
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis) {
        if (null == jedis || null == pool) {
            return;
        }
        // log.error("~~~~~Redis returnBrokenResource()");
        pool.returnBrokenResource(jedis);
    }

    /**
     * 返回key对应list的长度，key不存在返回0
     *
     * @param key
     * @return list 长度
     */
    public long llenKey(String key) {
        Jedis jedis = getJedis();
        long value = 0;
        try {
            value = jedis.llen(key);// 返回key对应list的长度
        } catch (Exception e) {
            log.error("Redis llenKey() error:Failed to get the list length", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 随机返回set中的数据
     * xiesanchuan
     *
     * @param key
     * @param count
     * @return 2020年4月10日 下午4:54:50
     */
    public List<String> srandmember(String key, Integer count) {
        List<String> values = new ArrayList<String>();
        Jedis jedis = getJedis();
        try {
            values = jedis.srandmember(key, count);// 返回key对应list的长度
        } catch (Exception e) {
            log.error("Redis srandmember() error:Failed to get the list length", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return values;
    }


    /**
     * 返回key对应list
     *
     * @param key
     * @return length 指定长度
     */
    public List<String> lrange(String key, long length) {
        Jedis jedis = getJedis();
        List<String> value = new ArrayList<>();
        try {
            value = jedis.lrange(key, 0, length);// 返回key对应list的长度
        } catch (Exception e) {
            log.error("Redis llenKey() error:Failed to get the list length", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 返回key对应set的长度，key不存在返回0
     *
     * @param key
     * @return list 长度
     */
    public long scardKey(String key) {
        Jedis jedis = getJedis();
        long value = 0;
        try {
            value = jedis.scard(key);
        } catch (Exception e) {
            log.error("Redis llenKey() error:Failed to get the list length", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 添加成功返回1，否则返回0
     * xiesanchuan
     *
     * @param key
     * @param element
     * @return 2020年3月18日 上午11:44:03
     */
    public Long pfadd(String key, String element) {
        Jedis jedis = getJedis();
        Long incr = 0L;
        try {
            incr = jedis.pfadd(key, element);
        } catch (Exception e) {
            log.error("Redis pfadd error:Delete the key failure", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return incr;
    }


    public Long pfcount(String key) {
        Jedis jedis = getJedis();
        Long incr = 0L;
        try {
            incr = jedis.pfcount(key);
        } catch (Exception e) {
            log.error("Redis pfcount error:Delete the key failure", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return incr;
    }

    // -----------------------------------------Key(键)-------------------------------------------------------
    public Long del(String... keys) {
        Jedis jedis = getJedis();
        Long number = 0L;
        try {
            number = jedis.del(keys);
        } catch (Exception e) {
            log.error("Redis del() error:Delete the key failure", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return number;
    }

    public Long delByte(String keys) {
        Jedis jedis = getJedis();
        Long number = 0L;
        try {
            number = jedis.del(SerializeUtil.serialize(keys));
        } catch (Exception e) {
            log.error("Redis delByte() error:Delete the key failure", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return number;
    }

    /**
     * 查找所有符合给定模式 pattern 的 key 。 * 匹配数据库中所有 key 。 h?llo 匹配 hello ， hallo 和 hxllo 等。
     * h*llo 匹配hllo 和 heeeeello 等。 h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     *
     * @return 返回set集合
     */
    public Set<String> keys(String key) {
        Jedis jedis = getJedis();
        Set<String> setKeys = null;
        try {
            setKeys = jedis.keys(key);
        } catch (Exception e) {
            log.error("Redis keys() error:Failed to get all of the key", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return setKeys;
    }

    /**
     * 检查给定 key 是否存在。
     *
     * @return 返回0(不存在)或者1(存在)
     */
    public boolean exists(String key) {
        Jedis jedis = getJedis();
        boolean exists = false;
        try {
            exists = jedis.exists(key);
        } catch (Exception e) {
            log.error("Redis exists() error:Determine the 'key' whether there is an error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return exists;
    }

    /**
     * 检查给定 key 是否存在。
     *
     * @return 返回0(不存在)或者1(存在)
     */
    public boolean existsByte(String key) {
        Jedis jedis = getJedis();
        boolean exists = false;
        try {
            exists = jedis.exists(SerializeUtil.serialize(key));
        } catch (Exception e) {
            log.error("Redis exists() error:Determine the 'key' whether there is an error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return exists;
    }

    /**
     * 检查给定 key 是否存在。
     *
     * @return 返回0(不存在)或者1(存在)
     */
    public boolean hexists(String key, String field) {
        Jedis jedis = getJedis();
        boolean exists = false;
        try {
            exists = jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("Redis exists() error:Determine the 'key' whether there is an error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return exists;
    }

    // -----------------------------------------String(字符串)------------------------------------------------

    /**
     * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     */
    public boolean set(String key, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            log.error("redis set() error:Save the String data failed", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * 设置值包含过期时间
     *
     * @param key
     * @param value
     * @param time
     * @return
     * @author x1c 2017年11月7日 下午12:56:37
     */
    public boolean set(String key, String value, int time) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            jedis.expire(key, time);
        } catch (Exception e) {
            log.error("redis set() error:Save the String data failed", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET
     * 只能用于处理字符串值。
     */
    public String get(String key) {
        Jedis jedis = getJedis();
        String value = null;
        try {
            value = jedis.get(key);
        } catch (Exception e) {
            log.error("Redis get() error:Failed to get the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;

    }

    /**
     * Redis Mget 命令返回所有(一个或多个)给定 key 的值。 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。
     * 只能用于处理字符串值。
     */
    public List<String> mGet(String... keys) {
        Jedis jedis = getJedis();
        List<String> values = new ArrayList<>();
        try {
            values = jedis.mget(keys);
        } catch (Exception e) {
            log.error("Redis mget() error:Failed to get the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return values;

    }

    /**
     * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET
     * 只能用于处理字符串值。
     */
    public byte[] getByte(String key) {
        Jedis jedis = getJedis();
        byte[] value = null;
        try {
            value = jedis.get(SerializeUtil.serialize(key));
        } catch (Exception e) {
            log.error("Redis get() error:Failed to get the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;

    }

    /**
     * 设置Byte对象，外部直接传入json对象，不需要传Key-darcy20130125 直接以json中key为redis的key
     */
    public boolean setByte(String key, Object object) {
        Jedis jedis = getJedis();
        try {
            try {
                jedis.set(SerializeUtil.serialize(key), SerializeUtil.serialize(object));
            } catch (JSONException e) {
                returnBrokenResource(jedis);
                log.error("Redis JsonObject data format  error", e);
            }
        } catch (Exception e) {
            log.error("Redis setByte(key, object) error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * 设置Json对象，对外封装对json对象的转换-darcy20130124
     */
    public boolean setJson(String key, JSONObject value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value.toString());
        } catch (Exception e) {
            log.error("Redis setJson(key,value) error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }


    // -----------------------------------------List(列表)----------------------------------------------------

    /**
     * 将一个或多个值 value 插入到list列表 key 的表头 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头：
     * 比如对一个空列表 mylist 执行 LPUSH mylist a b c ，则结果列表为 c b a ，等同于执行执行命令 LPUSH mylist a
     * 、 LPUSH mylist b 、 LPUSH mylist c 。
     */
    public boolean lpush(String key, String... strings) {
        Jedis jedis = getJedis();
        try {
            jedis.lpush(key, strings);
        } catch (Exception e) {
            log.error("Redis lpush() error:The list to add data failed", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }


    /**
     * 移除列表的最后一个元素，返回值为移除的元素
     *
     * @param key
     */
    public String rpop(String key) {
        String rpop = "";
        Jedis jedis = getJedis();
        try {
            rpop = jedis.rpop(key);
        } catch (Exception e) {
            log.error("Redis rpop() error:The list to get data failed", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return rpop;
        } finally {
            returnResource(jedis);
        }
        return rpop;
    }



    /**
     * 将一个或多个值 value 插入到List列表 key 的表尾(最右边)。
     *
     * @param key
     * @param strings
     */
    public boolean rpush(String key, String strings) {
        Jedis jedis = getJedis();
        try {
            jedis.rpush(key, strings);
        } catch (Exception e) {
            log.error("Redis rpush() error:The list to add data failed", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * 将一个或多个值 value 插入到List列表 key 的表尾(最左边)。
     *
     * @param key
     * @param strings
     */
    public boolean lpush(String key, String strings) {
        Jedis jedis = getJedis();
        try {
            jedis.lpush(key, strings);
        } catch (Exception e) {
            log.error("Redis lpush() error:The list to add data failed", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * 获取lpush和rpush中所有 的元素
     *
     * @param key
     * @return list<String>
     */
    public List<String> lrange(String key) {
        Jedis jedis = getJedis();
        List<String> retList = null;
        try {
            retList = jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            log.error("Redis lrange() error:Failed to get the list", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return retList;
    }

    /**
     * 读取list中的数据,输入索引值 Darcy 20121107
     *
     * @param key
     * @return list中数据
     */
    public List<String> lrange(String key, long startIndex, long endIndex) {
        Jedis jedis = getJedis();
        List<String> stringList = null;
        try {
            stringList = jedis.lrange(key, startIndex, (int) endIndex);// 获取list中数据
            if (stringList == null || stringList.isEmpty()) {
                stringList = null;
            }
        } catch (Exception e) {
            log.error("Redis lrange(key,start,end) error:Failed to get the list", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return stringList;
    }

    /**
     * 将一个或者多个值放到set中。
     *
     * @param key
     * @param strings
     */
    public Long sadd(String key, String... members) {
        Jedis jedis = getJedis();
        Long incr = 0l;
        try {
            incr = jedis.sadd(key, members);
        } catch (Exception e) {
            log.error("Redis sadd() error:Set the data failure", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return incr;
        } finally {
            returnResource(jedis);
        }
        return incr;
    }

    /**
     * 获取Key对应的Set，返回所有的数据。 无序
     *
     * @param key
     * @param strings
     */
    public Set<String> smembers(String key) {
        Jedis jedis = getJedis();
        Set<String> ret = null;
        try {
            ret = jedis.smembers(key);
        } catch (Exception e) {
            log.error("Redis smembers() error:To obtain the set of data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 删除Set中部分数据。要求Key保存的必须为Set，不能为list，否则将报错。
     */
    public void srem(String key, String... value) {
        Jedis jedis = getJedis();
        try {
            jedis.srem(key, value);
        } catch (Exception e) {
            log.error("Redis srem() error:Delete data in a set", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }


    /**
     * 保存 key field value
     *
     * @param key
     * @param
     */
    public void zset(String key, String field, Integer value) {
        Jedis jedis = getJedis();
        try {
            jedis.zadd(key, value, field);
        } catch (Exception e) {
            log.error("Redis hset() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 保存map
     *
     * @param key
     * @param map
     */
    public void hmset(String key, Map<String, String> hashMap) {
        Jedis jedis = getJedis();
        try {
            jedis.hmset(key, hashMap);
        } catch (Exception e) {
            log.error("Redis hmset() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 保存 key field value
     *
     * @param key
     * @param map
     */
    public void hsetnx(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            log.error("Redis hmset() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }


    /**
     * 保存 key field value
     *
     * @param key
     * @param map
     */
    public void hset(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("Redis hset() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 保存 key field value
     *
     * @param key
     * @param map
     */
    public Long hincrby(String key, String field, Integer value) {
        Jedis jedis = getJedis();
        Long result =null;
        try {
            result = jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            log.error("Redis hmset() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * hash表数据新增
     *
     * @param key
     * @param field
     * @param value
     * @return
     * @edit max.zheng
     */

    public boolean setValueToHash(String key, String field, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("Redis setJson(key,field,value) error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * hash获取数据
     *
     * @param key
     * @param field
     * @return
     * @edit max.zheng
     */
    public String getFromHash(String key, String field) {
        Jedis jedis = getJedis();
        String value = null;
        try {
            String ret = jedis.hget(key, field);
            value = ret;
        } catch (Exception e) {
            log.error("Redis getFromHash(key,field) error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * hash中删除数据
     *
     * @param key
     * @param field
     * @return
     * @edit max.zheng
     */
    public Long delFromHash(String key, String field) {
        Jedis jedis = getJedis();
        Long value = null;
        try {
            Long ret = jedis.hdel(key, field);
            value = ret;
        } catch (Exception e) {
            log.error("Redis getFromHash(key,field) error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 查询map中的数据
     *
     * @param key
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = getJedis();
        Map<String, String> hashMap = new HashMap<String, String>();
        try {
            hashMap = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("Redis hgetAll() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return hashMap;
    }

    /**
     * 根据key 和 fiel 查询
     *
     * @param key field
     */
    public String hget(String key, String field) {
        Jedis jedis = getJedis();
        String retValue = null;
        try {
            retValue = jedis.hget(key, field);
        } catch (Exception e) {
            log.error("Redis hgetAll() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return retValue;
    }

    /**
     * 根据key 和 fiel 查询
     *
     * @param key field
     */
    public String hgetOrDefault(String key, String field, String def) {
        Jedis jedis = getJedis();
        String retValue = null;
        try {
            retValue = jedis.hget(key, field);
        } catch (Exception e) {
            log.error("Redis hgetAll() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return null == retValue ? def : retValue;
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param unixTime
     */
    public void expire(String key, int unixTime) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, unixTime);
        } catch (Exception e) {
            log.error("Redis expire() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param unixTime
     */
    public void expireByte(String key, int unixTime) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(SerializeUtil.serialize(key), unixTime);
        } catch (Exception e) {
            log.error("Redis expire() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取key的过期时间
     *
     * @param key
     * @param unixTime
     * @return
     */
    public Long ttl(String key) {
        Jedis jedis = getJedis();
        Long ttl = 0l;
        try {
            ttl = jedis.ttl(key);
        } catch (Exception e) {
            log.error("Redis ttl() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ttl;
    }

    /**
     * 设置key的过期时间
     *
     * @param key
     * @param unixTime
     */
    public void expireAt(String key, long unixTime) {
        Jedis jedis = getJedis();
        try {
            jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            log.error("Redis expireAt() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    // ------------------------------------------Connection(连接)----------------------------------------------

    /**
     * 输入Redis密码
     *
     * @return 密码匹配时返回 OK ，否则返回一个错误。
     */
    public String auth(String password) {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.auth(password);
        } catch (Exception e) {
            log.error("Redis auth() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 使用客户端向 Redis 服务器发送一个 PING 通常用于测试与服务器的连接是否仍然生效，或者用于测量延迟值。
     *
     * @return 如果连接正常就返回一个 PONG ，否则返回一个连接错误。
     */
    public String ping() {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.ping();
        } catch (Exception e) {
            log.error("Redis ping() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 切换数据库,目前redis中共有16个库.默认使用 0 号数据库。
     *
     * @param index
     * @return OK
     */
    public String select(int index) {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.select(index);
        } catch (Exception e) {
            log.error("Redis select() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 请求服务器关闭与当前客户端的连接。
     *
     * @return 总是OK
     */
    public String quit() {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.quit();
        } catch (Exception e) {
            log.error("Redis quit() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    // ------------------------------------------Server(服务器-------------------------------------------------

    /**
     * 在后台异步(Asynchronously)保存当前数据库的数据到磁盘。
     *
     * @return 反馈信息。
     */
    public String bgsave() {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.bgsave();
        } catch (Exception e) {
            log.error("Redis bgsave() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * SAVE 命令执行一个同步保存操作，将当前 Redis 实例的所有数据快照(snapshot)以 RDB 文件的形式保存到硬盘。
     * <p>
     * 一般来说，在生产环境很少执行 SAVE 操作，因为它会阻塞所有客户端，保存数据库的任务通常由 BGSAVE
     * 命令异步地执行。然而，如果负责保存数据的后台子进程不幸出现问题时， SAVE 可以作为保存数据的最后手段来使用。
     *
     * @return 保存成功时返回 OK 。
     */
    public String save() {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.save();
        } catch (Exception e) {
            log.error("Redis save() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 返回最近一次 Redis 成功将数据保存到磁盘上的时间，以 UNIX 时间戳格式表示。
     *
     * @return 一个 UNIX 时间戳。
     */
    public long lastSave() {
        Jedis jedis = getJedis();
        long value = 0;
        try {
            jedis.lastsave();
        } catch (Exception e) {
            log.error("Redis lastSave() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 清空整个 Redis 服务器的数据(删除所有数据库的所有 key )。
     *
     * @param key
     * @return 总是返回 OK 。
     */
    public boolean flushAll() {
        Jedis jedis = getJedis();
        try {
            jedis.flushAll();
        } catch (Exception e) {
            log.error("Redis flushAll() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    /**
     * 清空当前数据库中的所有 key。
     */
    public boolean flushDB() {
        Jedis jedis = getJedis();
        try {
            jedis.flushDB();
        } catch (Exception e) {
            log.error("Redis flushDB() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;
    }

    public boolean deleteByPrex(String prex) {
        Jedis jedis = getJedis();
        try {
            Set<String> keys = jedis.keys(prex + "*");
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String keyStr = it.next();
                jedis.del(keyStr);
            }
        } catch (Exception e) {
            log.error("Redis flushDB() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return false;
        } finally {
            returnResource(jedis);
        }
        return true;

    }

    /**
     * 获得key中数据的类型
     *
     * @param key
     * @return
     */
    public String getType(String key) {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.type(key);
        } catch (Exception e) {
            log.error("Redis getType error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 移除list中左边第一个元素
     *
     * @return
     */
    public String lpop(String key) {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            ret = jedis.lpop(key);
        } catch (Exception e) {
            log.error("Redis lpop() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return ret;
    }

    /**
     * 用于移除集合中的一个或多个成员元素，不存在的成员元素会被忽略
     *
     * @param key
     * @return
     */
    public void srem(String key, String member) {
        Jedis jedis = getJedis();
        String ret = null;
        try {
            jedis.srem(key, member);
        } catch (Exception e) {
            log.error("Redis lpop() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 根据参数 count 的值，移除列表(list)中与参数 value 相等的元素。
     *
     * @return
     */
    public void lrem(String key, long count, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.lrem(key, count, value);
        } catch (Exception e) {
            log.error("Redis lrem() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Trim an existing list so that it will contain only the specified range of
     * elements specified. Start and end are zero-based indexes. 0 is the first
     * element of the list (the list head), 1 the next element and so on. For
     * example LTRIM foobar 0 2 will modify the list stored at foobar key so that
     * only the first three elements of the list will remain. start and end can also
     * be negative numbers indicating offsets from the end of the list. For example
     * -1 is the last element of the list, -2 the penultimate element and so on.
     * Indexes out of range will not produce an error: if start is over the end of
     * the list, or start > end, an empty list is left as value. If end over the end
     * of the list Redis will threat it just like the last element of the list.
     *
     * @param key
     * @param start
     * @param end
     * @author liangm 2013-07-05
     */
    public void ltrim(String key, long start, long end) {
        Jedis jedis = getJedis();
        try {
            jedis.ltrim(key, start, end);
        } catch (Exception e) {
            log.error("Redis ltrim() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 在事务提交之前，可以指定待监控的Keys，然而在执行EXEC之前， 如果被监控的Keys发生修改，EXEC将放弃执行该事务队列中的所有命令。
     *
     * @param keys
     */
    public void watch(String... keys) {
        Jedis jedis = getJedis();
        try {
            jedis.watch(keys);
        } catch (Exception e) {
            log.error("Redis watch() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 往set里追加set
     */
    public void saddSet(String key, Set<String> strSet) {
        Jedis jedis = getJedis();
        try {
            if (strSet != null) {
                for (String str : strSet) {
                    redis.sadd(key, str);
                }
            }
        } catch (Exception e) {
            log.error("Redis saddSet() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 开启事务
     *
     * @param keys 不传参数则表示不监控key，有参数则会监控
     * @return Transaction
     */
    public Transaction startTrans(String... keys) {
        if (keys.length > 0) {
            redis.watch(keys);
        }
        Transaction trans = Redis.getJedis().multi();
        return trans;
    }

    /**
     * 批量执行命令（使用Pipeline调用操作redis的方法，和jedis用法一样 最后使用pipeline.syncAndReturnAll()执行命令）
     *
     * @return Pipeline对象
     */
    public Pipeline pipelined() {
        Jedis jedis = getJedis();
        Pipeline pipeline = jedis.pipelined();
        // pipeline.set("key", "value");//添加命令
        // List<Object> s = pipeline.syncAndReturnAll();//执行命令
        return pipeline;
    }

    /**
     * 获取事务对象
     *
     * @param keys 不传参数则表示不监控key，有参数则会监控
     * @return Transaction
     */
    public RedisTransaction getTrans(String... keys) {
        if (keys.length > 0) {
            redis.watch(keys);
        }
        Jedis jedis = Redis.getJedis();
        return new RedisTransaction(jedis.multi(), jedis);
    }

    /**
     * redis事物对象
     *
     * @author liangm 2014-09-26
     * @modify by: wenxin
     * @since 2014-9-28 上午10:23:58
     */
    public class RedisTransaction {
        private Transaction trans;
        private Jedis jedis;

        public RedisTransaction(Transaction trans, Jedis jedis) {
            this.trans = trans;
            this.jedis = jedis;
        }

        public Transaction getTransaction() {
            return trans;
        }

        public void ReleaseTransaction() {
            returnResource(jedis);
        }

    }

    /**
     * 获取map中key对应的value
     *
     * @param key
     * @param fields
     * @return
     * @author wenxin
     * @since 2015年4月7日 上午11:31:40
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = getJedis();
        List<String> value = null;
        try {
            value = jedis.hmget(key, fields);
        } catch (Exception e) {
            log.error("Redis hmget() error:Failed to get the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取map的所有key
     *
     * @param key
     * @param fields
     * @return
     * @author wenxin
     * @since 2015年4月7日 上午11:31:40
     */
    public Set<String> hkeys(String key) {
        Jedis jedis = getJedis();
        Set<String> value = null;
        try {
            value = jedis.hkeys(key);
        } catch (Exception e) {
            log.error("Redis hkeys() error:Failed to get the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取map的feild len
     *
     * @param key
     * @param fields
     * @return
     * @author wenxin
     * @since 2015年4月7日 上午11:31:40
     */
    public Long hlen(String key) {
        Jedis jedis = getJedis();
        Long len = null;
        try {
            len = jedis.hlen(key);
        } catch (Exception e) {
            log.error("Redis hlen() error:Failed to get the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return len;
    }

    /**
     * Increment the number stored at key by one.
     * <p>
     *
     * @param key key
     * @return num
     * @since 2015年4月9日 下午5:42:51
     */
    public Long incr(String key) {
        Jedis jedis = getJedis();
        Long value = new Long(0);
        try {
            value = jedis.incr(key);
        } catch (Exception e) {
            log.error("Redis incr() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * key对应数值减一
     *
     * @param key
     * @return
     */
    public Long decr(String key) {
        Jedis jedis = getJedis();
        Long value = new Long(0);
        try {
            value = jedis.decr(key);
        } catch (Exception e) {
            log.error("Redis decr() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 指定成员新增score数
     *
     * @param key
     * @return
     */
    public Double zincrby(String key, String member, Integer score) {
        Jedis jedis = getJedis();
        Double value = 0d;
        try {
            value = jedis.zincrby(key, score, member);
        } catch (Exception e) {
            log.error("Redis zincrby() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 移除成员
     *
     * @param key
     * @return
     */
    public Long zrem(String key, String member) {
        Jedis jedis = getJedis();
        Long value = 0l;
        try {
            value = jedis.zrem(key, member);
        } catch (Exception e) {
            log.error("Redis zincrby() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = getJedis();
        Set<String> zSet = new HashSet<String>();
        try {
            zSet = jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            log.error("Redis zincrby() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return zSet;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = getJedis();
        Double score = 0.00;
        try {
            score = jedis.zscore(key, member);
        } catch (Exception e) {
            log.error("Redis zscore() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return score;
    }

    /**
     * key成员数
     *
     * @param key
     * @return
     */
    public Long zcard(String key) {
        Jedis jedis = getJedis();
        Long value = 0L;
        try {
            value = jedis.zcard(key);
        } catch (Exception e) {
            log.error("Redis zincrby() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 指定区域递增排序返回成员集合
     *
     * @param key
     * @return
     */
    public Set<String> zrange(String key, Long start, Long end) {
        Jedis jedis = getJedis();
        Set<String> set = new HashSet<>();
        try {
            set = jedis.zrange(key, start, end);
        } catch (Exception e) {
            log.error("Redis zincrby() error:Failed to incr the String data", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
        } finally {
            returnResource(jedis);
        }
        return set;
    }


    /**
     * 返回redis list长度
     *
     * @param key
     * @return
     */
    public Long llen(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("Redis llen() error", e);
            if (jedis != null) {
                returnBrokenResource(jedis);
            }
            return null;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取设备对应的cmd key
     * xiesanchuan
     *
     * @param string
     * @return 2020年3月10日 上午10:51:49
     */
    public String getCmdKey(String key) {
        String val = null;
        try {
            val = get(key);
        } catch (Exception e) {
            log.error("获取设备cmd key失败,key:" + key + ",msg:" + e.getMessage(), e);
        }
        if (StringUtils.isBlank(val)) {
            return null;
        }
        JSONObject json = JSONObject.parseObject(val);
        if (null == json || !json.containsKey("redisKey")) {
            return null;
        }
        return json.getString("redisKey");
    }


}
