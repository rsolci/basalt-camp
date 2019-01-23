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

    fun delete(key: String) {
        redisConnection.sync().del(key)
    }

    fun <T> get(key:String, clazz: Class<T>): T? {
        val strValue = redisConnection.sync().get(key)
        return strValue?.let { objectMapper.readValue(it, clazz) }
    }
}