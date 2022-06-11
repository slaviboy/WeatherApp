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

import com.slaviboy.weather.core.util.TemperatureType
import kotlin.math.roundToInt

/**
 * Class for the wind box
 * @param degree the direction of the wind as degrees
 * @param speed the speed of the wind
 */
data class WindInfo(
    var degree: Float? = null,
    var speed: Float? = null
) {

    /**
     * Get the data from the info class as list of strings
     * @param temperatureType the temperature type Celsius (use km/h), Fahrenheit (use mph)
     * @return ("182°", "12 m/h")
     */
    fun getDataAsString(temperatureType: TemperatureType): List<String> {
        return listOf(degree.getDegreeSafe(), speed.getSpeedSafe(temperatureType))
    }

    companion object {

        fun Float?.getSpeedSafe(temperatureType: TemperatureType): String {
            return this.let {
                if (it == null) "N/A" else {
                    if (temperatureType == TemperatureType.Celsius) {
                        "$it m/s"
                    } else "${(it * 2.237f).roundToInt()} mph"
                }
            }
        }

        fun Float?.getDegreeSafe(): String {
            return this.let {
                if (it == null) "N/A" else {
                    "$it °"
                }
            }
        }
    }
}