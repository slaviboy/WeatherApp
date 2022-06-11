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
package com.slaviboy.weather.features.weather.domain.model

import com.slaviboy.weather.R
import com.slaviboy.weather.core.util.StaticMethods.getHourAsString
import com.slaviboy.weather.core.util.TemperatureType
import com.slaviboy.weather.core.util.TimeFormat
import com.slaviboy.weather.features.weather.domain.model.DailyInfo.Companion.getTemperature

/**
 * Class for the hourly info, represent the next 24 hours forecast
 * @param temperature temperature for that particular hour
 * @param weatherIconResId the res id for the weather icon
 * @param time long representation of the time from Date().time
 */
data class HourlyInfo(
    var temperature: Float? = null,
    var weatherIconResId: Int = R.drawable.ic_none,
    var time: Long? = null
) {

    /**
     * Get the hour as string
     * @param format the format of the time Am-Pm or 24 Hour format
     */
    fun getHour(format: TimeFormat): String = time.getHourAsString(format)

    /**
     * Get the temperature as string
     * @param temperatureType the temperature type Celsius, Fahrenheit
     * @return "32°"
     */
    fun getTemperatureAsString(temperatureType: TemperatureType): String {
        return temperature.getTemperature(temperatureType)
    }
}