package com.basalt.camp.business.cache

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.stereotype.Component

@Component
class CacheRepository(
        private val redisConnection: StatefulRedisConnection<String, String>,
        private val objectMapper: ObjectMapper
) {
    fun add(key: String, value: Any) {
        redisConnection.sync().set(key, objectMapper.writeValueAsString(value))
    }

    fun <T> getSet(key:String, value: Any, clazz: Class<T>): T? {
        val oldValue = redisConnection.sync().getset(key, objectMapper.writeValueAsString(value))
        return oldValue?.let { objectMapper.readValue(it, clazz) }
    }

    fun setnx(key:String, value: Any): Boolean {
        return redisConnection.sync().setnx(key, objectMapper.writeValueAsString(value))
    }

    fun delete(key: String) {
        redisConnection.sync().del(key)
    }

    fun <T> get(key:String, clazz: Class<T>): T? {
        val strValue = redisConnection.sync().get(key)
        return strValue?.let { objectMapper.readValue(it, clazz) }
    }
}