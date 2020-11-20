package com.sancaijia.building.core.model.redis;

import com.sancaijia.building.core.model.redis.cache.CacheManager;
import com.sancaijia.building.core.model.redis.cache.ICacheCmd;
import com.sancaijia.building.core.utils.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class LockManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockManager.class);
    private final String key;
    private final String value;
    private final int expire;
    private static final int TRY_LOCK_INTERVAL_MILLIS = 1000;
    private static final int DEFAULT_LOCK_TIMEOUT = 5;
    private static final int MAX_TRY_LOCK_COUNT = 15;
    private static final int MAX_LOCK_TIMEOUT = 20;
    private static final String KEY_PREFIX = "LOCK_";
    private int tryCount;

    public LockManager(String key) {
        this.key = "LOCK_" + key;
        this.expire = 5;
        this.value = IdUtil.nextStringId();
        this.tryCount = 0;
    }

    public LockManager(String key, int expire) {
        this.key = "LOCK_" + key;
        if (expire <= 0) {
            expire = 5;
        } else if (expire > 20) {
            expire = 20;
        }

        this.expire = expire;
        this.value = IdUtil.nextStringId();
        this.tryCount = 0;
    }

    private boolean tryAcquire() {
        return CacheManager.getJedis().setNx(this.key, this.value, this.expire);
    }

    public boolean acquire() {
        while(++this.tryCount <= 15) {
            boolean success = this.tryAcquire();
            if (success) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(1000L);
            } catch (InterruptedException var3) {
                LOGGER.error(var3.getMessage());
                return false;
            }
        }

        return false;
    }

    public void release() {
        ICacheCmd client = CacheManager.getJedis();
        String existValue = client.get(this.key);
        if (StringUtils.isNotEmpty(existValue) && StringUtils.equals(existValue, this.value)) {
            client.del(this.key);
        }
    }
    public String getValue() {
        return this.value;
    }


    public  boolean tyrLock(String key) {
        ICacheCmd client = CacheManager.getJedis();
        String existValue = client.get(key);
        if (StringUtils.isNotEmpty(existValue) && StringUtils.equals(existValue, this.value)) {
            return true;
        }
        return false;
    }
}
