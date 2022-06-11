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
package com.slaviboy.weather.features.weather.domain.repository

import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.core.util.UpdateIntervals

interface SettingsRepository {

    fun getTemperatureType(): TemperatureType
    fun setTemperatureType(temperatureType: TemperatureType)

    fun getTimeFormat(): TimeFormat
    fun setTimeFormat(timeFormat: TimeFormat)

    fun getUpdateIntervals(): UpdateIntervals
    fun setUpdateIntervals(updateIntervals: UpdateIntervals)

    fun getUseCurrentLocation(): Boolean
    fun setUseCurrentLocation(useCurrentLocation: Boolean)

    fun getSelectedCityId(): Int
    fun setSelectedCityId(cityId: Int)

    fun getLanguage(): Language
    fun setLanguage(language: Language)

    fun getShowWindBox(): Boolean
    fun setShowWindBox(showWindBox: Boolean)

    fun getShowSunriseAndSunsetBox(): Boolean
    fun setShowSunriseAndSunsetBox(showSunriseSunsetBox: Boolean)

    fun getShowComfortLevelBox(): Boolean
    fun setShowComfortLevelBox(showComfortLevelBox: Boolean)

    fun getShowNext24HoursForecastBox(): Boolean
    fun setShowNext24HoursForecastBox(showNext24HoursForecastBox: Boolean)

    fun getShowNext4DaysForecastBox(): Boolean
    fun setShowNext4DaysForecastBox(showNext4DaysForecastBox: Boolean)

    fun getEnableAnimation(): Boolean
    fun setEnableAnimation(enableAnimation: Boolean)

    fun getCurrentLocationLat(): Float
    fun setCurrentLocationLat(currentLocationLat: Float)

    fun getCurrentLocationLon(): Float
    fun setCurrentLocationLon(currentLocationLon: Float)

    fun getLastUpdatedTime(): Long
    fun setLastUpdatedTime(lastUpdatedTime: Long)

    companion object {
        const val NEW_YORK_CITY_ID = 5128638
    }
}