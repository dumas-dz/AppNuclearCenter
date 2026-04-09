package com.dumas.pedestal.framework.cache;

import org.springframework.data.redis.connection.zset.Tuple;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Administrator
 */
@Component
public class ZSetRedisCache extends BaseRawRedisCache {


    /**
     * 按位置获取参数 （非score）正序
     *
     * @param key
     * @param start
     * @param end
     * @param t
     * @param <T>
     * @return
     */
    public <T> Set<T> zrange(String key, long start, long end, Class<T> t) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> SerializationUtils.deserialize(connection.zSetCommands().zRange(rawKey, start, end), getSerializer(t)));
    }

    /**
     * 按位置获取参数 （非score）倒序
     *
     * @param key
     * @param start
     * @param end
     * @param t
     * @param <T>
     * @return
     */
    public <T> Set<T> zRevRange(String key, long start, long end, Class<T> t) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> SerializationUtils.deserialize(connection.zSetCommands().zRevRange(rawKey, start, end), getSerializer(t)));
    }

    /**
     * 对有序集合中指定成员的分数加上增量 increment
     *
     * @param key
     * @param increment
     * @param value
     * @return
     */
    public Double zIncrby(String key, double increment, Object value) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> connection.zSetCommands().zIncrBy(rawKey, increment, serialize(value)));
    }


    /**
     * 获取集合个数
     *
     * @param key
     * @return
     */
    public Long zcard(String key) {
        byte[] rawKey = rawKey(key);
        return execute(conection ->
                conection.zSetCommands().zCard(rawKey)
        );
    }

    /**
     * score high -> low sort
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> Set<ScoreTuple> zRevRangeWithScore(String key, long start, long end, Class<T> t) {
        byte[] rawKey = rawKey(key);
        Set<Tuple> resultSets = execute(connection -> connection.zSetCommands().zRevRangeByScoreWithScores(rawKey, 0, Double.MAX_VALUE, start, end));
        if (resultSets == null) {
            return null;
        }
        Set<ScoreTuple> set = new LinkedHashSet<>(resultSets.size());
        for (Tuple rawValue : resultSets) {
            set.add(new ScoreTuple(getSerializer(t).deserialize(rawValue.getValue()), rawValue.getScore()));
        }
        return set;
    }

    /**
     * score low -> high sort
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public <T> Set<ScoreTuple> zRangeWithScore(String key, long start, long end, Class<T> t) {
        byte[] rawKey = rawKey(key);
        Set<Tuple> resultSets = execute(connection -> connection.zSetCommands().zRangeByScoreWithScores(rawKey, 0, Double.MAX_VALUE, start, end));
        if (resultSets == null) {
            return null;
        }
        Set<ScoreTuple> set = new LinkedHashSet<>(resultSets.size());
        for (Tuple rawValue : resultSets) {
            set.add(new ScoreTuple(getSerializer(t).deserialize(rawValue.getValue()), rawValue.getScore()));
        }
        return set;
    }

    public Boolean add(String key, Object value, long score) {
        return execute(connection -> connection.zSetCommands().zAdd(rawKey(key), score, serialize(value)));

    }

    public long remove(String key, Object value) {
        Long result = execute(connection -> connection.zSetCommands().zRem(rawKey(key), serialize(value)));
        return result == null ? 0 : result.longValue();
    }

    public <T> Set<T> rangeByScore(String key, double min, double max, Class<T> t) {
        byte[] rawKey = rawKey(key);
        Set<T> resultSets = execute(connection -> SerializationUtils.deserialize(connection.zSetCommands().zRangeByScore(rawKey, min, max), getSerializer(t)));
        return resultSets;
    }

    /**
     * 获取元素的集合中排序（正序）
     * null 说明不存在集合中
     * > 0
     *
     * @param key
     * @param value
     * @return
     */
    public Long rank(String key, Object value) {
        return execute(connection -> connection.zSetCommands().zRank(rawKey(key), serialize(value)));
    }

    /**
     * 获取元素的集合中排序（倒序）
     * null 说明不存在集合中
     * > 0
     *
     * @param key
     * @param value
     * @return
     */
    public Long revRank(String key, Object value) {
        return execute(connection -> connection.zSetCommands().zRevRank(rawKey(key), serialize(value)));
    }


    /**
     * 获取评分
     *
     * @param key
     * @param value 可能为null
     * @return
     */
    public Double score(String key, Object value) {
        return execute(connection -> connection.zSetCommands().zScore(rawKey(key), serialize(value)));
    }

    /**
     * target与source的-10倍并集，并把结果存储到destKey中
     *
     * @Author: hujiangkai
     * @Date: 2020/10/13 11:35
     * @param: destKey :最终存储key
     * @param: target :目标key
     * @param: source :资源key
     * @return: java.lang.Long
     */
    public Long zUnionStore(String destKey, String target, String source) {
        return execute(connection -> connection.zUnionStore(
                rawKey(destKey),
                org.springframework.data.redis.connection.zset.Aggregate.SUM,
                new int[]{1, Integer.MIN_VALUE},
                rawKey(target),
                rawKey(source)
        ));
    }

    /**
     * 多个source的并集
     *
     * @Author: hujiangkai
     * @Date: 2020/10/13 11:35
     * @param: destKey :最终存储key
     * @param: target :目标key
     * @param: source :资源key
     * @return: java.lang.Long
     */
    public Long zUnionStore(String destKey, String... source) {
        return execute(connection -> connection.zUnionStore(rawKey(destKey), rawKey(source)));
    }

    /**
     * target与source的-10倍并集，并把结果存储到destKey中
     *
     * @Author: hujiangkai
     * @Date: 2020/10/13 14:28
     * @param: destKey :
     * @param: target :
     * @param: source :
     * @return: java.lang.Long
     */
    public Long zInterStore(String destKey, String target, String source) {
        return execute(connection -> connection.zInterStore(rawKey(destKey), rawKey(target), rawKey(source)));
    }

    /**
     * 判断KEY是否存在
     *
     * @Author: hujiangkai
     * @Date: 2020/10/14 15:09
     * @param: key :
     * @return: java.lang.Boolean
     */
    public Boolean exists(String key) {
        return execute(connection -> connection.exists(rawKey(key)));
    }

    /**
     * 按位置获取参数 （非score）
     *
     * @param key
     * @param start
     * @param end
     * @param t
     * @param <T>
     * @return
     */
    public <T> Set<T> zRangeByScore(String key, long start, long end, Class<T> t) {
        byte[] rawKey = rawKey(key);
        return execute(connection -> SerializationUtils.deserialize(connection.zRangeByScore(rawKey, start, end), getSerializer(t)));
    }

    /**
     * 取出所有scores满足条件的值
     *
     * @Author: hujiangkai
     * @Date: 2020/10/13 13:38
     * @param: key :
     * @param: start :
     * @param: end :
     * @return: java.lang.Long
     */
    public Long zRemRangeByScore(String key, long start, long end) {
        return execute(connection -> connection.zRemRangeByScore(rawKey(key), start, end));
    }


    /**
     * 清空key中数据，并删除key
     *
     * @Author: hujiangkai
     * @Date: 2020/10/13 11:48
     * @param: key :
     * @return: java.lang.Long
     */
    public Long zRemAll(String key) {
        return execute(connection -> connection.zRemRange(rawKey(key), 0, -1));
    }

    /**
     * 删除指定的KEY，可以多个
     *
     * @Author: hujiangkai
     * @Date: 2020/10/14 15:09
     * @param: key :
     * @return: java.lang.Long
     */
    public Long del(String... key) {
        return execute(connection -> connection.del(rawKey(key)));
    }

    public static class ScoreTuple<T> {
        private T key;
        private Double count;

        public ScoreTuple(T key, Double count) {
            this.key = key;
            this.count = count;
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public Double getCount() {
            return count;
        }

        public void setCount(Double count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "ScoreTuple{" +
                    "key='" + key + '\'' +
                    ", count=" + count +
                    '}';
        }
    }
}
