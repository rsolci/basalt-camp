package com.basalt.camp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CampApplication

fun main(args: Array<String>) {
	runApplication<CampApplication>(*args)
}

