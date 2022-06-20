package com.harmony.lark.spring;

import com.larksuite.oapi.core.IStore;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisStore implements IStore {

    public static final String KEY_PREFIX = "lark:store:";

    private RedisTemplate<String, String> redisTemplate;
    private String prefix;

    public RedisStore(RedisTemplate<String, String> redisTemplate) {
        this(redisTemplate, KEY_PREFIX);
    }

    public RedisStore(RedisTemplate<String, String> redisTemplate, String prefix) {
        this.redisTemplate = redisTemplate;
        this.prefix = prefix;
    }

    @Override
    public String get(String key) throws Exception {
        return redisTemplate.boundValueOps(prefix + key).get();
    }

    @Override
    public void put(String key, String value, int expire, TimeUnit timeUnit) throws Exception {
        redisTemplate.opsForValue().set(prefix + key, value, expire, timeUnit);
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
