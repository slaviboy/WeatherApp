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
import com.slaviboy.weather.features.weather.data.local.entity.WeatherApiResponse
import com.slaviboy.weather.features.weather.data.local.entity.relationship.CurrentWithRelations
import com.slaviboy.weather.features.weather.data.local.entity.relationship.DailyWithRelations
import com.slaviboy.weather.features.weather.data.local.entity.relationship.HourlyWithRelations
import com.slaviboy.weather.features.weather.data.local.entity.relationship.WeatherApiResponseWithRelations
import com.slaviboy.weather.features.weather.data.remote.WeatherApi.Companion.GEOGRAPHIC_COORDINATES_CITY_ID
import com.slaviboy.weather.features.weather.data.remote.WeatherApi.Companion.GEOGRAPHIC_COORDINATES_CITY_NAME

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param lat Geographical coordinates of the location (latitude)
 * @param lon Geographical coordinates of the location (longitude)
 * @param timezone Timezone name for the requested location
 * @param timezoneOffset Shift in seconds from UTC
 * @param currentDTO Current weather data API response
 * @param dailyDTO Daily forecast for 7 days data API response
 * @param hourlyDTO Hourly forecast for 48 hours weather data API response
 */
data class WeatherApiResponseDTO(

    val lat: Double? = null,
    val lon: Double? = null,
    val timezone: String? = null,
    @SerializedName("timezone_offset") val timezoneOffset: Int? = null,
    @SerializedName("current") val currentDTO: CurrentDTO? = null,
    @SerializedName("daily") val dailyDTO: List<DailyDTO> = listOf(),
    @SerializedName("hourly") val hourlyDTO: List<HourlyDTO> = listOf()
) {

    /**
     * Convert the DTO object from the API response in to a entity object that is used by the DB(database)
     * @param cityId the city id '-1' when used lon|lat with GPS
     * @param cityName the city name 'gps' when used lon|lat with GPS
     */
    fun toCityWeatherWithRelations(cityId: Int = GEOGRAPHIC_COORDINATES_CITY_ID, cityName: String = GEOGRAPHIC_COORDINATES_CITY_NAME): WeatherApiResponseWithRelations {

        val weatherApiResponseEntity = WeatherApiResponse(cityId, cityName, lat, lon, timezone, timezoneOffset)

        // mask city id with [0, CURRENT_MAP_RANGE)
        val currentWithRelations = currentDTO?.toCurrentWithRelations(cityId, 0) ?: CurrentWithRelations(Current(cityId, 0), listOf())

        // set the the ranges for the mapped
        setMappedRange(dailyDTO.size, hourlyDTO.size)

        // mask city id with [CURRENT_MAP_RANGE, DAILY_MAP_RANGE)
        val dailyEntities = arrayListOf<DailyWithRelations>()
        for (i in dailyDTO.indices) {
            val start = CURRENT_MAP_RANGE
            val dailyEntity = dailyDTO[i].toDailyWithRelation(cityId, (start + i))
            dailyEntities.add(dailyEntity)
        }

        // mask city id with [DAILY_MAP_RANGE, HOURLY_MAP_RANGE)
        val hourlyEntities = arrayListOf<HourlyWithRelations>()
        for (i in hourlyDTO.indices) {
            val start = DAILY_MAP_RANGE
            val hourlyEntity = hourlyDTO[i].toHourlyWithRelation(cityId, start + i)
            hourlyEntities.add(hourlyEntity)
        }

        return WeatherApiResponseWithRelations(weatherApiResponseEntity, currentWithRelations, dailyEntities, hourlyEntities)
    }

    fun setMappedRange(numberDaily: Int, numberHourly: Int) {
        DAILY_MAP_RANGE = CURRENT_MAP_RANGE + numberDaily    // 4 days
        HOURLY_MAP_RANGE = DAILY_MAP_RANGE + numberHourly  // 24 hours
    }

    companion object {

        var CURRENT_MAP_RANGE = 1
        var DAILY_MAP_RANGE = 0    // 4 days
        var HOURLY_MAP_RANGE = 0   // 24 hours
    }
}