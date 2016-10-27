package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.lang.SpatialPoint
import com.github.reimashi.siloader.services.DatabaseObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class MarineRecord(var position: SpatialPoint = SpatialPoint.Null, var time: Date = Date.from(Instant.EPOCH), var depth: Double = 0.0) : DatabaseObject {

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
        return position.latitude.toString() + "_" +
                position.longitude.toString() + "_" +
                time.time.toString() + "_" +
                depth.toString()
    }

    override fun toString(): String {
        return position.toString() + " " +
                time.toString() + " " +
                depth.toString() + " " +
                u.toString() + " " +
                v.toString() + " " +
                salinity.toString() + " "
    }

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "position" to position,
            "time" to time,
            "depth" to depth,
            "u" to u,
            "v" to v,
            "salinity" to salinity
        )
    }

    override fun getTable(): String {
        return "marine"
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
                "depth" -> this.depth = tvalue!!.toDouble()
                "u" -> this.u = tvalue?.toInt()
                "v" -> this.v = tvalue?.toInt()
                "salinity" -> this.salinity = tvalue?.toInt()
            }
        }
    }
}