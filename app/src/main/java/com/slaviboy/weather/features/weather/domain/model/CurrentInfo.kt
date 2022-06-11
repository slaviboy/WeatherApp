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
import com.slaviboy.weather.core.util.StaticMethods.getDayAsString

data class CurrentInfo(
    var minTemperature: Float? = null,
    var maxTemperature: Float? = null,
    var currentTemperature: Float? = null,
    var weatherIconResId: Int = R.drawable.ic_none,
    var weatherTitle: String = DEFAULT_WEATHER_TITLE,
    var weatherSubtitle: String = DEFAULT_WEATHER_SUBTITLE,
    var time: Long? = null
) {

    // get the day as string: Monday, Tuesday, Wednesday,..
    val dayFormatted get() = time.getDayAsString()

    companion object {
        const val DEFAULT_WEATHER_TITLE = "None"
        const val DEFAULT_WEATHER_SUBTITLE = "no description"
    }
}