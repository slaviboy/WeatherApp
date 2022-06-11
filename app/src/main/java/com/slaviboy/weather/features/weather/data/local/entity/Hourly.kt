package com.slaviboy.weather.features.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.weather.core.util.StaticMethods.maskToLong

@Entity(tableName = "hourly")
data class Hourly(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    var cityId: Int = 0,
    var maskMultiplier: Int = 0,

    val dt: Long? = null,
    val temp: Double? = null,
    val feelsLike: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val dewPoint: Double? = null,
    val uvi: Double? = null,
    val clouds: Int? = null,
    val visibility: Int? = null,
    val windSpeed: Double? = null,
    val windGust: Double? = null,
    val windDeg: Int? = null,
    val pop: Double? = null,

    val weatherId_: Long = cityId.maskToLong(maskMultiplier)
)