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

import com.slaviboy.weather.core.util.StaticMethods.toFahrenheit
import com.slaviboy.weather.core.util.TemperatureType

/**
 * Class for the comfort level box
 * @param humidity the current weather humidity [0,100]
 * @param feelsLike the temperature of how the weather feels like
 * @param uvIndex the uv(ultraviolet) index
 */
data class ComfortLevelInfo(
    var humidity: Float = 0f,
    var feelsLike: Float? = null,
    var uvIndex: Float? = null
) {

    val minHumidity = 0f
    val maxHumidity = 100f

    /**
     * Get the data from the info class as list of strings
     * @param temperatureType the temperature type Celsius, Fahrenheit
     * @return ("32 °C", "1.3 uv")
     */
    fun getDataAsString(temperatureType: TemperatureType): List<String> {

        val degree = feelsLike.let {
            if (it == null) "N/A" else {
                if (temperatureType == TemperatureType.Celsius) {
                    "$it °C"
                } else "${it.toFahrenheit()} °F"
            }
        }

        val uvIndex = uvIndex.let {
            if (it == null) "N/A" else {
                "$it uv"
            }
        }

        return listOf(degree, uvIndex)
    }
}
