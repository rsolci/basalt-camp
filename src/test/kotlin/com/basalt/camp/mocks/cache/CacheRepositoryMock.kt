package com.basalt.camp.mocks.cache

import com.basalt.camp.business.cache.CacheRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.api.StatefulRedisConnection
import org.mockito.Mockito

class CacheRepositoryMock: CacheRepository(Mockito.mock(StatefulRedisConnection::class.java) as StatefulRedisConnection<String, String>, Mockito.mock(ObjectMapper::class.java)) {
    override fun set(key: String, value: Any) {
    }

    override fun <T> getSet(key: String, value: Any, clazz: Class<T>): T? {
        return clazz.cast(value)
    }

    override fun setnx(key: String, value: Any): Boolean {
        return true
    }

    override fun msetnx(values: Map<String, Any>): Boolean {
        return true
    }

    override fun mset(values: Map<String, Any>) {
    }

    override fun delete(key: String) {
    }

    override fun <T> get(key: String, clazz: Class<T>): T? {
        return null
    }

    override fun keys(pattern: String): List<String> {
        return emptyList()
    }

    override fun expire(key: String, seconds: Long) {
    }
}