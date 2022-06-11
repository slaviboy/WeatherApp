package com.slaviboy.weather.features.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.weather.core.util.StaticMethods.maskToLong

@Entity(tableName = "current")
data class Current(

    @PrimaryKey(autoGenerate = false)
    var cityId: Int,
    var maskMultiplier: Int,

    val dt: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double? = null,
    val feelsLike: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val dewPoint: Double? = null,
    val clouds: Int? = null,
    val uvi: Double? = null,
    val visibility: Int? = null,
    val windSpeed: Double? = null,
    val windDeg: Int? = null,

    val weatherId_: Long = cityId.maskToLong(maskMultiplier),
) {
}