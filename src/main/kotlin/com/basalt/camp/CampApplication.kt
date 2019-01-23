package com.basalt.camp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.basalt.camp")
@EnableCaching
class CampApplication

fun main(args: Array<String>) {

	runApplication<CampApplication>(*args)
}
