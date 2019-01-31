package com.basalt.camp.base

import com.basalt.camp.CampApplication
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mashape.unirest.http.ObjectMapper
import com.mashape.unirest.http.Unirest
import org.junit.AfterClass
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [CampApplication::class]
)
@RunWith(SpringRunner::class)
class BaseIntegration : BaseTest() {
    companion object {
        var url: String = ""

        @AfterClass
        @JvmStatic
        fun tearDown() {
            Unirest.shutdown()
        }
    }

    @LocalServerPort
    private val serverPort: Int = 0

    init {
        Unirest.setDefaultHeader("Content-Type", "application/json")
        Unirest.setObjectMapper(createObjectMapper())
        Unirest.setTimeouts(10000L, 30 * 60 * 1000L)
    }

    @Before
    fun beforeIT() {
        url = "http://localhost:$serverPort"
    }

    private fun createObjectMapper(): ObjectMapper {
        return object : ObjectMapper {
            private val jacksonObjectMapper =
                com.fasterxml.jackson.databind.ObjectMapper().registerModule(KotlinModule())

            override fun <T> readValue(value: String, valueType: Class<T>): T {
                try {
                    return jacksonObjectMapper.readValue(value, valueType)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }

            override fun writeValue(value: Any): String {
                try {
                    return jacksonObjectMapper.writeValueAsString(value)
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }

            }
        }
    }

    fun apiUrl(path: String): String {
        return BaseIntegration.url + path
    }
}