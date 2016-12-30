package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.services.DatabaseObject

class MeasurementRecord : DatabaseObject {
    var location: LocationRecord? = null
    var time: TimeRecord? = null
    var alert: AlertRecord? = null

    var elevation: Double? = null
    var temperature_surface: Double? = null
    var temperature_500mb: Double? = null
    var temperature_850mb: Double? = null
    var temperature_sea_level: Double? = null
    var cloud_cover_high: Double? = null
    var cloud_cover_half: Double? = null
    var cloud_cover_low: Double? = null
    var visibility: Double? = null
    var salinity: Double? = null
    var water_speed_eastward: Double? = null
    var water_speed_northward: Double? = null
    var wave_direction_mean: Double? = null
    var wave_direction_peak: Double? = null
    var wave_period_absolute: Double? = null
    var wave_period_peak: Double? = null
    var snow_level: Double? = null
    var snow_precipitation: Double? = null
    var rain_precipitation: Double? = null
    var humidity: Double? = null
    var wind_direction: Double? = null
    var wind_lon: Double? = null
    var wind_lat: Double? = null
    var wind_gust: Double? = null

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "idAlert" to alert,
            "idTime" to time,
            "idLocation" to location,
            "elevation" to elevation,
            "temperature_surface" to temperature_surface,
            "temperature_500mb" to temperature_500mb,
            "temperature_850mb" to temperature_850mb,
            "temperature_sea_level" to temperature_sea_level,
            "cloud_cover_high" to cloud_cover_high,
            "cloud_cover_half" to cloud_cover_half,
            "cloud_cover_low" to cloud_cover_low,
            "visibility" to visibility,
            "salinity" to salinity,
            "water_speed_eastward" to water_speed_eastward,
            "water_speed_northward" to water_speed_northward,
            "wave_direction_mean" to wave_direction_mean,
            "wave_direction_peak" to wave_direction_peak,
            "wave_period_absolute" to wave_period_absolute,
            "wave_period_peak" to wave_period_peak,
            "snow_level" to snow_level,
            "snow_precipitation" to snow_precipitation,
            "rain_precipitation" to rain_precipitation,
            "humidity" to humidity,
            "wind_direction" to wind_direction,
            "wind_lon" to wind_lon,
            "wind_lat" to wind_lat,
            "wind_gust" to wind_gust
        )
    }

    override fun getTable(): String {
        return "measurement"
    }

    override fun loadValues(values: Map<String, String?>) {
        throw UnsupportedOperationException("not implemented")
    }
}