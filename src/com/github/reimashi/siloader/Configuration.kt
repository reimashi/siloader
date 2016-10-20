package com.github.reimashi.siloader

import java.util.*

class Configuration {
    var cacheUrl: String? = null

    var dbUser: String? = null
    var dbPassword: String? = null
    var dbURL: String? = null

    constructor() {
        this.cacheUrl = "redis://localhost"

        this.dbUser = "esei"
        this.dbPassword = "eseipass"
        this.dbURL = "jdbc:mysql://vega/work_si"
    }

    constructor(cacheUrl: String, dbUser: String, dbPassword: String, dbUrl: String) {
        this.cacheUrl = cacheUrl
        
        this.dbUser = dbUser
        this.dbPassword = dbPassword
        this.dbURL = dbUrl
    }

    override fun toString(): String {
        val conf = StringBuilder()
        val EOL = System.getProperty("line.separator")

        conf.append("Redis - Dirección: " + this.cacheUrl + EOL)
        conf.append("DB    - Usuario: " + this.dbUser + EOL)
        conf.append("DB    - Contraseña: " + this.dbPassword + EOL)
        conf.append("DB    - Dirección: " + this.dbURL + EOL)

        return conf.toString()
    }

    companion object {
        fun fromMap(mp: Map<String, String>): Configuration {
            val prop = Properties()
            prop.putAll(mp)
            return Configuration.fromProperties(prop)
        }

        fun fromProperties(p: Properties): Configuration {
            val defaultc = Configuration()

            return Configuration(
                    p.getProperty("wps", defaultc.cacheUrl),
                    p.getProperty("db.user", defaultc.dbUser),
                    p.getProperty("db.password", defaultc.dbPassword),
                    p.getProperty("db.url", defaultc.dbURL))
        }
    }
}