package com.sancaijia.building.core.model.redis;

import com.sancaijia.building.core.exception.BaseException;
import com.sancaijia.building.core.exception.BasicExceptionEnums;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisLock {
    private LockManager lockManager;
    private String key;

    private RedisLock(LockManager lockManager,String key){
        this.lockManager = lockManager;
        this.key = key;
    }

    public static RedisLock getInstance(String key,int expire){
        LockManager lockManager = new LockManager(key,expire);
        RedisLock redisLock = new RedisLock(lockManager,key);
        return redisLock;
    }
    public static RedisLock getInstance(String key,String id,int expire){
        String keyStr = String.format("%s_%s", key, id);
        LockManager lockManager = new LockManager(keyStr,expire);
        RedisLock redisLock = new RedisLock(lockManager,keyStr);
        return redisLock;
    }
    public static RedisLock getInstance(String key,Long id,int expire){
        String keyStr = String.format("%s_%s", key, id);
        LockManager lockManager = new LockManager(keyStr,expire);
        RedisLock redisLock = new RedisLock(lockManager,keyStr);
        return redisLock;
    }


    public void lock(){
        log.info("KEY[{}] 准备加锁",key);
        if(!this.lockManager.acquire()){
            log.info("KEY[{}] 加锁失败",key);
            throw new BaseException(BasicExceptionEnums.SYSTEM_ERROR);
        }
        log.info("KEY[{}] 加锁成功",key);
    }
    public void unlock(){
        this.lockManager.release();
        log.info("KEY[{}] 锁已释放",key);
    }


    /**
     * 获取分布式锁
     * @return
     */
    public boolean tyrLock(String key){
        return this.lockManager.tyrLock(key);
    }
}
