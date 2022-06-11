package com.slaviboy.weather.features.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slaviboy.weather.core.util.StaticMethods
import com.slaviboy.weather.core.util.StaticMethods.maskToLong
import com.slaviboy.weather.core.util.TimeState

@Entity(tableName = "daily")
data class Daily(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    var cityId: Int = 0,
    var maskMultiplier: Int = 0,

    val dt: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val moonrise: Long? = null,
    val moonset: Long? = null,
    val moonPhase: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val dewPoint: Double? = null,
    val windSpeed: Double? = null,
    val windGust: Double? = null,
    val windDeg: Int? = null,
    val clouds: Int? = null,
    val uvi: Double? = null,
    val pop: Double? = null,
    val rain: Double? = null,
    val snow: Double? = null,

    val tempId_: Long = cityId.maskToLong(maskMultiplier),
    val feelsLikeId_: Long = cityId.maskToLong(maskMultiplier),
    val weatherId_: Long = cityId.maskToLong(maskMultiplier)
) {

    fun getTimeState(currentTime: Long, timeZoneOffset: Long): TimeState {
        return StaticMethods.getTimeState(sunrise ?: 0L, sunset ?: 0L, currentTime, timeZoneOffset)
    }
}

