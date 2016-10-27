package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.services.DatabaseObject

data class LocationRecord(val latitude: Double = 0.0, val longitude: Double = 0.0) : DatabaseObject {

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )
    }

    override fun getTable(): String {
        return "location"
    }

    override fun loadValues(values: Map<String, String?>) {
        throw UnsupportedOperationException("not implemented")
    }
}