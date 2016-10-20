package com.github.reimashi.siloader.data

import java.io.Serializable
import java.util.*

class Ww3Record(lat: Double, lon: Double, time: Date) : Serializable {
    public val latitude: Double =  lat
    public val longitude: Double = lon
    public val time: Date = time

    public var dirm: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var dirp: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var rtp: Double? = null
        get
        set(value) { if (value == -999.9000244140625) field = null else field = value }

    public var tm_10: Double? = null
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
                dirm.toString() + " " +
                dirp.toString() + " " +
                rtp.toString() + " " +
                tm_10.toString() + " "
    }
}