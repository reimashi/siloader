package com.github.reimashi.siloader.lang

/**
 * Created by reima on 27/10/2016.
 */
class SpatialPoint(latitude: Double = 0.0, longitude: Double = 0.0) {
    val latitude: Double = latitude;
    val longitude: Double = longitude;

    override fun toString(): String {
        return "Coordinate{" + this.latitude + "," + this.longitude + "}";
    }

    companion object {
        val Null: SpatialPoint = SpatialPoint()
    }
}