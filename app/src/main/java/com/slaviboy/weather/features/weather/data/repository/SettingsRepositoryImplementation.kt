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
package com.slaviboy.weather.features.weather.data.repository

import android.content.SharedPreferences
import com.slaviboy.weather.core.util.Language
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.core.util.UpdateIntervals
import com.slaviboy.weather.features.weather.data.remote.WeatherApi
import com.slaviboy.weather.features.weather.domain.repository.SettingsRepository
import com.slaviboy.weather.features.weather.domain.repository.SettingsRepository.Companion.NEW_YORK_CITY_ID
import javax.inject.Inject

class SettingsRepositoryImplementation @Inject constructor(
    private val preferences: SharedPreferences
) : SettingsRepository {

    override fun getTemperatureType(): TemperatureType {
        return TemperatureType.fromInt(preferences.getInt("temperature", TemperatureType.Celsius.value))
    }

    override fun setTemperatureType(temperatureType: TemperatureType) {
        preferences.edit()?.putInt("temperature", temperatureType.value)?.apply()
    }

    override fun getTimeFormat(): TimeFormat {
        return TimeFormat.fromInt(preferences.getInt("timeFormat", TimeFormat.AmPm.value))
    }

    override fun setTimeFormat(timeFormat: TimeFormat) {
        preferences.edit()?.putInt("timeFormat", timeFormat.value)?.apply()
    }

    override fun getUpdateIntervals(): UpdateIntervals {
        return UpdateIntervals.fromInt(preferences.getInt("updateIntervals", UpdateIntervals.Every6Hours.value))
    }

    override fun setUpdateIntervals(updateIntervals: UpdateIntervals) {
        preferences.edit()?.putInt("updateIntervals", updateIntervals.value)?.apply()
    }

    override fun getUseCurrentLocation(): Boolean {
        return preferences.getBoolean("useCurrentLocation", false)
    }

    override fun setUseCurrentLocation(useCurrentLocation: Boolean) {
        preferences.edit()?.putBoolean("useCurrentLocation", useCurrentLocation)?.apply()
    }

    override fun getSelectedCityId(): Int {
        return preferences.getInt("selectedCityId", NEW_YORK_CITY_ID)
    }

    override fun setSelectedCityId(cityId: Int) {
        preferences.edit()?.putInt("selectedCityId", cityId)?.apply()
    }

    override fun getLanguage(): Language {
        val language = Language.fromInt(preferences.getInt("language", 0))

        // set the API language
        WeatherApi.setSupportedLanguageAPI(language)

        return language
    }

    override fun setLanguage(language: Language) {

        // set the API language
        WeatherApi.setSupportedLanguageAPI(language)

        preferences.edit()?.putInt("language", language.value)?.apply()
    }

    override fun getShowWindBox(): Boolean {
        return preferences.getBoolean("showWindBox", true)
    }

    override fun setShowWindBox(showWindBox: Boolean) {
        preferences.edit()?.putBoolean("showWindBox", showWindBox)?.apply()
    }

    override fun getShowSunriseAndSunsetBox(): Boolean {
        return preferences.getBoolean("showSunriseAndSunsetBox", true)
    }

    override fun setShowSunriseAndSunsetBox(showSunriseSunsetBox: Boolean) {
        preferences.edit()?.putBoolean("showSunriseAndSunsetBox", showSunriseSunsetBox)?.apply()
    }

    override fun getShowComfortLevelBox(): Boolean {
        return preferences.getBoolean("showComfortLevelBox", true)
    }

    override fun setShowComfortLevelBox(showComfortLevelBox: Boolean) {
        preferences.edit()?.putBoolean("showComfortLevelBox", showComfortLevelBox)?.apply()
    }

    override fun getShowNext24HoursForecastBox(): Boolean {
        return preferences.getBoolean("showNext24HoursForecastBox", true)
    }

    override fun setShowNext24HoursForecastBox(showNext24HoursForecastBox: Boolean) {
        preferences.edit()?.putBoolean("showNext24HoursForecastBox", showNext24HoursForecastBox)?.apply()
    }

    override fun getShowNext4DaysForecastBox(): Boolean {
        return preferences.getBoolean("showNext4DaysForecastBox", true)
    }

    override fun setShowNext4DaysForecastBox(showNext4DaysForecastBox: Boolean) {
        preferences.edit()?.putBoolean("showNext4DaysForecastBox", showNext4DaysForecastBox)?.apply()
    }

    override fun getEnableAnimation(): Boolean {
        return preferences.getBoolean("enableAnimation", true)
    }

    override fun setEnableAnimation(enableAnimation: Boolean) {
        preferences.edit()?.putBoolean("enableAnimation", enableAnimation)?.apply()
    }

    override fun getCurrentLocationLat(): Float {
        return preferences.getFloat("currentLocationLat", -1f)
    }

    override fun setCurrentLocationLat(currentLocationLat: Float) {
        preferences.edit()?.putFloat("currentLocationLat", currentLocationLat)?.apply()
    }

    override fun getCurrentLocationLon(): Float {
        return preferences.getFloat("currentLocationLon", -1f)
    }

    override fun setCurrentLocationLon(currentLocationLon: Float) {
        preferences.edit()?.putFloat("currentLocationLon", currentLocationLon)?.apply()
    }

    override fun getLastUpdatedTime(): Long {
        return preferences.getLong("lastUpdatedTime", 0L)
    }

    override fun setLastUpdatedTime(lastUpdatedTime: Long) {
        preferences.edit()?.putLong("lastUpdatedTime", lastUpdatedTime)?.apply()
    }
}