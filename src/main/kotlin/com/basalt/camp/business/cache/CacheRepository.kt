package com.basalt.camp.business.cache

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.stereotype.Component

@Component
class CacheRepository(
        private val redisConnection: StatefulRedisConnection<String, String>,
        private val objectMapper: ObjectMapper
) {
    fun set(key: String, value: Any) {
        redisConnection.sync().set(key, objectMapper.writeValueAsString(value))
    }

    fun <T> getSet(key:String, value: Any, clazz: Class<T>): T? {
        val oldValue = redisConnection.sync().getset(key, objectMapper.writeValueAsString(value))
        return oldValue?.let { objectMapper.readValue(it, clazz) }
    }

    fun setnx(key:String, value: Any): Boolean {
        return redisConnection.sync().setnx(key, objectMapper.writeValueAsString(value))
    }

    fun msetnx(values: Map<String, Any>): Boolean {
        val strValues = values.mapValues { objectMapper.writeValueAsString(it.value) }
        return redisConnection.sync().msetnx(strValues)
    }

    fun mset(values: Map<String, Any>) {
        val strValues = values.mapValues { objectMapper.writeValueAsString(it.value) }
        redisConnection.sync().mset(strValues)
    }

    fun delete(key: String) {
        redisConnection.async().del(key)
    }

    fun <T> get(key:String, clazz: Class<T>): T? {
        val strValue = redisConnection.sync().get(key)
        return strValue?.let { objectMapper.readValue(it, clazz) }
    }

    fun keys(pattern: String): List<String> {
        return redisConnection.sync().keys(pattern)
    }

    fun expire(key: String, seconds: Long) {
        redisConnection.reactive().expire(key, seconds)
    }
}