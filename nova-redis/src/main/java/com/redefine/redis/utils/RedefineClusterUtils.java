package com.redefine.redis.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author QIANG
 */
public class RedefineClusterUtils {

    private static RedefineClusterUtils cacheUtils;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取所有keys cluster不能用
     *
     * @param key
     * @return
     */
    public static Set<String> keys(String key) {

        return cacheUtils.redisTemplate.keys(key);
    }

    public static String getset(String key, String value) {

        return cacheUtils.redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 将数据存入缓存
     *
     * @param key
     * @param val
     * @return
     */
    public static void saveString(String key, String val) {

        ValueOperations<String, String> vo = cacheUtils.redisTemplate.opsForValue();
        vo.set(key, val);
    }

    public static void saveString(String key, String val, long timeout, TimeUnit unit) {

        ValueOperations<String, String> vo = cacheUtils.redisTemplate.opsForValue();
        vo.set(key, val, timeout, unit);
    }

    public static boolean setNx(String key, String val) {

        ValueOperations<String, String> vo = cacheUtils.redisTemplate.opsForValue();
        return vo.setIfAbsent(key, val);
    }

    /**
     * 设置超时时间
     *
     * @param key
     */
    public static boolean expire(String key, long timeout, TimeUnit unit) {

        return cacheUtils.redisTemplate.expire(key, timeout, unit);

    }

    /**
     * 将数据存入缓存的集合中
     *
     * @param key
     * @param val
     * @return
     */
    public static void saveToSet(String key, String val) {

        SetOperations<String, String> so = cacheUtils.redisTemplate.opsForSet();

        so.add(key, val);
    }

    /**
     * @param key 缓存Key
     * @return keyValue
     * @author:mijp
     * @since:2017/1/16 13:23
     */
    public static String getFromSet(String key) {

        return cacheUtils.redisTemplate.opsForSet().pop(key);
    }

    /**
     * 将 key的值保存为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET if
     * Not eXists』(如果不存在，则 SET)的简写。 <br>
     * 保存成功，返回 true <br>
     * 保存失败，返回 false
     */
    public static boolean saveNX(String key, String val) {

        /** 设置成功，返回 1 设置失败，返回 0 **/
        return cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.setNX(key.getBytes(), val.getBytes());
        });

    }

    /**
     * 将 key的值保存为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET if
     * Not eXists』(如果不存在，则 SET)的简写。 <br>
     * 保存成功，返回 true <br>
     * 保存失败，返回 false
     *
     * @param key
     * @param val
     * @param expire 超时时间
     * @return 保存成功，返回 true 否则返回 false
     */
    public static boolean saveNX(String key, String val, int expire) {

        boolean ret = saveNX(key, val);
        if (ret) {
            cacheUtils.redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return ret;
    }

    /**
     * 将数据存入缓存（并设置失效时间）
     *
     * @param key
     * @param val
     * @param seconds
     * @return
     */
    public static void saveString(String key, String val, int seconds) {

        cacheUtils.redisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    /**
     * 保存复杂类型数据到缓存
     *
     * @param key
     * @param obj
     * @return
     */
    public static void saveBean(String key, Object obj) {

        cacheUtils.redisTemplate.opsForValue().set(key, JSON.toJSONString(obj));
    }

    /**
     * 保存复杂类型数据到缓存（并设置失效时间）
     *
     * @param key
     * @param seconds
     * @return
     */
    public static void saveBean(String key, Object obj, int seconds) {

        cacheUtils.redisTemplate.opsForValue().set(key, JSON.toJSONString(obj), seconds,
                TimeUnit.SECONDS);
    }

    /**
     * 保存到hash集合中
     *
     * @param hName 集合名
     * @param key
     */
    public static void hashSet(String hName, String key, String value) {

        cacheUtils.redisTemplate.opsForHash().put(hName, key, value);
    }

    /**
     * 存储多维数据
     *
     * @param key
     * @param data
     */
    public static void hmset(String key, Map<String, String> data) {

        cacheUtils.redisTemplate.opsForHash().putAll(key, data);
    }

    /**
     * 根据key获取hash所有的域
     *
     * @param key
     * @return
     */
    public static Set<Object> hkeys(String key) {

        return cacheUtils.redisTemplate.opsForHash().keys(key);
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment
     *
     * @param hName
     * @param key
     * @param value
     */
    public static Long hashHincrby(String hName, String key, long value) {

        return cacheUtils.redisTemplate.opsForHash().increment(hName, key, value);
    }

    /**
     * 根据key获取所以值
     *
     * @param key
     * @return
     */
    public static Map<Object, Object> hgetAll(String key) {

        return cacheUtils.redisTemplate.opsForHash().entries(key);
    }


    /**
     * 根据key获取hlen
     *
     * @param key
     * @return
     */
    public static Long hLen(String key) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> connection.hLen(key.getBytes()));

    }


    /**
     * 根据key获取hlen
     *
     * @param key
     * @return
     */
    public static void set(String key, String val, long timeout, TimeUnit timeUnit) {

        cacheUtils.redisTemplate.opsForValue().set(key, val, timeout, timeUnit);

    }

    /**
     * 保存到hash集合中
     *
     * @param <T>
     * @param hName 集合名
     * @param key
     */
    public static <T> void hashSet(String hName, String key, T t) {

        hashSet(hName, key, JSON.toJSONString(t));
    }

    /**
     * 取得复杂JSON数据
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T getBean(String key, Class<T> clazz) {

        String value = cacheUtils.redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }

    /**
     * 从缓存中取得字符串数据
     *
     * @param key
     * @return 数据
     */
    public static String getString(String key) {

        return cacheUtils.redisTemplate.opsForValue().get(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public static boolean exists(String key) {
        return cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.exists(key.getBytes()));
    }

    /**
     * 取得序列值的下一个
     *
     * @param key
     * @return
     */
    public static Long getSeqNext(String key) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> {

            return connection.incr(key.getBytes());

        });
    }

    /**
     * 取得序列值的下一个
     *
     * @param key
     * @return
     */
    public static Long getSeqNext(String key, long value) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> {

            return connection.incrBy(key.getBytes(), value);

        });

    }

    /**
     * 将序列值回退一个
     *
     * @param key
     * @return
     */
    public static void getSeqBack(String key) {

        cacheUtils.redisTemplate
                .execute((RedisCallback<Long>) connection -> connection.decr(key.getBytes()));

    }

    /**
     * 从hash集合里取得
     *
     * @param hName
     * @param key
     * @return
     */
    public static Object hashGet(String hName, String key) {

        return cacheUtils.redisTemplate.opsForHash().get(hName, key);
    }

    public static <T> T hashGet(String hName, String key, Class<T> clazz) {

        return JSON.parseObject((String) hashGet(hName, key), clazz);
    }

    /**
     * 增加浮点数的值
     *
     * @param key
     * @return
     */
    public static Double incrFloat(String key, double incrBy) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Double>) connection -> {

            return connection.incrBy(key.getBytes(), incrBy);

        });
    }

    /**
     * 判断是否缓存了数据
     *
     * @param key 数据KEY
     * @return 判断是否缓存了
     */
    public static boolean isCached(String key) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.exists(key.getBytes());
        });
    }

    /**
     * 判断hash集合中是否缓存了数据
     *
     * @param hName
     * @param key   数据KEY
     * @return 判断是否缓存了
     */
    public static boolean hashCached(String hName, String key) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.hExists(key.getBytes(), key.getBytes());
        });
    }

    /**
     * 判断是否缓存在指定的集合中
     *
     * @param key 数据KEY
     * @param val 数据
     * @return 判断是否缓存了
     */
    public static boolean isMember(String key, String val) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            return connection.sIsMember(key.getBytes(), val.getBytes());
        });
    }

    /**
     * 从缓存中删除数据
     *
     * @return
     */
    public static void delKey(String key) {

        cacheUtils.redisTemplate
                .execute((RedisCallback<Long>) connection -> connection.del(key.getBytes()));
    }

    /**
     * 设置超时时间
     *
     * @param key
     * @param seconds
     */
    public static void expire(String key, int seconds) {

        cacheUtils.redisTemplate.execute(
                (RedisCallback<Boolean>) connection -> connection.expire(key.getBytes(), seconds));

    }

    /**
     * 列出set中所有成员
     *
     * @param setName set名
     * @return
     */
    public static Set<Object> listSet(String setName) {

        return cacheUtils.redisTemplate.opsForHash().keys(setName);

    }

    /**
     * 向set中追加一个值
     *
     * @param setName set名
     * @param value
     */
    public static Long sAdd(String setName, String value) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> connection
                .sAdd(setName.getBytes(), value.getBytes()));

    }

    /**
     * 获取set集合交集
     *
     * @param key
     * @param skey
     * @return
     */
    public static Set<String> intersect(String key, String skey) {

        return cacheUtils.redisTemplate.opsForSet().intersect(key, skey);
    }

    /**
     * 获取set集合交集
     *
     * @param key
     * @param otherKeys
     * @return
     */
    public static Set<String> intersect(String key, List<String> otherKeys) {

        return cacheUtils.redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 获取集合元素数量
     *
     * @return
     */
    public static Long scard(String key) {

        return cacheUtils.redisTemplate.opsForSet().size(key);

    }

    /**
     * 返回集合 key 中的所有成员
     *
     * @param key set名
     */
    public static Set<byte[]> sMembers(String key) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Set<byte[]>>) connection -> connection
                .sMembers(key.getBytes()));

    }

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     */
    public static Long sRem(String key, String member) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> connection
                .sRem(key.getBytes(), member.getBytes()));

    }

    /**
     * 逆序列出sorted set包括分数的set列表
     *
     * @param key   set名
     * @param start 开始位置
     * @param end   结束位置
     * @return 列表
     */
    public static Set<Tuple> zRevRangeWithScores(String key, int start, int end) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Set<Tuple>>) connection -> {
            return connection.zRevRangeWithScores(key.getBytes(), start, end);
        });
    }

    /**
     * 逆序取得sorted sort排名
     *
     * @param key    set名
     * @param member 成员名
     * @return 排名
     */
    public static Long zRevRank(String key, String member) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.zRevRank(key.getBytes(), member.getBytes());
        });

    }

    /**
     * 根据成员名取得sorted sort分数
     *
     * @param key    set名
     * @param member 成员名
     * @return 分数
     */
    public static Double zScore(String key, String member) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Double>) connection -> {
            return connection.zScore(key.getBytes(), member.getBytes());
        });
    }

    /**
     * 向sorted set中追加一个值
     *
     * @param key    set名
     * @param score  分数
     * @param member 成员名称
     */
    public static void zAdd(String key, Double score, String member) {

        cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection
                .zAdd(key.getBytes(), score, member.getBytes()));
    }

    /**
     * 向sorted set中追加一个值
     *
     * @param key   set名
     * @param start 开始角标
     * @param end   结束角标
     */
    public static Long zRemRange(String key, long start, long end) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> connection
                .zRemRange(key.getBytes(), start, end));
    }

    /**
     * 有序集合中对指定成员的分数加上增量 increment
     *
     * @param key
     * @param increment
     * @param value
     * @return
     */
    public static Double zIncrBy(String key, double increment, String value) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Double>) connection -> connection
                .zIncrBy(key.getBytes(), increment, value.getBytes()));
    }

    /**
     * 从sorted set删除一个值
     *
     * @param key    set名
     * @param member 成员名称
     */
    public static void zRem(String key, String member) {

        cacheUtils.redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.zRem(key.getBytes(), member.getBytes()));

    }

    /**
     * 根据key获取有序集合成员数量
     *
     * @param key
     * @return
     */
    public static Set<byte[]> zRangeByScore(String key, String min, String max) {

        return cacheUtils.redisTemplate
                .execute((RedisCallback<Set<byte[]>>) connection -> connection
                        .zRangeByScore(key.getBytes(), min, max));

    }

    /**
     * 根据key获取有序集合成员数量,根据Range 和limit进行限制
     *
     * @param key
     * @return
     */
    public static Set<byte[]> zRangeByScore(String key, String min, String max, int offset,
                                            int count) {

        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gte(min);
        range.lte(max);
        RedisZSetCommands.Limit limit = new RedisZSetCommands.Limit();
        limit.offset(offset);
        limit.count(count);
        return cacheUtils.redisTemplate
                .execute((RedisCallback<Set<byte[]>>) connection -> connection
                        .zRangeByScore(key.getBytes(), range, limit));

    }

    /**
     * 根据key从大到小获取有序集合成员数量,根据Range 和limit进行限制
     *
     * @param key
     * @return
     */
    public static Set<byte[]> zRevRangeByScore(String key, String min, String max, int offset,
                                               int count) {

        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gte(min);
        range.lte(max);
        RedisZSetCommands.Limit limit = new RedisZSetCommands.Limit();
        limit.offset(offset);
        limit.count(count);
        return cacheUtils.redisTemplate
                .execute((RedisCallback<Set<byte[]>>) connection -> connection
                        .zRevRangeByScore(key.getBytes(), range, limit));

    }

    /**
     * 根据key获取有序集合的size
     *
     * @param key
     * @return
     */
    public static Long zCard(String key) {

        return cacheUtils.redisTemplate
                .execute((RedisCallback<Long>) connection -> connection.zCard(key.getBytes()));

    }

    /**
     * 返回有序集key中，score值在min和max之间(默认包括score值等于min或max)的成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public static Long zCount(String key, Double min, Double max) {

        RedisZSetCommands.Range range = new RedisZSetCommands.Range();
        range.gte(min);
        range.lte(max);
        return cacheUtils.redisTemplate
                .execute((RedisCallback<Long>) connection -> connection.zCount(key.getBytes(), range));

    }

    /**
     * 根据key获取有序集合
     *
     * @param key
     * @return
     */
    public static Set<byte[]> zRange(String key, long min, long max) {

        return cacheUtils.redisTemplate.execute(
                (RedisCallback<Set<byte[]>>) connection -> connection.zRange(key.getBytes(), min, max));

    }

    /**
     * 根据key获取有序集合的,从高到底
     *
     * @param key
     * @return
     */
    public static Set<byte[]> zRevRange(String key, long min, long max) {

        return cacheUtils.redisTemplate
                .execute((RedisCallback<Set<byte[]>>) connection -> connection.zRevRange(key.getBytes(),
                        min, max));

    }

    /**
     * 从hash map中取得复杂JSON数据
     *
     * @param key
     * @param field
     * @param clazz
     */
    public static <T> T getBeanFromMap(String key, String field, Class<T> clazz) {

        byte[] input = cacheUtils.redisTemplate.execute((RedisCallback<byte[]>) connection -> {
            return connection.hGet(key.getBytes(), field.getBytes());
        });
        return JSON.parseObject(input, clazz, Feature.AutoCloseSource);
    }

    /**
     * 从hashmap中删除一个值
     *
     * @param key   map名
     * @param field 成员名称
     */
    public static void hDel(String key, String field) {

        cacheUtils.redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.hDel(key.getBytes(), field.getBytes()));

    }

    /**
     * @param key
     * @return
     * @Description: 根据key增长 ，计数器
     * @author clg
     * @date 2016年6月30日 下午2:37:52
     */
    public static long incr(String key) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Long>) connection -> {
            return connection.incr(key.getBytes());
        });
    }

    /**
     * @param key
     * @return
     * @Description: 根据key获取当前计数结果
     * @author clg
     * @date 2016年6月30日 下午2:38:20
     */
    public static String getCount(String key) {

        return cacheUtils.redisTemplate.opsForValue().get(key);
    }

    /**
     * 将所有指定的值插入到存于 key 的列表的头部。如果 key 不存在，那么在进行 push 操作前会创建一个空列表
     *
     * @param <T>
     * @param key
     * @param value
     * @return
     */
    public static <T> Long lpush(String key, T value) {

        return cacheUtils.redisTemplate.opsForList().leftPush(key, JSON.toJSONString(value));
    }

    public static <T> Long rpush(String key, T value) {

        return cacheUtils.redisTemplate.opsForList().rightPush(key, JSON.toJSONString(value));
    }

    public static Long lpush(String key, String value) {

        return cacheUtils.redisTemplate.opsForList().leftPush(key, value);
    }

    public static Long rpush(String key, String value) {

        return cacheUtils.redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 只有当 key 已经存在并且存着一个 list 的时候，在这个 key 下面的 list 的头部插入 value。 与 LPUSH 相反，当 key
     * 不存在的时候不会进行任何操作
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> Long lpushx(String key, T value) {

        return cacheUtils.redisTemplate.opsForList().leftPushIfPresent(key,
                JSON.toJSONString(value));
    }

    /**
     * 返回存储在 key 里的list的长度。 如果 key 不存在，那么就被看作是空list，并且返回长度为 0
     *
     * @param key
     * @return
     */
    public static Long llen(String key) {

        return cacheUtils.redisTemplate.opsForList().size(key);
    }

    /**
     * 返回存储在 key 里的list的长度。 如果 key 不存在，那么就被看作是空list，并且返回长度为 0
     *
     * @param key
     * @return
     */
    public static String lIndex(String key, long index) {

        byte[] data = cacheUtils.redisTemplate.execute(
                (RedisCallback<byte[]>) connection -> connection.lIndex(key.getBytes(), index));
        return new String(data);
    }

    /**
     * 返回存储在 key 的列表里指定范围内的元素。 start 和 end
     * 偏移量都是基于0的下标，即list的第一个元素下标是0（list的表头），第二个元素下标是1，以此类推
     *
     * @param key
     * @return
     */
    public static List<String> lrange(String key, long start, long end) {

        return cacheUtils.redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 移除并且返回 key 对应的 list 的第一个元素
     *
     * @param key
     * @return
     */
    public static String lpop(String key) {

        return cacheUtils.redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 保存到hash集合中 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key
     * 关联。如果字段已存在，该操作无效果。
     *
     * @param hName 集合名
     * @param key
     */
    public static Boolean hsetnx(String hName, String key, String value) {

        return cacheUtils.redisTemplate.execute((RedisCallback<Boolean>) connection -> connection
                .hSetNX(key.getBytes(), key.getBytes(), value.getBytes()));

    }

    /**
     * 保存到hash集合中 只在 key 指定的哈希集中不存在指定的字段时，设置字段的值。如果 key 指定的哈希集不存在，会创建一个新的哈希集并与 key
     * 关联。如果字段已存在，该操作无效果。
     *
     * @param <T>
     * @param hName 集合名
     * @param key
     */
    public static <T> void hsetnx(String hName, String key, T t) {

        hsetnx(hName, key, JSON.toJSONString(t));
    }

    /**
     * 获取匹配的key
     *
     * @param match
     * @param count
     * @return
     */
    public static List<String> scan(String match, int count) {
        List<String> result = new ArrayList<>();
        Cursor<byte[]> retVal = cacheUtils.redisTemplate.execute((RedisCallback<Cursor<byte[]>>) connection -> connection
                .scan(ScanOptions.scanOptions().match(match).count(count).build()));
        while (retVal.hasNext()) {
            result.add(new String(retVal.next()));
        }
        return result;
    }

    /**
     * 获取Set类型, 指定key 中匹配的值
     *
     * @param key
     * @param match
     * @param count
     * @return
     */
    public static List<String> sScan(String key, String match, int count) {
        List<String> result = new ArrayList<>();
        Cursor<byte[]> retVal = cacheUtils.redisTemplate.execute((RedisCallback<Cursor<byte[]>>) connection -> connection
                .sScan(key.getBytes(), ScanOptions.scanOptions().match(match).count(count).build()));
        while (retVal.hasNext()) {
            result.add(new String(retVal.next()));
        }
        return result;
    }

    /**
     * 获取Hash类型, 指定key 中匹配的值
     *
     * @param key
     * @param match
     * @param count
     * @return
     */
    public static Map<String, String> hScan(String key, String match, int count) {
        Map<String, String> result = new HashMap<>(16);
        Cursor<Map.Entry<byte[], byte[]>> retVal = cacheUtils.redisTemplate.execute((RedisCallback<Cursor<Map.Entry<byte[], byte[]>>>) connection -> connection
                .hScan(key.getBytes(), ScanOptions.scanOptions().match(match).count(count).build()));
        while (retVal.hasNext()) {
            Map.Entry<byte[], byte[]> temp = retVal.next();
            result.put(new String(temp.getKey()), new String(temp.getValue()));
        }
        return result;
    }

    /**
     * 获取redis cluster connection
     *
     * @return
     */
    public static RedisClusterConnection getJedisClusterConnection() {
        return cacheUtils.redisTemplate.getConnectionFactory().getClusterConnection();

    }


    public static List<String> mget(Collection<String> hashKeys) {
        return cacheUtils.redisTemplate.opsForValue().multiGet(hashKeys);

    }

    public static List<Object> hmget(String key, Collection<Object> hashKeys) {
        return cacheUtils.redisTemplate.opsForHash().multiGet(key, hashKeys);

    }

    public static void mset(Map<String, String> map) {
        cacheUtils.redisTemplate.opsForValue().multiSet(map);
    }


    @PostConstruct
    public void init() {

        cacheUtils = this;
        cacheUtils.redisTemplate = redisTemplate;
    }

}
