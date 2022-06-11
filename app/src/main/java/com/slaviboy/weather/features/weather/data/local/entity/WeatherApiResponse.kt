package com.slaviboy.weather.features.weather.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_response")
data class WeatherApiResponse(

    @PrimaryKey(autoGenerate = false)
    var cityId: Int,
    var cityName: String,

    val lat: Double? = null,
    val lon: Double? = null,
    val timezone: String? = null,
    val timezoneOffset: Int? = null,

    var currentId_: Int = cityId,
    var dailyId_: Int = cityId,
    var hourlyId_: Int = cityId
)