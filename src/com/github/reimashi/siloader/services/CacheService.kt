package com.github.reimashi.siloader.services

import com.github.reimashi.siloader.Configuration
import com.lambdaworks.redis.RedisClient
import com.lambdaworks.redis.RedisConnectionException
import com.lambdaworks.redis.api.StatefulRedisConnection
import java.util.logging.Level
import java.util.logging.Logger

class CacheService(private val config: Configuration) {
    val redisClient = RedisClient.create(config.cacheUrl)
    var connection: StatefulRedisConnection<String, String>? = null

    /**
     * Inicia el servicio Database
     */
    @Throws(RedisConnectionException::class)
    fun start() {
        if (this.connection == null) {
            this.connection = redisClient.connect()
            log.log(Level.INFO, "Cliente de cache iniciado. <" + this.config.cacheUrl + ">")
        }
    }

    /**
     * Para el servicio Database
     */
    fun stop() {
        if (this.connection != null) {
            this.connection!!.close()
            this.connection = null
            log.log(Level.INFO, "Cliente de cache parado.")
        }
    }

    companion object {
        private val log = Logger.getLogger(DatabaseService::class.java.name)
    }
}