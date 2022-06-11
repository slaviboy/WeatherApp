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

import androidx.compose.ui.graphics.Color

/**
 * Class for the comfort level box
 * @param currentInfo info for the current weather box
 * @param next4DaysForecastInfo info for the next 4 days forecast box
 * @param next24HoursForecastInfo info for the next 24 hours forecast box
 * @param comfortLevelInfo info for the comfort level box
 * @param windInfo info for the wind box
 * @param sunriseSunsetInfo info for the sunrise and sunset box
 */
data class WeatherApiResponseInfo(
    var topBackgroundColor: Color = Color(0xFF041B5D),
    var bottomBackgroundColor: Color = Color(0xFF3B70AD),
    var currentInfo: CurrentInfo = CurrentInfo(),
    var next4DaysForecastInfo: MutableList<DailyInfo> = MutableList(4) { DailyInfo() },
    val next24HoursForecastInfo: MutableList<HourlyInfo> = MutableList(24) { HourlyInfo() },
    val comfortLevelInfo: ComfortLevelInfo = ComfortLevelInfo(),
    val windInfo: WindInfo = WindInfo(),
    val sunriseSunsetInfo: SunriseSunsetInfo = SunriseSunsetInfo()
) {


}