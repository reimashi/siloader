package com.github.reimashi.siloader.services

import com.github.reimashi.siloader.lang.SpatialPoint
import java.sql.*
import java.util.*
import java.util.Date
import java.util.logging.Level
import java.util.logging.Logger

class DatabaseService(dbUrl: String = "localhost", dbUser: String, dbPass: String, dbTable: String) {
    private val dbUrl: String = dbUrl;
    private val dbUser: String = dbUser;
    private val dbPass: String = dbPass;
    private val dbTable: String = dbTable;

    var connection: Connection? = null
        private set

    /**
     * Inicia el servicio Database
     */
    @Throws(SQLException::class)
    fun start() {
        if (this.connection == null) {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.dbUrl + "/" + this.dbTable + "?useUnicode=true&serverTimezone=UTC",
                    this.dbUser,
                    this.dbPass)
            log.log(Level.INFO, "Cliente DB iniciado. Base de datos: <" + this.dbUrl + ">. Tabla: <" + this.dbTable + ">")
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
            log.log(Level.INFO, "Cliente DB parado. Base de datos: <" + this.dbUrl + ">. Tabla: <" + this.dbTable + ">")
        }
    }

    companion object {
        private val log = Logger.getLogger(DatabaseService::class.java.name)
    }

    fun selectNearby(table: String, sp: SpatialPoint, bean: DatabaseObject): DatabaseObject {
        var lat = sp.latitude;
        var lng = sp.longitude;

        var query: String = "SELECT *, X(position) AS latitude, Y(position) AS longitude, (3959*acos(cos(radians(78.3232))*cos(radians($lat))*cos(radians($lng)-radians(65.3234))+sin(radians(78.3232))*sin(radians($lat)))) AS distance FROM $table ORDER BY distance LIMIT 1;"
        val ps = this.connection?.prepareStatement(query)
        val result: ResultSet = ps!!.executeQuery()

        var fields = bean.getFields().keys.toMutableList();
        fields.add("latitude")
        fields.add("longitude")

        while (result.next()) {
            var loadElems: HashMap<String, String?> = HashMap<String, String?>();

            for (key in fields) {
                loadElems.put(key, result.getString(key));
            }

            bean.loadValues(loadElems)
        }

        return bean
    }

    fun <T : DatabaseObject>selectIterator(table: String, bean: T): DatabaseObjectIterator<T>? {
        var keys: MutableList<String> = bean.getFields().keys.toMutableList();
        var queryKeys: MutableList<String> = bean.getFields().keys.toMutableList();

        if (keys.contains("position")) {
            keys = keys.filterNot { it == "position" }.toMutableList();
            queryKeys = queryKeys.filterNot { it == "position" }.toMutableList();

            keys.add("latitude")
            keys.add("longitude")
            queryKeys.add("X(position) AS latitude")
            queryKeys.add("Y(position) AS longitude")
        }

        val query = "SELECT " + queryKeys.joinToString(",") + " FROM $table"
        try {
            val ps = this.connection?.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            val result: ResultSet = ps!!.executeQuery()
            return DatabaseObjectIterator(result, keys, bean);
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null;
    }

    fun truncate(table: String) {
        var query = "TRUNCATE `" + table + "`;";
        var statement = this.connection!!.prepareStatement(query)
        statement.executeUpdate()
    }

    fun insert(elem: DatabaseObject): Any? {
        var fields = elem.getFields()
        var keys: Array<String> = fields.keys.toTypedArray();
        var values: Array<String> = keys.copyOf();

        var query = "INSERT INTO " + elem.getTable() + " (" + keys.joinToString(",") + ")"

        for ((key, value) in fields) {
            var index = keys.indexOf(key);

            if (value == null) {
                values[index] = "NULL";
            }
            else if (value is DatabaseObject) {
                var result: Any? = insert(value)
                values[index] = if (result != null) result.toString() else "NULL"
            }
            else {
                values[index] = when (value.javaClass.kotlin) {
                    String::class -> "'" + value.toString() + "'"
                    Date::class -> {
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        "'" + sdf.format(value as Date) + "'"
                    }
                    SpatialPoint::class -> "ST_GeomFromText('POINT(" + (value as SpatialPoint).latitude + " " + (value as SpatialPoint).longitude + ")')"
                    else -> value.toString()
                }
            }
        }

        query += " VALUES (" + values.joinToString(",") + ")"

        var statement = this.connection!!.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

        try {
            statement.executeUpdate()
        }
        catch (e: SQLException) {
            log.severe(e.message + "\r\n" + query)
            throw e;
        }

        try {
            var genKeys = statement.generatedKeys;
            genKeys.next();
            return genKeys.getInt(1)
        }
        catch (e: SQLException) {
            return null;
        }
    }
}