package com.sancaijia.building.core.model.redis.cache;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sancaijia.building.core.utils.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Scope("singleton")
public class JedisOwner implements ICacheCmd {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisOwner.class);
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private Integer redisPost;
    private static JedisPool jedisPool;
    private static final String RANDOM_PREFIX = "RANDOM_";
    private static final Integer NEVER_TIMEOUT = -1;
    private static final String NULL_VALUE = "null";
    ThreadLocal<Jedis> jedisThreadLocal = new ThreadLocal();

    private JedisOwner() {
    }
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(500);
        config.setMinIdle(100);
        config.setMaxIdle(100);
        config.setTestOnBorrow(true);
        config.setMaxWaitMillis(3000L);
        config.setBlockWhenExhausted(true);
        config.setJmxEnabled(false);
        jedisPool = new JedisPool(config, this.redisHost, this.redisPost);
        LOGGER.info("Connect to redis server tcp://{}:{}", this.redisHost, this.redisPost);
    }
    public boolean put(final String key, final String value) {
        if (StringUtils.isNotEmpty(key)) {
            String result = this.getResource().set(key, value);
            return StringUtils.isNotEmpty(result);
        } else {
            return false;
        }
    }

    public boolean put(final String key, final String value, final Integer timeout) {
        if (StringUtils.isNotEmpty(key)) {
            String result = this.getResource().setex(key, timeout, value);
            return StringUtils.isNotEmpty(result);
        } else {
            return false;
        }
    }

    public String put(final String value) {
        return this.put(value, NEVER_TIMEOUT);
    }

    public String put(final String value, final Integer timeout) {
        String stringId = "RANDOM_" + IdUtil.nextStringId();
        if (!StringUtils.isEmpty(value) && !this.getResource().exists(stringId)) {
            String result = this.getResource().setex(stringId, timeout, value);
            return StringUtils.isEmpty(result) ? "" : stringId;
        } else {
            return "";
        }
    }

    public String get(final String key) {
        String result = this.getResource().get(key);
        return "null".equals(result) ? "" : result;
    }

    public Integer getInt(String key) {
        String s = this.get(key);
        return StringUtils.isNotEmpty(s) ? Integer.valueOf(s) : null;
    }

    public Long getLong(String key) {
        String s = this.get(key);
        return StringUtils.isNotEmpty(s) ? Long.valueOf(s) : null;
    }

    public Double getDouble(String key) {
        String s = this.get(key);
        return StringUtils.isNotEmpty(s) ? Double.valueOf(s) : null;
    }

    public void del(final String key) {
        if (!StringUtils.isEmpty(key)) {
            this.getResource().del(key);
        }
    }

    public boolean exists(final String key) {
        return StringUtils.isEmpty(key) ? false : this.getResource().exists(key);
    }

    public boolean hSet(ICacheAble entity) {
        if (null == entity) {
            return false;
        } else {
            Class<? extends ICacheAble> clazz = entity.getClass();
            Output output = this.getOutput(clazz, entity);
            byte[] keyBytes = clazz.getName().toUpperCase().getBytes(StandardCharsets.UTF_8);
            String cachedId = String.valueOf(entity.cacheId());
            byte[] idBytes = cachedId.getBytes(StandardCharsets.UTF_8);
            byte[] bytes = output.getBuffer();
            this.getResource().hset(keyBytes, idBytes, bytes);
            output.flush();
            return true;
        }
    }

    public boolean hSet(List<ICacheAble> list) {
        if (null != list && !list.isEmpty() && list.size() <= 5000) {
            Class<? extends ICacheAble> clazz = ((ICacheAble)list.get(0)).getClass();
            byte[] keyBytes = clazz.getName().toUpperCase().getBytes(StandardCharsets.UTF_8);
            Pipeline pipelined = this.getResource().pipelined();
            Kryo kryo = this.getKryo(clazz);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
                ICacheAble entity = (ICacheAble)iterator.next();
                String cachedId = String.valueOf(entity.cacheId());
                kryo.writeObject(output, entity);
                byte[] idBytes = cachedId.getBytes(StandardCharsets.UTF_8);
                byte[] bytes = output.getBuffer();
                pipelined.hset(keyBytes, idBytes, bytes);
                output.flush();
            }

            pipelined.sync();
            pipelined.clear();

            pipelined.close();

            return true;
        } else {
            LOGGER.error("hSet execute failed. {} {}", list);
            return false;
        }
    }

    public <T> T hGet(Long id, Class<T> clazz) {
        byte[] idBytes = String.valueOf(id).getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = clazz.getName().toUpperCase().getBytes(StandardCharsets.UTF_8);
        byte[] bytes = this.getResource().hget(keyBytes, idBytes);
        if (null != bytes && bytes.length != 0) {
            Kryo kryo = this.getKryo(clazz);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            return kryo.readObject(input, clazz);
        } else {
            return null;
        }
    }

    public <T> List<T> hGet(Class<T> clazz, List<String> ids) {
        Pipeline pipelined = this.getResource().pipelined();
        List<T> list = Lists.newArrayList();
        ids.forEach((id) -> {
            byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);
            byte[] keyBytes = clazz.getName().toUpperCase().getBytes(StandardCharsets.UTF_8);
            Response<byte[]> hget = pipelined.hget(keyBytes, idBytes);
            pipelined.sync();
            byte[] bytes = (byte[])hget.get();
            if (null != bytes && bytes.length > 0) {
                Kryo kryo = this.getKryo(clazz);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream);
                list.add(kryo.readObject(input, clazz));
            }

        });
        pipelined.sync();
        pipelined.clear();

        pipelined.close();

        return list;
    }

    public boolean hmSet(String key, String field, List<String> data) {
        byte[] keyBytes = key.getBytes();
        byte[] filedBytes = field.getBytes();
        Kryo kryo = this.getKryoToList(String.class);
        byte[] dataBytes = this.getOutputBytes(kryo, data);
        Map<byte[], byte[]> fs = Maps.newHashMap();
        fs.put(filedBytes, dataBytes);
        this.getResource().hmset(keyBytes, fs);
        return true;
    }

    public boolean hmSet(String key, String field, String data) {
        byte[] keyBytes = key.getBytes();
        byte[] filedBytes = field.getBytes();
        byte[] dataBytes = data.getBytes();
        Map<byte[], byte[]> fs = Maps.newHashMap();
        fs.put(filedBytes, dataBytes);
        this.getResource().hmset(keyBytes, fs);
        return true;
    }

    public String hmGetString(String key, String field) {
        List<byte[]> bytes = this.hmGetBytes(key, field);
        if (bytes != null && !bytes.isEmpty()) {
            byte[] bytes1 = (byte[])bytes.get(0);
            return bytes1 != null && bytes1.length != 0 ? new String(bytes1) : null;
        } else {
            return null;
        }
    }

    private List<byte[]> hmGetBytes(String key, String field) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] filedBytes = field.getBytes(StandardCharsets.UTF_8);
        this.getResource().hmget(keyBytes, new byte[][]{filedBytes});
        return this.getResource().hmget(keyBytes, new byte[][]{filedBytes});
    }

    public List<String> hmGet(String key, String field) {
        List<byte[]> bytes = this.hmGetBytes(key, field);
        if (bytes != null && !bytes.isEmpty()) {
            List<String> data = Lists.newArrayList();
            Class<? extends List> clazz = data.getClass();
            byte[] bytes1 = (byte[])bytes.get(0);
            if (bytes1 != null && bytes1.length != 0) {
                Kryo kryo = this.getKryoToList(String.class);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes1);
                Input input = new Input(byteArrayInputStream);
                List list = (List)kryo.readObject(input, clazz);
                data.addAll(list);
                return data;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void hmDel(String key, String field) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] fieldBytes = field.getBytes(StandardCharsets.UTF_8);
        this.getResource().hdel(keyBytes, new byte[][]{fieldBytes});
    }

    public void expire(String key, int seconds) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (seconds != -1) {
            this.getResource().expire(keyBytes, seconds);
        }

    }

    public void del(String... keys) {
        this.getResource().del(keys);
    }

    public void sadd(String key, String... members) {
        this.getResource().sadd(key, members);
    }

    public void zadd(String key, Double score, String member) {
        this.getResource().zadd(key, score, member);
    }

    public void zadd(String key, Map<String, Double> members) {
        Jedis resource = this.getResource();
        Pipeline pipelined = resource.pipelined();
        members.forEach((m, s) -> {
            pipelined.zadd(key, s, m);
        });
        pipelined.sync();
        pipelined.clear();

        pipelined.close();

    }

    public ICacheCmd select(Integer index) {
        if (index == null) {
            return this;
        } else if (index >= 0 && index <= 15) {
            if (null == this.jedisThreadLocal.get()) {
                this.getResource();
            }

            ((Jedis)this.jedisThreadLocal.get()).select(index);
            return this;
        } else {
            return this;
        }
    }

    public void srem(String key, String... members) {
        this.getResource().srem(key, members);
    }

    public void zrem(String key, String... members) {
        this.getResource().zrem(key, members);
    }

    public Set<String> sinter(String... keys) {
        return this.getResource().sinter(keys);
    }

    public Set<String> zrangeByScore(String key, String min, String max) {
        return this.getResource().zrangeByScore(key, min, max);
    }

    public List<Map.Entry<String, String>> hscan(String key, String pattern, int count) {
        List<Map.Entry<String, String>> result = Lists.newArrayList();
        ScanParams scanParams = (new ScanParams()).count(count).match(pattern);
        String cur = ScanParams.SCAN_POINTER_START;
        do {
            result = this.getResource().hscan(key, cur, scanParams).getResult();
        } while(!cur.equals(ScanParams.SCAN_POINTER_START));
        return result;
    }

    public Integer zcount(String key, String min, String max) {
        return Math.toIntExact(this.getResource().zcount(key, min, max));
    }

    public Set<String> zrange(String key, int start, int end) {
        return this.getResource().zrange(key, (long)start, (long)end);
    }

    public List<String> zrangeWithScores(String key, int start, int end) {
        Set<Tuple> tuples = this.getResource().zrangeWithScores(key, (long)start, (long)end);
        double s = 0.0D;
        int i = 0;
        String[] result = new String[tuples.size()];

        for(Iterator iterator = tuples.iterator(); iterator.hasNext(); ++i) {
            Tuple tuple = (Tuple)iterator.next();
            double score = tuple.getScore();
            String element = tuple.getElement();
            if (i > 0 && score <= s) {
                String v = result[i - 1];
                result[i - 1] = element;
                result[i] = v;
            } else {
                result[i] = element;
            }
        }

        return Lists.newArrayList(result);
    }

    public Set<String> zrevrange(String key, int start, int end) {
        return this.getResource().zrevrange(key, (long)start, (long)end);
    }

    public List<String> zrevrangeWithScores(String key, int start, int end) {
        Set<Tuple> tuples = this.getResource().zrevrangeWithScores(key, (long)start, (long)end);
        ArrayList<String> result = Lists.newArrayList();
        if (null != tuples && tuples.size() > 0) {
            ArrayList<Tuple> list = Lists.newArrayList(tuples);
            list.sort((o1, o2) -> {
                double score = o1.getScore();
                double score1 = o2.getScore();
                return score > score1 ? -1 : (score - score1 == 0.0D ? 0 : 1);
            });
            Iterator iterator = list.iterator();

            while(iterator.hasNext()) {
                Tuple tuple = (Tuple)iterator.next();
                result.add(tuple.getElement());
            }

            return result;
        } else {
            return result;
        }
    }

    public List<String> sscan(String key, String pattern, int count) {
        List<String> result = Lists.newArrayList();
        ScanParams scanParams = (new ScanParams()).count(count).match(pattern);
        String cur = ScanParams.SCAN_POINTER_START;
        do {
            result = this.getResource().sscan(key, cur, scanParams).getResult();
        } while(!cur.equals(ScanParams.SCAN_POINTER_START));

        return result;
    }

    public void hdel(Long id, Class<? extends ICacheAble> clazz) {
        byte[] keyBytes = clazz.getName().toUpperCase().getBytes(StandardCharsets.UTF_8);
        String cachedId = String.valueOf(id);
        byte[] idBytes = cachedId.getBytes(StandardCharsets.UTF_8);
        this.getResource().hdel(keyBytes, new byte[][]{idBytes});
    }

    /**
     *
     * 2020年9月16日10:35:11
     * cc
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean setNx(String key, String value, int seconds) {
        if (StringUtils.isEmpty(key)) {
            return false;
        } else {
            String result = this.getResource().set(key, value, SetParams.setParams().nx().ex(seconds));
            return StringUtils.isNotEmpty(result);
        }
    }
    private Output getOutput(Class<?> clazz, Object entity) {
        Kryo kryo = this.getKryo(clazz);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, entity);
        return output;
    }

    private byte[] getOutputBytes(Kryo kryo, Object data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, data);
        output.flush();
        output.close();
        byte[] dataBytes = byteArrayOutputStream.toByteArray();

        try {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (Exception e) {
        }

        return dataBytes;
    }

    private Kryo getKryo(Class<?> clazz) {
        Kryo kryo = new Kryo();
        kryo.register(clazz);
        Field[] fields = clazz.getDeclaredFields();
        Field[] fields1 = fields;
        int var5 = fields.length;

        for(int i = 0; i < var5; ++i) {
            Field field = fields1[i];
            kryo.register(field.getType());
        }

        return kryo;
    }

    private Kryo getKryoToList(Class<?> clazz) {
        Kryo kryo = new Kryo();
        CollectionSerializer serializer = new CollectionSerializer();
        serializer.setElementClass(clazz, new JavaSerializer());
        serializer.setElementsCanBeNull(false);
        kryo.register(String.class, new JavaSerializer());
        kryo.register(ArrayList.class, serializer);
        kryo.register(clazz);
        Field[] fields = clazz.getDeclaredFields();
        Field[] fields1 = fields;
        int i = fields.length;

        for(int j = 0; j < i; ++j) {
            Field field = fields1[j];
            kryo.register(field.getType());
        }

        return kryo;
    }

    public void freeResource() {
        if (this.getResource() != null) {
            this.getResource().close();
        }

        this.jedisThreadLocal.remove();
    }

    private Jedis getResource() {
        Jedis jedis = (Jedis)this.jedisThreadLocal.get();
        if (null == jedis) {
            jedis = this.nextResource();
        }

        this.jedisThreadLocal.set(jedis);
        return jedis;
    }

    private Jedis getResource(Integer index) {
        Jedis jedis = (Jedis)this.jedisThreadLocal.get();
        jedis.select(index);
        if (null == jedis) {
            jedis = this.nextResource();
        }

        this.jedisThreadLocal.set(jedis);
        return jedis;
    }

    private Jedis nextResource() {
        return jedisPool.getResource();
    }

    public String getRedisHost() {
        return this.redisHost;
    }

    public Integer getRedisPost() {
        return this.redisPost;
    }

    public ThreadLocal<Jedis> getJedisThreadLocal() {
        return this.jedisThreadLocal;
    }

    public void setRedisHost(final String redisHost) {
        this.redisHost = redisHost;
    }

    public void setRedisPost(final Integer redisPost) {
        this.redisPost = redisPost;
    }

    public void setJedisThreadLocal(final ThreadLocal<Jedis> jedisThreadLocal) {
        this.jedisThreadLocal = jedisThreadLocal;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof JedisOwner)) {
            return false;
        } else {
            JedisOwner other = (JedisOwner)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                out: {
                    Object redisHost = this.getRedisHost();
                    Object otherRedisHost = other.getRedisHost();
                    if (redisHost == null) {
                        if (otherRedisHost == null) {
                            break out;
                        }
                    } else if (redisHost.equals(otherRedisHost)) {
                        break out;
                    }

                    return false;
                }

                Object redisPost = this.getRedisPost();
                Object otherRedisPost = other.getRedisPost();
                if (redisPost == null) {
                    if (otherRedisPost != null) {
                        return false;
                    }
                } else if (!redisPost.equals(otherRedisPost)) {
                    return false;
                }

                Object jedisThreadLocal = this.getJedisThreadLocal();
                Object otherJedisThreadLocal = other.getJedisThreadLocal();
                if (jedisThreadLocal == null) {
                    if (otherJedisThreadLocal != null) {
                        return false;
                    }
                } else if (!jedisThreadLocal.equals(otherJedisThreadLocal)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof JedisOwner;
    }

    public int hashCode() {
        int result = 1;
        Object redisHost = this.getRedisHost();
        result = result * 59 + (redisHost == null ? 43 : redisHost.hashCode());
        Object redisPost = this.getRedisPost();
        result = result * 59 + (redisPost == null ? 43 : redisPost.hashCode());
        Object jedisThreadLocal = this.getJedisThreadLocal();
        result = result * 59 + (jedisThreadLocal == null ? 43 : jedisThreadLocal.hashCode());
        return result;
    }

    public String toString() {
        return "JedisOwner(redisHost=" + this.getRedisHost() + ", redisPost=" + this.getRedisPost() + ", jedisThreadLocal=" + this.getJedisThreadLocal() + ")";
    }
}
