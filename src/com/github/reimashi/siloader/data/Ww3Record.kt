package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.lang.SpatialPoint
import com.github.reimashi.siloader.services.DatabaseObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class Ww3Record(var position: SpatialPoint = SpatialPoint.Null, var time: Date = Date.from(Instant.EPOCH)) : DatabaseObject {

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
        return position.latitude.toString() + "_" +
                position.longitude.toString() + "_" +
                time.time.toString()
    }

    override fun toString(): String {
        return position.toString() + " " +
                time.toString() + " " +
                dirm.toString() + " " +
                dirp.toString() + " " +
                rtp.toString() + " " +
                tm_10.toString() + " "
    }

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
                "position" to position,
                "time" to time,
                "dirm" to dirm,
                "dirp" to dirp,
                "rtp" to rtp,
                "tm_10" to tm_10
        )
    }

    override fun getTable(): String {
        return "ww3"
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
                "dirm" -> this.dirm = tvalue?.toDouble()
                "dirp" -> this.dirp = tvalue?.toDouble()
                "rtp" -> this.rtp = tvalue?.toDouble()
                "tm_10" -> this.tm_10 = tvalue?.toDouble()
            }
        }
    }
}