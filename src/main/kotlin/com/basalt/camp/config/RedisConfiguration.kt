package com.basalt.camp.config

import io.lettuce.core.ReadFrom
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.Utf8StringCodec
import io.lettuce.core.masterslave.MasterSlave
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import redis.embedded.RedisServer
import java.io.IOException
import java.time.Duration
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class RedisConfiguration {

    private val redisPort: Int = 6379

    private var redisServer: RedisServer? = null

    @PostConstruct
    @Throws(IOException::class)
    fun startRedis() {
        redisServer = RedisServer(redisPort)
        redisServer!!.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer!!.stop()
    }

    @Bean(destroyMethod = "close")
    fun redisConnection(): StatefulRedisConnection<String, String> {
        val redisClient = RedisClient.create()

        val connection = MasterSlave.connect(redisClient, Utf8StringCodec(), RedisURI.Builder.redis("localhost", redisPort).build())
        connection.timeout = Duration.ofMillis(500)
        return connection
    }
}