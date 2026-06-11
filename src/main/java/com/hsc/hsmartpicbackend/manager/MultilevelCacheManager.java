package com.hsc.hsmartpicbackend.manager;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class MultilevelCacheManager {

    private final Cache<String, String> localCache;

    @Resource  // 改为 @Resource
    private StringRedisTemplate stringRedisTemplate;  // 字段注入

    public MultilevelCacheManager() {
        // 本地缓存初始化（不需要依赖外部 Bean，放在构造器里没问题）
        this.localCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    /**
     * 多级缓存获取方法
     * @param key 缓存键
     * @param clazz 返回结果的类型（用于反序列化）
     * @param dbLoader 数据库加载函数（当缓存未命中时执行）
     * @param redisTtlSeconds Redis 缓存的过期时间（秒）
     * @return 缓存或数据库加载的结果
     */
    public Object get(String key, Class<?> clazz, Supplier<?> dbLoader, int redisTtlSeconds) {
        // 1. 本地缓存
        String cachedValue = localCache.getIfPresent(key);
        if (cachedValue != null) {
            return JSONUtil.toBean(cachedValue, clazz);
        }

        // 2. Redis 缓存
        cachedValue = stringRedisTemplate.opsForValue().get(key);
        if (cachedValue != null) {
            localCache.put(key, cachedValue);
            return JSONUtil.toBean(cachedValue, clazz);
        }

        // 3. 数据库加载
        Object result = dbLoader.get();
        if (result == null) {
            return null;
        }

        // 4. 回写缓存
        String jsonValue = JSONUtil.toJsonStr(result);
        localCache.put(key, jsonValue);
        stringRedisTemplate.opsForValue().set(key, jsonValue, redisTtlSeconds, TimeUnit.SECONDS);

        return result;
    }

    public void evict(String key) {
        localCache.invalidate(key);
        stringRedisTemplate.delete(key);
    }
}