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
import com.slaviboy.weather.features.weather.data.local.entity.Daily
import com.slaviboy.weather.features.weather.data.local.entity.FeelsLike
import com.slaviboy.weather.features.weather.data.local.entity.Temp
import com.slaviboy.weather.features.weather.data.local.entity.relationship.DailyWithRelations

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param dt Time of the forecasted data, Unix, UTC
 * @param sunrise Sunrise time, Unix, UTC
 * @param sunset Sunset time, Unix, UTC
 * @param moonrise The time of when the moon rises for this day, Unix, UTC
 * @param moonset The time of when the moon sets for this day, Unix, UTC
 * @param moonPhase Moon phase. 0 and 1 are 'new moon', 0.25 is 'first quarter moon', 0.5 is 'full moon' and 0.75 is 'last quarter moon'. The periods in between are called 'waxing crescent', 'waxing gibous', 'waning gibous', and 'waning crescent', respectively.
 * @param tempDTO Units – default: kelvin, metric: Celsius, imperial: Fahrenheit
 * @param feelsLikeDTO This accounts for the human perception of weather. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param pressure Atmospheric pressure on the sea level, hPa
 * @param humidity Humidity, %
 * @param dewPoint Atmospheric temperature (varying according to pressure and humidity) below which water droplets begin to condense and dew can form. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
 * @param windSpeed Wind speed. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour.
 * @param windGust Wind gust. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour.
 * @param windDeg Wind direction, degrees (meteorological)
 * @param clouds Cloudiness, %
 * @param uvi The maximum value of UV index for the day
 * @param pop Probability of precipitation
 * @param rain Precipitation volume, mm
 * @param snow Snow volume, mm
 * @param weatherDTO
 */
data class DailyDTO(
    val dt: Long? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val moonrise: Long? = null,
    val moonset: Long? = null,
    @SerializedName("moon_phase") val moonPhase: Double? = null,
    @SerializedName("temp") val tempDTO: TempDTO? = null,
    @SerializedName("feels_like") val feelsLikeDTO: FeelsLikeDTO? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    @SerializedName("dew_point") val dewPoint: Double? = null,
    @SerializedName("wind_speed") val windSpeed: Double? = null,
    @SerializedName("wind_gust") val windGust: Double? = null,
    @SerializedName("wind_deg") val windDeg: Int? = null,
    val clouds: Int? = null,
    val uvi: Double? = null,
    val pop: Double? = null,
    val rain: Double? = null,
    val snow: Double? = null,
    @SerializedName("weather") val weatherDTO: List<WeatherDTO> = listOf()
) {

    fun toDailyEntity(cityId: Int, maskMultiplier: Int): Daily {
        return Daily(null, cityId, maskMultiplier, dt?.times(1000L), sunrise?.times(1000L), sunset?.times(1000L), moonrise?.times(1000L), moonset?.times(1000L), moonPhase, pressure, humidity, dewPoint, windSpeed, windGust, windDeg, clouds, uvi, pop, rain, snow)
    }

    fun toDailyWithRelation(cityId: Int, maskMultiplier: Int): DailyWithRelations {

        val dailyEntity = toDailyEntity(cityId, maskMultiplier)
        val tempEntity = tempDTO?.toTempEntity(cityId, maskMultiplier) ?: Temp()
        val feelsLikeEntity = feelsLikeDTO?.toFeelsLikeEntity(cityId, maskMultiplier) ?: FeelsLike()
        val weatherEntity = weatherDTO.map {
            it.toWeatherEntity(cityId, maskMultiplier)
        }
        return DailyWithRelations(dailyEntity, tempEntity, feelsLikeEntity, weatherEntity)
    }

}