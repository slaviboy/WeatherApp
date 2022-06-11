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
import com.slaviboy.weather.core.util.StaticMethods.getDayAsShortString
import com.slaviboy.weather.core.util.StaticMethods.toFahrenheit
import com.slaviboy.weather.core.util.TemperatureType
import kotlin.math.roundToInt

/**
 * Class for the daily info, represent the next 4 day forecast
 * @param minTemperature the minimum temperature for the day
 * @param maxTemperature the maximum temperature for the day
 * @param weatherIconResId the res id for the weather icon
 * @param time long representation of the time from Date().time
 */
data class DailyInfo(
    var minTemperature: Float? = null,
    var maxTemperature: Float? = null,
    var weatherIconResId: Int = R.drawable.ic_none,
    var time: Long? = null
) {

    // get the day as string: Mon, Tue, Wen,..
    val dayShortFormatted get() = time.getDayAsShortString()

    /**
     * Get the min/max temperature as string
     * @param temperatureType the temperature type Celsius, Fahrenheit
     * @return "2°/12°"
     */
    fun getMinMaxTemperatureAsString(temperatureType: TemperatureType): String {

        minTemperature?.let { minTemperature ->
            maxTemperature?.let { maxTemperature ->
                return "${minTemperature.getTemperature(temperatureType)}/${maxTemperature.getTemperature(temperatureType)}"
            }
        }
        return "N/A"

    }

    companion object {

        fun Float?.getTemperature(temperatureType: TemperatureType): String {
            return this.let {
                if (it == null) "N/A°" else {
                    if (temperatureType == TemperatureType.Celsius) {
                        "${it.roundToInt()}°"
                    } else "${it.toFahrenheit()}°"
                }
            }
        }

        fun Float.getTemperature(temperatureType: TemperatureType): String {
            return if (temperatureType == TemperatureType.Celsius) {
                "${this.roundToInt()}°"
            } else "${this.toFahrenheit()}°"
        }
    }
}
