package com.basalt.camp.config

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.util.SocketUtils
import org.springframework.util.StringUtils
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres
import ru.yandex.qatools.embed.postgresql.distribution.Version
import java.io.IOException
import java.net.ServerSocket
import java.nio.file.Paths

@Configuration
@EnableConfigurationProperties(DataSourceProperties::class)
class DatabaseConfiguration {
    companion object {
        private val log = LoggerFactory.getLogger(DatabaseConfiguration::class.java)
    }

    private val globalPostgres = EmbeddedPostgres(Version.Main.V10)

    @Bean
    @Primary
    fun dataSource(properties: DataSourceProperties) : HikariDataSource {
        val port = getPostgresPort()
        log.info("Stating postgres on port $port")

        val cachedRuntimeConfig =
            EmbeddedPostgres.cachedRuntimeConfig(Paths.get(System.getProperty("java.io.tmpdir"), "embedded-postgres-movile-pay"))
        val host = "localhost"
        val dbName = "basaltcamp"
        val username = "basalt"
        val password = "changethis"
        val fullConnUrl = globalPostgres.start(cachedRuntimeConfig,
            host,
            port,
            dbName,
            username,
            password,
            emptyList()
        )

        properties.url = "jdbc:postgresql://$host:$port/$dbName"
        properties.username = username
        properties.password = password

        val dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
        if (StringUtils.hasText(properties.name)) {
            dataSource.poolName = properties.name
        }
        return dataSource
    }

    private fun getPostgresPort(): Int {
        return try {
            val port = 50001
            ServerSocket(port).close()
            port
        } catch (pError: IOException) {
            SocketUtils.findAvailableTcpPort()
        }
    }
}