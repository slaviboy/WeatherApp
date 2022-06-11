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
import com.slaviboy.weather.features.weather.data.local.entity.FeelsLike

/**
 * DTO (Data Transfer Object) matching expected JSON from Weather API
 * @param morn Morning temperature
 * @param day Day temperature
 * @param eve Evening temperature
 * @param night Night temperature
 */
data class FeelsLikeDTO(
    val morn: Double? = null,
    val day: Double? = null,
    val eve: Double? = null,
    val night: Double? = null
) {

    fun toFeelsLikeEntity(cityId: Int, maskMultiplier: Int): FeelsLike {
        return FeelsLike(null, cityId, cityId.maskToLong(maskMultiplier), morn, day, eve, night)
    }
}