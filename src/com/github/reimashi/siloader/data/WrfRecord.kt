package com.github.reimashi.siloader.data

import java.io.Serializable
import java.util.*

class WrfRecord(lat: Double, lon: Double, time: Date) : Serializable {
    public val latitude: Double =  lat
    public val longitude: Double = lon
    public val time: Date = time

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
        return latitude.toString() + "_" +
                longitude.toString() + "_" +
                time.time.toString()
    }

    override fun toString(): String {
        return latitude.toString() + " " +
                longitude.toString() + " " +
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
}