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
import com.slaviboy.weather.features.weather.data.local.entity.Hourly
import com.slaviboy.weather.features.weather.data.local.entity.relationship.HourlyWithRelations

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param dt Time of the forecasted data, Unix, UTC
 * @param temp Temperature. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param feelsLike Temperature. This accounts for the human perception of weather. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param pressure Atmospheric pressure on the sea level, hPa
 * @param humidity Humidity, %
 * @param dewPoint  Atmospheric temperature (varying according to pressure and humidity) below which water droplets begin to condense and dew can form. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param uvi UV index
 * @param clouds Cloudiness, %
 * @param visibility Average visibility, metres
 * @param windSpeed Wind speed. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour
 * @param windGust Wind gust. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour.
 * @param windDeg Wind direction, degrees (meteorological)
 * @param pop Probability of precipitation
 */
data class HourlyDTO(
    val dt: Long? = null,
    val temp: Double? = null,
    @SerializedName("feels_like") val feelsLike: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    @SerializedName("dew_point") val dewPoint: Double? = null,
    val uvi: Double? = null,
    val clouds: Int? = null,
    val visibility: Int? = null,
    @SerializedName("wind_speed") val windSpeed: Double? = null,
    @SerializedName("wind_gust") val windGust: Double? = null,
    @SerializedName("wind_deg") val windDeg: Int? = null,
    val pop: Double? = null,
    @SerializedName("snow") val snowDTO: SnowDTO? = null,
    @SerializedName("rain") val rainDTO: RainDTO? = null,
    @SerializedName("weather") val weatherDTO: List<WeatherDTO> = listOf()
) {

    fun toHourlyEntity(cityId: Int, maskMultiplier: Int): Hourly {
        return Hourly(null, cityId, maskMultiplier, dt?.times(1000L), temp, feelsLike, pressure, humidity, dewPoint, uvi, clouds, visibility, windSpeed, windGust, windDeg, pop)
    }

    fun toHourlyWithRelation(cityId: Int, maskMultiplier: Int): HourlyWithRelations {
        val hourlyEntity = toHourlyEntity(cityId, maskMultiplier)
        val weatherEntity = weatherDTO.map {
            it.toWeatherEntity(cityId, maskMultiplier)
        }
        return HourlyWithRelations(hourlyEntity, weatherEntity)
    }

}