package com.github.reimashi.siloader.services

import com.github.reimashi.siloader.Configuration
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.logging.Level
import java.util.logging.Logger

class DatabaseService(private val config: Configuration) {

    /**
     * Obtiene la configuración del servidor.

     * @return Objeto de configuración.
     */
    var connection: Connection? = null
        private set

    /**
     * Inicia el servicio Database
     */
    @Throws(SQLException::class)
    fun start() {
        if (this.connection == null) {
            this.connection = DriverManager.getConnection(
                    this.config.dbURL + "?useUnicode=true&serverTimezone=UTC",
                    this.config.dbUser,
                    this.config.dbPassword)
            log.log(Level.INFO, "Cliente DB iniciado. <" + this.config.dbURL + ">")
        }
    }

    /**
     * Para el servicio Database
     */
    @Throws(SQLException::class)
    fun stop() {
        if (this.connection != null) {
            this.connection!!.close()
            this.connection = null
            log.log(Level.INFO, "Cliente DB parado.")
        }
    }

    companion object {
        private val log = Logger.getLogger(DatabaseService::class.java.name)
    }
}