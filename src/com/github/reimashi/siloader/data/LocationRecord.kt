package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.services.DatabaseObject

class LocationRecord : DatabaseObject {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
    }

    override fun getTable(): String {
        return "location"
    }
}