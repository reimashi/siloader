package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.services.DatabaseObject

class AlertRecord : DatabaseObject {
    var temperature_high: Boolean = false;
    var temperature_half: Boolean = false;
    var temperature_low: Boolean = false;
    var rain_high: Boolean = false;
    var rain_half: Boolean = false;
    var rain_low: Boolean = false;

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "temperature_high" to temperature_high,
            "temperature_half" to temperature_half,
            "temperature_low" to temperature_low,
            "rain_high" to rain_high,
            "rain_half" to rain_half,
            "rain_low" to rain_low
        )
    }

    override fun getTable(): String {
        return "alert"
    }

    override fun loadValues(values: Map<String, String?>) {
        throw UnsupportedOperationException("not implemented")
    }
}