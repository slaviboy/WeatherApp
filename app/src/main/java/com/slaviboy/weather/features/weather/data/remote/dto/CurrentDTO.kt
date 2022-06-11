/*
* Copyright (C) 2022 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.slaviboy.weather.features.weather.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.slaviboy.weather.features.weather.data.local.entity.Current
import com.slaviboy.weather.features.weather.data.local.entity.relationship.CurrentWithRelations

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param dt Current time, Unix, UTC
 * @param sunrise Sunrise time, Unix, UTC
 * @param sunset Sunset time, Unix, UTC
 * @param temp Temperature. Units - default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param feelsLike Temperature. This temperature parameter accounts for the human perception of weather. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param pressure Atmospheric pressure on the sea level, hPa
 * @param humidity Humidity, %
 * @param dewPoint Atmospheric temperature (varying according to pressure and humidity) below which water droplets begin to condense and dew can form. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param clouds Cloudiness, %
 * @param uvi Current UV index
 * @param visibility Average visibility, metres
 * @param windSpeed Wind speed. Wind speed. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour.
 * @param windGust Wind gust. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour.
 * @param windDeg Wind direction, degrees (meteorological)
 */
data class CurrentDTO(
    val dt: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double? = null,
    @SerializedName("feels_like") val feelsLike: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    @SerializedName("dew_point") val dewPoint: Double? = null,
    val clouds: Int? = null,
    val uvi: Double? = null,
    val visibility: Int? = null,
    @SerializedName("wind_speed") val windSpeed: Double? = null,
    @SerializedName("wind_gust") val windGust: Double? = null,
    @SerializedName("wind_deg") val windDeg: Int? = null,
    @SerializedName("snow") val snowDTO: SnowDTO? = null,
    @SerializedName("rain") val rainDTO: RainDTO? = null,
    @SerializedName("weather") val weatherDTO: List<WeatherDTO> = listOf()
) {

    fun toCurrentEntity(cityId: Int, maskMultiplier: Int): Current {
        return Current(cityId, maskMultiplier, dt?.times(1000L), sunrise?.times(1000L), sunset?.times(1000L), temp, feelsLike, pressure, humidity, dewPoint, clouds, uvi, visibility, windSpeed, windDeg)
    }

    fun toCurrentWithRelations(cityId: Int, maskMultiplier: Int): CurrentWithRelations {
        val currentEntity = toCurrentEntity(cityId, maskMultiplier)
        val weatherEntity = weatherDTO.map {
            it.toWeatherEntity(cityId, maskMultiplier)
        }
        return CurrentWithRelations(currentEntity, weatherEntity)
    }

}

