package com.basalt.camp

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import redis.embedded.RedisServer
import redis.embedded.RedisServerBuilder

private val LOG = LoggerFactory.getLogger(CampApplication::class.java)
private var redisServer: RedisServer? = null


@SpringBootApplication
@ComponentScan("com.basalt.camp")
class CampApplication

@EventListener(classes = [ContextStoppedEvent::class])
fun stopHandler() {
	redisServer?.let {
		try {
			LOG.info("Stopping redis")
			it.stop()
		} catch (e: Exception) {
			LOG.error("Failed to stop redis", e)
		}
	}
}

fun main(args: Array<String>) {
	LOG.info("Starting application")
	val builder = RedisServerBuilder()
	builder.port(6379)
	builder.setting("maxmemory 64M").setting("bind 127.0.0.1")

	redisServer = builder.build()
	redisServer!!.start()

	runApplication<CampApplication>(*args)
}
