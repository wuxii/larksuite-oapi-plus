package com.harmony.lark.spring

import com.larksuite.oapi.core.IStore
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

/**
 * @author wuxin
 */
class RedisStore(
    private val redisTemplate: RedisTemplate<String, String>,
    private val prefix: String = "lark:store:"
) : IStore {

    override fun get(key: String): String? {
        return redisTemplate.boundValueOps("$prefix$key").get()
    }

    override fun put(key: String, value: String, expire: Int, timeUnit: TimeUnit) {
        redisTemplate.opsForValue().set("$prefix$key", value, expire.toLong(), timeUnit)
    }

}
