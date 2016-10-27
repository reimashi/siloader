package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.lang.SpatialPoint
import com.github.reimashi.siloader.services.DatabaseObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class WrfRecord(var position: SpatialPoint = SpatialPoint.Null, var time: Date = Date.from(Instant.EPOCH)) : DatabaseObject {

    /**
     * Altura del terreno ?
     */
    public var topo: Double? = null
        get
        set(value) { if (value == -2.384185791015625E-7) field = null else field = value }

    /**
     * Temperatura en superficie
     */
    public var temp: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value == null) field = null else field = (value - 275.25) }

    /**
     * Temperatura a 500mb
     */
    public var t500: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value == null) field = null else field = (value - 275.25) }

    /**
     * Temperatura a 850mb
     */
    public var t850: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value == null) field = null else field = (value - 275.25) }

    /**
     * Temperatura al nivel del mar
     */
    public var sst: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value == null) field = null else field = (value - 275.25) }

    /**
     * Cubierta nubosa - Gran altura
     */
    public var chf: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    /**
     * Cubierta nubosa - Media altura
     */
    public var cfm: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    /**
     * Cubierta nubosa - Baja altura
     */
    public var cfl: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var visibility: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var snow_level: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value!! < 0.0) field = 0.0 else field = value }

    public var snow_prec: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value!! < 0.0) field = 0.0 else field = value }

    public var prec: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else if (value!! < 0.0) field = 0.0 else field = value }

    public var humidity: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var wind_dir: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var wind_lon: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var wind_lat: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    // Rafagas de viento
    public var wind_gust: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public fun getID(): String {
        return position.toString() + "_" +
                time.time.toString()
    }

    override fun toString(): String {
        return position.toString() + " " +
            time.toString() + " " +
            topo.toString() + " " +
            temp.toString() + " " +
            t500.toString() + " " +
            t850.toString() + " " +
            sst.toString() + " " +
            chf.toString() + " " +
            cfm.toString() + " " +
            cfl.toString() + " " +
            visibility.toString() + " " +
            snow_level.toString() + " " +
            snow_prec.toString() + " " +
            prec.toString() + " " +
            humidity.toString() + " " +
            wind_dir.toString() + " " +
            wind_lon.toString() + " " +
            wind_lat.toString() + " " +
            wind_gust.toString() + " "
    }

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "position" to position,
            "time" to time,
            "topo" to topo,
            "temp" to temp,
            "t500" to t500,
            "t850" to t850,
            "sst" to sst,
            "chf" to chf,
            "cfm" to cfm,
            "cfl" to cfl,
            "visibility" to visibility,
            "snow_level" to snow_level,
            "snow_prec" to snow_prec,
            "prec" to prec,
            "humidity" to humidity,
            "wind_dir" to wind_dir,
            "wind_lon" to wind_lon,
            "wind_lat" to wind_lat,
            "wind_gust" to wind_gust
        )
    }

    override fun getTable(): String {
        return "wrf"
    }

    override fun loadValues(values: Map<String, String?>) {
        if (values.containsKey("latitude") && values.containsKey("longitude")) {
            this.position = SpatialPoint(values.get("latitude")!!.toDouble(), values.get("longitude")!!.toDouble())
        }

        for ((key, value) in values) {
            var tvalue = value;
            if (value?.toLowerCase() == "null") tvalue = null;

            when (key) {
                "time" -> {
                    val df = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    this.time = df.parse(tvalue)
                }
                "topo" -> this.topo = tvalue?.toDouble()
                "temp" -> this.temp = tvalue?.toDouble()
                "t500" -> this.t500 = tvalue?.toDouble()
                "t850" -> this.t850 = tvalue?.toDouble()
                "sst" -> this.sst = tvalue?.toDouble()
                "chf" -> this.chf = tvalue?.toDouble()
                "cfm" -> this.cfm = tvalue?.toDouble()
                "cfl" -> this.cfl = tvalue?.toDouble()
                "visibility" -> this.visibility = tvalue?.toDouble()
                "snow_level" -> this.snow_level = tvalue?.toDouble()
                "snow_prec" -> this.snow_prec = tvalue?.toDouble()
                "prec" -> this.prec = tvalue?.toDouble()
                "humidity" -> this.humidity = tvalue?.toDouble()
                "wind_dir" -> this.wind_dir = tvalue?.toDouble()
                "wind_lon" -> this.wind_dir = tvalue?.toDouble()
                "wind_lat" -> this.wind_dir = tvalue?.toDouble()
                "wind_gust" -> this.wind_dir = tvalue?.toDouble()
            }
        }
    }
}