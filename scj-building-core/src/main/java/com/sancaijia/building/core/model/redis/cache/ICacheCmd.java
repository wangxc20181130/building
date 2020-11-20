package com.sancaijia.building.core.model.redis.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICacheCmd {
    int NO_EXPIRE = -1;

    boolean put(final String key, final String value);

    boolean put(final String key, final String value, final Integer timeout);

    String put(final String value);

    String put(final String value, final Integer timeout);

    String get(final String key);

    Integer getInt(final String key);

    Long getLong(final String key);

    Double getDouble(final String key);

    void del(final String key);

    void del(final String... keys);

    boolean exists(final String key);

    boolean hSet(ICacheAble entity);

    boolean hSet(List<ICacheAble> list);

    <T> T hGet(Long id, Class<T> clazz);

    <T> List<T> hGet(Class<T> clazz, List<String> ids);

    boolean hmSet(final String key, String field, List<String> data);

    boolean hmSet(final String key, String field, String data);

    String hmGetString(final String key, final String field);

    List<String> hmGet(final String key, final String field);

    void hmDel(final String key, final String field);

    void expire(final String key, int seconds);

    void sadd(String key, String... members);

    void zadd(String key, Double score, String member);

    void zadd(String key, Map<String, Double> members);

    ICacheCmd select(Integer index);

    void srem(String key, String... members);

    void zrem(String key, String... members);

    Set<String> sinter(String... keys);

    Set<String> zrangeByScore(String key, String min, String max);

    List<Map.Entry<String, String>> hscan(String key, String pattern, int count);

    Integer zcount(String key, String min, String max);

    Set<String> zrange(String key, int start, int end);

    List<String> zrangeWithScores(String key, int start, int end);

    Set<String> zrevrange(String key, int start, int end);

    List<String> zrevrangeWithScores(String key, int start, int end);

    List<String> sscan(String key, String pattern, int count);

    void hdel(Long id, Class<? extends ICacheAble> clazz);

    boolean setNx(final String key, final String value, final int seconds);
}
