package com.github.reimashi.siloader.data

import java.io.Serializable
import java.util.*

class MarineRecord(lat: Double, lon: Double, time: Date, depth: Double) : Serializable {
    public val latitude: Double =  lat
    public val longitude: Double = lon
    public val time: Date = time
    public val depth: Double = depth

    public var u: Int? = null
        get
        set(value) { if (value == -32767) field = null else field = value }

    public var v: Int? = null
        get
        set(value) { if (value == -32767) field = null else field = value }

    public var salinity: Int? = null
        get
        set(value) { if (value == -32767) field = null else field = value }

    public fun getID(): String {
        return latitude.toString() + "_" +
                longitude.toString() + "_" +
                time.time.toString() + "_" +
                depth.toString()
    }

    override fun toString(): String {
        return latitude.toString() + " " +
                longitude.toString() + " " +
                time.toString() + " " +
                depth.toString() + " " +
                u.toString() + " " +
                v.toString() + " " +
                salinity.toString() + " "
    }
}