package com.sancaijia.building.core.model.redis.cache;

import com.sancaijia.building.core.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);
    private static ICacheCmd client;

    private CacheManager() {
    }

    public static synchronized ICacheCmd getJedis() {
        if (null == client) {
            LOGGER.debug("Init Jedis Cache client.");
            JedisOwner jedisOwner = (JedisOwner) SpringContextUtil.getBean(JedisOwner.class);
            client = (ICacheCmd)(new JedisAgent()).bind(jedisOwner);
        }

        return client;
    }
}
