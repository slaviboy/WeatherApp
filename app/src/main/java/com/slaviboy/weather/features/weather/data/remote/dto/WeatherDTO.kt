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

import com.slaviboy.weather.core.util.StaticMethods.maskToLong
import com.slaviboy.weather.features.weather.data.local.entity.Weather

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param id Weather condition id
 * @param main Group of weather parameters (Rain, Snow, Extreme etc.)
 * @param description Weather condition within the group. You can get the output in your language.
 * @param icon Weather icon id
 */
data class WeatherDTO(
    val id: Int = 0,
    val main: String = "None",
    val description: String = "no description",
    val icon: String = "01d"
) {
    fun toWeatherEntity(cityId: Int, maskMultiplier: Int): Weather {
        return Weather(null, cityId, cityId.maskToLong(maskMultiplier), id, main, description, icon)
    }
}